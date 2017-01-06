package be.storefront.imicloud.web;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.repository.ImBlockRepository;
import be.storefront.imicloud.repository.ImDocumentRepository;
import be.storefront.imicloud.repository.ImageRepository;
import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.service.*;
import be.storefront.imicloud.service.dto.ImBlockDTO;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.dto.ImMapDTO;
import be.storefront.imicloud.service.dto.ImageDTO;
import be.storefront.imicloud.service.mapper.ImBlockMapper;
import be.storefront.imicloud.service.mapper.ImDocumentMapper;
import be.storefront.imicloud.service.mapper.ImageMapper;
import be.storefront.imicloud.web.dom.DomHelper;
import be.storefront.imicloud.web.rest.response.*;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import org.apache.commons.io.IOUtils;
import org.joox.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static org.joox.JOOX.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
@RequestMapping("/upload")
public class UploadController {

    private final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Inject
    private FileStorageService fileStorageService;

    @Inject
    private ImageService imageService;

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private ImDocumentService imDocumentService;

    @Inject
    private ImDocumentRepository imDocumentRepository;

    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private ImCloudSecurity imCloudSecurity;

    @Inject
    private ImMapService imMapService;

    @Inject
    private ImBlockService imBlockService;

    @Inject
    private ImBlockRepository imBlockRepository;

    @Inject
    private ImDocumentMapper imDocumentMapper;

    @Inject
    private ImBlockMapper imBlockMapper;

    @Inject
    private ImageMapper imageMapper;

    @Inject
    private MailService mailService;

    @GetMapping("/")
    public String index() {
        return "upload";
    }


    // TODO call to mark an upload as "finished"!


    @GetMapping("/xml/")
    @Timed
    public
    @ResponseBody
    String checkFileExists(@RequestParam(value = "document_name") String documentName,
                           @RequestParam("access_token") String accessToken,
                           HttpServletResponse response) {

        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

        if (imCloudSecurity.canUserUploadDocuments(uploadingUser)) {
            //return "Document already exists";
            List<ImDocument> docs = imDocumentRepository.findByUserAndDocumentName(uploadingUser.getId(), documentName);

            if (docs.size() > 0) {
                response.setStatus(200);
                return "A document already exists with this name";

            } else {
                response.setStatus(404);
                return "No document with this name yet";
            }

        } else {
            response.setStatus(403);
            return "Access denied";
        }
    }

    @GetMapping("/xml/reprocess")
    @Timed
    public
    @ResponseBody
    String reprocessSavedXmls(HttpServletResponse response) {
        String r = "";

        List<ImDocument> docs = imDocumentRepository.findAll();
        for (ImDocument doc : docs) {
            try {
                doc = processXmlSavedInDocument(doc);
                r += "Document " + doc.getId() + ": OK\n";
            } catch (Exception e) {
                r += "Document " + doc.getId() + ": ERROR: " + e.toString() + "\n";
            }
        }

        return r;
    }


    @PostMapping("/xml/")
    @Timed
    public
    @ResponseBody
    ResponseEntity<ImDocumentResponse> handleXmlFileUpload(@RequestParam(value = "password", required = false) String password, @RequestParam(value = "document_name") String documentName, @RequestParam("xml_file") MultipartFile file,
                                                           @RequestParam("template_code") String templateCode, @RequestParam("access_token") String accessToken,
                                                           RedirectAttributes redirectAttributes, HttpServletRequest request) throws ParserConfigurationException, TransformerException, SAXException, IOException {

        log.debug("XML upload request: {}", file.getOriginalFilename());

        try {
            User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

            if (imCloudSecurity.canUserUploadDocuments(uploadingUser)) {
                if (imCloudSecurity.hasUserAvailableStorage(uploadingUser)) {

                    ImDocumentDTO newDocument = processUploadedDocument(documentName, file, password, templateCode, uploadingUser);

                    ImDocumentCreated imDocumentUploaded = new ImDocumentCreated(newDocument);

                    return ResponseEntity.ok().body(imDocumentUploaded);

                } else {
                    return ResponseEntity.status(400).body(new ImDocumentUploadError(ImDocumentUploadError.ERROR_STORAGE_FULL, "Storage full"));
                }
            } else {
                return ResponseEntity.status(403).body(new ImDocumentUploadError(ImDocumentUploadError.ERROR_ACCESS_DENIED, "Access denied"));

            }
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(new ImDocumentUploadError(ImDocumentUploadError.ERROR_OTHER, ex.getMessage()));
        }
    }

