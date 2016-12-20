package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.service.*;
import be.storefront.imicloud.service.dto.ImBlockDTO;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.dto.ImMapDTO;
import be.storefront.imicloud.service.dto.ImageDTO;
import be.storefront.imicloud.service.mapper.ImBlockMapper;
import be.storefront.imicloud.service.mapper.ImDocumentMapper;
import be.storefront.imicloud.service.mapper.ImageMapper;
import be.storefront.imicloud.web.rest.response.ImDocumentUploaded;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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
    private ImDocumentService imDocumentService;

    @Inject
    private ImCloudSecurity imCloudSecurity;

    @Inject
    private ImMapService imMapService;

    @Inject
    private ImBlockService imBlockService;


    @Inject
    private ImDocumentMapper imDocumentMapper;

    @Inject
    private ImBlockMapper imBlockMapper;

    @Inject
    private ImageMapper imageMapper;

    @GetMapping("/")
    public String index() {
        return "upload";
    }


    @PostMapping("/xml/")
    @Timed
    public
    @ResponseBody
    ResponseEntity<ImDocumentUploaded> handleXmlFileUpload(@RequestParam(value = "password", required = false) String password, @RequestParam("xml_file") MultipartFile file,
                                                           @RequestParam("template_code") String templateCode, @RequestParam("access_token") String accessToken,
                                                           RedirectAttributes redirectAttributes) throws ParserConfigurationException, TransformerException, SAXException, IOException {

        log.debug("XML upload request: {}", file.getOriginalFilename());

        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

        if (imCloudSecurity.canUserUploadDocuments(uploadingUser)) {

            ImDocumentDTO newDocument = processUploadedDocument(file, password, uploadingUser);

            ImDocumentUploaded imDocumentUploaded = new ImDocumentUploaded(newDocument);

            return ResponseEntity.ok().body(imDocumentUploaded);


        } else {
            return accessDeniedResponse();
        }
    }

    @Transactional
    private ImDocumentDTO processUploadedDocument(MultipartFile file, String password, User user) throws IOException, ParserConfigurationException, SAXException, TransformerException {

        ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
        String xmlString = IOUtils.toString(stream, "UTF-8");

        // Cleanup XML string
        xmlString = xmlString.trim().replaceFirst("^([\\W]+)<", "<");

        String hashedPassword = null;
        if (password != null && password.length() > 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            hashedPassword = passwordEncoder.encode(password);
        }

        ImDocumentDTO newDocument = new ImDocumentDTO();
        newDocument.setOriginalFilename(file.getOriginalFilename());
        newDocument.setOriginalXml(xmlString);
        newDocument.setPassword(hashedPassword);
        newDocument.setUserId(user.getId());


        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xmlString));
        Document doc = dBuilder.parse(is);

        //optional, but recommended, read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();


        newDocument = imDocumentService.save(newDocument);

        //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

        NodeList mapList = doc.getElementsByTagName("map");

        for (int i = 0; i < mapList.getLength(); i++) {

            Node oneMap = mapList.item(i);
            if (oneMap.getNodeType() == Node.ELEMENT_NODE) {
                Element oneMapElement = (Element) oneMap;

                String mapGuid = oneMapElement.getAttribute("guid");
                String mapLabel = getText(oneMap, "label");

                // Save all maps
                ImMapDTO newMapDto = new ImMapDTO();
                newMapDto.setGuid(mapGuid);
                newMapDto.setImDocumentId(newDocument.getId());
                newMapDto.setLabel(mapLabel);
                newMapDto.setPosition((float) i);
                imMapService.save(newMapDto);

                NodeList blockList = oneMapElement.getElementsByTagName("block");

                for (int j = 0; j < blockList.getLength(); j++) {
                    Node oneBlock = blockList.item(j);

                    if (oneBlock.getNodeType() == Node.ELEMENT_NODE) {
                        Element oneBlockElement = (Element) oneBlock;

                        String blockGuid = oneBlockElement.getAttribute("guid");
                        String blockLabel = getText(oneBlock, "label");

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
                        newBlockDto.setPosition((float) j);
                        newBlockDto.setContent(contentText);

                        imBlockService.save(newBlockDto);
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
        return newDocument;
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

        root.find("headerrow").rename("tr");
        //.wrap("thead");
        root.find("row").rename("tr");
        //.wrap("tbody");

        // We need <li> around a sub-list
        root.find("ul > ul").wrap("li");
        root.find("ol > ol").wrap("li");
        root.find("ol > ul").wrap("li");
        root.find("ul > ol").wrap("li");

        contentText = root.toString();

        // Remove <content> and </content>
        contentText = contentText.replaceAll("<content>", "");
        contentText = contentText.replaceAll("</content>", "");

        // Remove whitespace between tags
        contentText = contentText.replaceAll(">\\s+<", "><");


        // Remove meaningless <p><p>... paragraph in paragraph
        while (contentText.indexOf("<p><p>") != -1) {
            contentText = contentText.replaceAll("<p><p>", "<p>");
        }
        while (contentText.indexOf("</p></p>") != -1) {
            contentText = contentText.replaceAll("</p></p>", "</p>");
        }

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

    @PostMapping("/image/")
    @Timed
    public ResponseEntity<ImageDTO> handleImageFileUpload(@RequestParam("image_file") MultipartFile file, @RequestParam("access_token") String accessToken, @RequestParam("source") String source, @RequestParam("document_id") Long documentId, RedirectAttributes redirectAttributes) {

        log.debug("Image upload request: {}", file.getOriginalFilename());

        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

        if (imCloudSecurity.canUserUploadDocuments(uploadingUser)) {

            if (file == null) {
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "missing_image", "There was no file upload with name \"image_file\" present.")).body(null);
            }

            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")).toLowerCase();

            if (".jpg".equals(extension) || ".png".equals(extension)) {

                try {
                    // Get the document this image belongs to
                    ImDocumentDTO documentDto = imDocumentService.findOne(documentId);
                    ImDocument document = imDocumentMapper.imDocumentDTOToImDocument(documentDto);


                    if (documentDto.getUserId().equals(uploadingUser.getId())) {
                        // Owner is uploading more images

                        BufferedImage image = ImageIO.read(file.getInputStream());
                        Integer width = image.getWidth();
                        Integer height = image.getHeight();

                        if (width > 0 && height > 0) {
                            ImageDTO savedImage = processUploadedImage(file, source, document);

                            return ResponseEntity.ok()
                                .body(savedImage);

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

    @Transactional
    private ImageDTO processUploadedImage(MultipartFile file, String source, ImDocument document) throws IOException, NoSuchAlgorithmException {
        String filename = fileStorageService.saveFile(file);

        ImageDTO newImage = new ImageDTO();
        newImage.setFilename(filename);

        ImageDTO savedImageDto = imageService.save(newImage);

        // TODO now that the image is uploaded, update all relationships

        for(ImMap map : document.getMaps()){
            for(ImBlock block : map.getBlocks()){
                String blockContent = block.getContent();

                Match root = $(blockContent);
                for(Match img : root.find("img[data-source]").each()){

                    String imgSource = img.attr("data-source");
                    if(source.equals(imgSource)){
                        // This is the right image
                        img.attr("data-id", ""+savedImageDto.getId());
                        img.removeAttr("data-source");
                    }
                }

                Image savedImage = imageMapper.imageDTOToImage(savedImageDto);
                block.addImage(savedImage);
                block.setContent(root.toString());

                // Save the block
                ImBlockDTO blockDto = imBlockMapper.imBlockToImBlockDTO(block);

                imBlockService.save(blockDto);
            }
        }

        return savedImageDto;
    }

    private ResponseEntity accessDeniedResponse() {
        return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("accessdenied", "Access denied")).body(null);
    }

//    private ResponseEntity exceptionResponse(Exception ex){
//        return new ErrorVM(ex.getMessage());
//    }

}