    @Transactional
    private ImDocumentDTO processUploadedDocument(String documentName, MultipartFile file, String password, String templateCode, User user) throws IOException, ParserConfigurationException, SAXException, TransformerException {


        ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
        String xmlString = IOUtils.toString(stream, "UTF-8");

        // Cleanup XML string
        xmlString = xmlString.trim().replaceFirst("^([\\W]+)<", "<");

        String hashedPassword = null;
        if (password != null && password.length() > 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            hashedPassword = passwordEncoder.encode(password);
        }

        // Test XML document
        Document xmlDoc = getXmlDocumentFromString(xmlString);

        // Look for existing document
        List<ImDocument> existingDocs = imDocumentRepository.findByUserAndDocumentName(user.getId(), documentName);

        ImDocument doc;

        if (existingDocs.size() > 0) {
            doc = existingDocs.get(0);

        } else {
            doc = new ImDocument();
            doc.setDocumentName(documentName);
            doc.setUser(user);
        }

        doc.setOriginalXml(xmlString);
        doc.setPassword(hashedPassword);
        doc.setDefaultTemplate(templateCode);

        doc = imDocumentRepository.save(doc);

        doc = processXmlSavedInDocument(doc);

        return imDocumentMapper.imDocumentToImDocumentDTO(doc);
    }

    private Document getXmlDocumentFromString(String xml) throws IOException, SAXException, ParserConfigurationException {
        // Check the XML
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        Document xmlDoc = dBuilder.parse(is);
        //optional, but recommended, read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        xmlDoc.getDocumentElement().normalize();

        return xmlDoc;
    }

    private ImDocument processXmlSavedInDocument(ImDocument doc) throws ParserConfigurationException, SAXException, IOException, TransformerException {

        // Delete old maps - if any
        for (ImMap m : doc.getMaps()) {
            imMapService.delete(m.getId());
        }
        doc.setMaps(null);

        // Process what's in the XML
        Document xmlDoc = getXmlDocumentFromString(doc.getOriginalXml());
        NodeList mapList = xmlDoc.getElementsByTagName("map");

        for (int i = 0; i < mapList.getLength(); i++) {

            Node oneMap = mapList.item(i);
            if (oneMap.getNodeType() == Node.ELEMENT_NODE) {
                Element oneMapElement = (Element) oneMap;

                String mapGuid = oneMapElement.getAttribute("guid");
                String mapLabel = getText(oneMap, "label");

                // Save all maps
                ImMapDTO newMapDto = new ImMapDTO();
                newMapDto.setGuid(mapGuid);
                newMapDto.setImDocumentId(doc.getId());
                newMapDto.setLabel(mapLabel);
                newMapDto.setPosition((float) i);
                newMapDto = imMapService.save(newMapDto);

                NodeList blockList = oneMapElement.getElementsByTagName("block");

                for (int j = 0; j < blockList.getLength(); j++) {
                    Node oneBlock = blockList.item(j);

                    if (oneBlock.getNodeType() == Node.ELEMENT_NODE) {
                        Element oneBlockElement = (Element) oneBlock;

                        String blockGuid = oneBlockElement.getAttribute("guid");
                        String blockLabel = getText(oneBlock, "label");
                        String blockImageSource = getAttributeFromNode(oneBlock, "image", "source");

                        String contentText = null;

                        NodeList contentNodes = oneBlockElement.getElementsByTagName("content");
                        if (contentNodes.getLength() > 0) {
                            Element contentElement = (Element) contentNodes.item(0);

                            contentText = nodeToString(contentElement).trim();
                        }

                        contentText = transformContentToHtml(contentText);

                        // Save all blocks
                        ImBlockDTO newBlockDto = new ImBlockDTO();
                        newBlockDto.setImMapId(newMapDto.getId());
                        newBlockDto.setLabel(blockLabel);
                        newBlockDto.setGuid(blockGuid);
                        newBlockDto.setPosition((float) j);
                        newBlockDto.setContent(contentText);
                        newBlockDto.setLabelImageSource(blockImageSource);

                        newBlockDto = imBlockService.save(newBlockDto);
                    }
                }
            }


//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());

//                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//
//                        Element eElement = (Element) nNode;
//
//                        System.out.println("Staff id : " + eElement.getAttribute("id"));
//                        System.out.println("First Name : " + eElement.getElementsByTagName("
//
//                    }
        }

        doc = imDocumentRepository.save(doc);

        return doc;
    }


    private String transformContentToHtml(String contentText) {
        Match root = $(contentText);

        root.find("paragraph").rename("p");
        root.find("text").rename("p");
        root.find("format[formattype=bold]").rename("strong").removeAttr("formattype");
        root.find("list[numbered=false]").rename("ul").removeAttr("numbered");
        root.find("list[numbered=true]").rename("ol").removeAttr("numbered");
        root.find("listitem").rename("li");

        renameAttr(root.find("image").rename("img"), "source", "data-source");


//        List<Match> imgs = root.find("image").rename("img").each();
//
//        // Rename image attr
//        for (Match img : imgs) {
//            renameAttr(img, "source", "src");
//        }

        // Rename table type to class
        renameAttr(root.find("table[type]"), "type", "class");

        root.find("headerrow cell").rename("th");
        root.find("row cell").rename("td");

        // Convert header rows into <thead>
        Match headerRows = root.find("headerrow");
        headerRows.wrap("thead");
        headerRows.rename("tr");

        // Add a <tbody> and more normal rows inside it
        Match allTables = root.find("table");

        // Rename to <tr>
        root.find("row").rename("tr");

        // Move all direct child rows into <tbody>
        for (Match table : allTables.each()) {
            table.append("<tbody></tbody>");
            Match childRows = table.children("tr");
            Match tbody = table.find("tbody");

            // Add the rows to the <tbody>
            tbody.append(childRows);

            // Remove the original wrongly positioned rows
            //childRows.remove();
        }

        // If table cells only contain a single <p>, unwrap it


        //.wrap("tbody");

        // We need <li> around a sub-list
        root.find("ul > ul").wrap("li");
        root.find("ol > ol").wrap("li");
        root.find("ol > ul").wrap("li");
        root.find("ul > ol").wrap("li");

        // Unwrap nested <p><p>
        root.find("p > p").rename("span");

        contentText = root.toString();

        // Remove <content> and </content>
        contentText = contentText.replaceAll("<content>", "");
        contentText = contentText.replaceAll("</content>", "");

        // Remove whitespace between tags
        contentText = contentText.replaceAll(">\\s+<", "><");


        // Remove meaningless <p><p>... paragraph in paragraph
        // WOUTER: This can cause problems, for example <p bookmark="..."><p> is not handled
//        while (contentText.indexOf("<p><p>") != -1) {
//            contentText = contentText.replaceAll("<p><p>", "<p>");
//        }
//        while (contentText.indexOf("</p></p>") != -1) {
//            contentText = contentText.replaceAll("</p></p>", "</p>");
//        }

        // Remove meaningless <p/><p/><p/><p/>...
        while (contentText.indexOf("<p/><p/>") != -1) {
            contentText = contentText.replaceAll("<p/><p/>", "<p/>");
        }

        // Remove meaningless <p/><p>...
        while (contentText.indexOf("<p/><p>") != -1) {
            contentText = contentText.replaceAll("<p/><p>", "<p>");
        }

        // Remove meaningless </strong><strong>...
        while (contentText.indexOf("</strong><strong>") != -1) {
            contentText = contentText.replaceAll("</strong><strong>", "");
        }

        // Final trim
        contentText = contentText.trim();


        // Remove meaningless <p/> at the beginning
        while (contentText.length() > 4 && "<p/>".equals(contentText.substring(0, 4))) {
            contentText = contentText.substring(4);
        }

        // Remove meaningless <p/> at the end
        while (contentText.length() > 4 && "<p/>".equals(contentText.substring(contentText.length() - 4))) {
            contentText = contentText.substring(0, contentText.length() - 4);
        }

        return contentText;
    }

    private Match renameAttr(Match m, String oldName, String newName) {
        String src = m.attr(oldName);
        m.attr(newName, src);
        m.removeAttr(oldName);
        return m;
    }

    private String nodeToString(Node node) throws TransformerException {
        StringWriter sw = new StringWriter();

        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.transform(new DOMSource(node), new StreamResult(sw));

        return sw.toString();
    }

    protected String getText(Node node, String childNodeName) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() == Node.ELEMENT_NODE && childNodeName.equals(childNode.getNodeName())) {
                return childNode.getTextContent().trim();
            }
        }
        return null;
    }

    protected String getAttributeFromNode(Node node, String childNodeName, String attrName) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() == Node.ELEMENT_NODE && childNodeName.equals(childNode.getNodeName())) {
                return childNode.getAttributes().getNamedItem(attrName).getNodeValue();
            }
        }
        return null;
    }

    @PostMapping("/image/")
    @Timed
    public ResponseEntity<ImageCreated> handleImageFileUpload(@RequestParam("image_file") MultipartFile file, @RequestParam("access_token") String accessToken, @RequestParam("source") String source, @RequestParam("document_id") Long documentId, RedirectAttributes redirectAttributes) {

        log.debug("Image upload request: {}", file.getOriginalFilename());

        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

        if (imCloudSecurity.canUserUploadDocuments(uploadingUser)) {

            if (file == null) {
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "missing_image", "There was no file upload with name \"image_file\" present.")).body(null);
            }

            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")).toLowerCase();

            String contentType = null;

            if (".jpg".equals(extension)) {
                // Check if this is a valid JPG
                contentType = "image/jpg";

            } else if (".png".equals(extension)) {
                // Check if this is a valid PNG
                contentType = "image/png";
            }

            if (contentType != null) {

                try {
                    // Get the document this image belongs to
                    ImDocumentDTO documentDto = imDocumentService.findOne(documentId);
                    ImDocument document = imDocumentRepository.findOne(documentDto.getId());

                    if (documentDto.getUserId().equals(uploadingUser.getId())) {
                        // Owner is uploading more images

                        BufferedImage image = ImageIO.read(file.getInputStream());
                        Integer width = image.getWidth();
                        Integer height = image.getHeight();

                        if (width > 0 && height > 0) {
                            Image savedImage = processUploadedImage(file, source, document, contentType, width, height, uploadingUser);

                            return ResponseEntity.ok()
                                .body(new ImageCreated(savedImage));

                        } else {
                            // Invalid image dimensions
                            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "invalid_image", "The uploaded file is not an image.")).body(null);
                        }

                    } else {
                        // Not the document owner, cannot upload
                        return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "only_owner_can_upload", "Only the owner of the document can upload images.")).body(null);
                    }

                } catch (Exception ex) {
                    return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "exception", ex.getMessage())).body(null);
                }

            } else {
                // Invalid extension
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "invalid_extension", "Only files with extension .jpg or .png are allowed.")).body(null);
            }

        } else {
            return accessDeniedResponse();
        }


//        redirectAttributes.addFlashAttribute("message",
//            "You successfully uploaded " + file.getOriginalFilename() + "!");


    }


    @PostMapping("/complete")
    @Timed
    public ResponseEntity<ImDocumentCompletelyUploaded> handleDocumentComplete(@RequestParam("access_token") String accessToken, @RequestParam("document_id") Long documentId, @RequestParam(value = "send_email", required = false) Boolean sendEmail) {
        log.debug("Document complete request: {}", documentId);

        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

        if (imCloudSecurity.canUserUploadDocuments(uploadingUser)) {
            ImDocument doc = imDocumentRepository.findOne(documentId);

            doc.setUploadComplete(true);

            doc = imDocumentRepository.save(doc);

            if (sendEmail) {
                mailService.sendDocumentUploadedEmail(uploadingUser, doc);
            }

            String baseUrl = imCloudProperties.getBaseUrl();
            return ResponseEntity.ok().body(new ImDocumentCompletelyUploaded(doc, baseUrl));

        } else {
            return accessDeniedResponse();
        }

    }


    @Transactional
    private Image processUploadedImage(MultipartFile file, String source, ImDocument document, String contentType, int width, int height, User uploadingUser) throws IOException, NoSuchAlgorithmException {
        String filename = fileStorageService.saveFileAndGetPath(file);

        File f = fileStorageService.loadFile(filename);
        long contentLength = Files.size(f.toPath());

        Image image = imageRepository.findByFilename(filename);

        if (image != null) {
            // This image has already been uploaded before. Don't create a new record.
        } else {
            image = new Image();
            image.setFilename(filename);
            image.setContentType(contentType);
            image.setContentLength(contentLength);
            image.setImageWidth(width);
            image.setImageHeight(height);
            image.setUploadedByUser(uploadingUser);

            image = imageRepository.save(image);
        }

        for (ImMap map : document.getMaps()) {
            for (ImBlock block : map.getBlocks()) {

                // Check block image label
                if (source.equals(block.getLabelImageSource())) {
                    block.setLabelImage(image);
                    block.setLabelImageSource(null);
                }


                // Check images in content
                boolean imageIsUsedInBlock = false;

                String blockContent = block.getContent();

                Match root = DomHelper.getDomRoot(blockContent);
                for (Match img : root.find("img[data-source]").each()) {

                    String imgSource = img.attr("data-source");
                    if (source.equals(imgSource)) {
                        // This is the right image
                        img.attr("data-id", "" + image.getId());
                        img.removeAttr("data-source");

                        imageIsUsedInBlock = true;
                    }
                }

                if (imageIsUsedInBlock) {
                    block.addImage(image);

                    blockContent = DomHelper.domToString(root);
                    block.setContent(blockContent);

                    block = imBlockRepository.save(block);
                }
            }
        }

        return image;
    }

    private ResponseEntity accessDeniedResponse() {
        return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("accessdenied", "Access denied")).body(null);
    }

//    private ResponseEntity exceptionResponse(Exception ex){
//        return new ErrorVM(ex.getMessage());
//    }

}
