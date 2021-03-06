package be.storefront.imicloud.web;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.domain.*;
import be.storefront.imicloud.domain.document.ImDocumentStructure;
import be.storefront.imicloud.domain.util.XmlDocument;
import be.storefront.imicloud.repository.*;
import be.storefront.imicloud.security.DocumentPasswordEncoder;
import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.security.MyUserDetails;
import be.storefront.imicloud.security.SecurityUtils;
import be.storefront.imicloud.service.*;
import be.storefront.imicloud.service.dto.ImBlockDTO;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.dto.ImMapDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.joox.JOOX.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
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

    @Inject
    private ImageSourcePathRepository imageSourcePathRepository;

    @Inject
    private UrlHelperService urlHelperService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private BrandingRepository brandingRepository;

    @Inject
    private UserInfoRepository userInfoRepository;

    //@Inject private PasswordEncoder passwordEncoder;

    private PasswordEncoder documentPasswordEncoder = new DocumentPasswordEncoder();

    @GetMapping("/")
    public String index() {
        return "upload";
    }


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

            boolean isOverwritingExistingDoc = isOverwritingExistingDoc(uploadingUser, documentName);

            if (isOverwritingExistingDoc) {
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
    String reprocessAllXmls(HttpServletResponse response) {
        String r = "";

        List<ImDocument> docs = imDocumentRepository.findAll();
        for (ImDocument doc : docs) {
            r += reprocessXml(doc);
        }

        return r;
    }

    @GetMapping("/xml/reprocess/{documentId}")
    @Timed
    public
    @ResponseBody
    String reprocessOneXml(@PathVariable(value = "documentId") Long documentId) {
        String r = "";

        ImDocument doc = imDocumentRepository.findOne(documentId);

        r += reprocessXml(doc);

        return r;
    }

    protected String reprocessXml(ImDocument doc) {
        String r = "";
        try {
            doc = processXmlSavedInDocument(doc);
            r += "Document " + doc.getId() + ": OK\n";
        } catch (Exception e) {
            r += "Document " + doc.getId() + ": ERROR: " + e.toString() + "\n";
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
                // User is allowed to upload

                boolean isOverwritingExistingDoc = isOverwritingExistingDoc(uploadingUser, documentName);

                if (imCloudSecurity.hasUserAvailableStorage(uploadingUser, isOverwritingExistingDoc)) {
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
            hashedPassword = documentPasswordEncoder.encode(password);
        }

        // Test XML document
        Document xmlDoc = XmlDocument.getXmlDocumentFromString(xmlString);

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

        doc.setTempXml(xmlString);
        doc.setTempPassword(hashedPassword);
        doc.setTempTemplate(templateCode);

        // Add missing random secret
        if (doc.getSecret() == null || "".equals(doc.getSecret())) {
            doc.setSecret(SecurityUtils.generateSecret());
        }

        if (doc.getBranding() == null) {
            UserInfo ui = userInfoRepository.findByUserId(doc.getUser().getId());
            Organization organization = ui.getOrganization();
            Branding defaultBranding = organization.getBranding();

            doc.setBranding(defaultBranding);
        }

        doc = imDocumentRepository.save(doc);

        //processXmlSavedInDocument(doc);

        return imDocumentMapper.imDocumentToImDocumentDTO(doc);
    }

    private boolean isOverwritingExistingDoc(User u, String documentName) {
        List<ImDocument> docs = imDocumentRepository.findByUserAndDocumentName(u.getId(), documentName);
        return (docs.size() > 0);
    }


    @Transactional
    @Timed
    protected ImDocument processXmlSavedInDocument(ImDocument doc) throws ParserConfigurationException, SAXException, IOException, TransformerException {

        // Delete old maps - if any
        for (ImMap m : doc.getMaps()) {
            imMapService.delete(m.getId());
        }
        doc.setMaps(null);

        // Process what's in the XML
        Document xmlDoc = XmlDocument.getXmlDocumentFromString(doc.getOriginalXml());

        //removeNodesByType(xmlDoc, "overviewmap");

        NodeList mapList = xmlDoc.getElementsByTagName("map");

        for (int i = 0; i < mapList.getLength(); i++) {

            Node oneMap = mapList.item(i);
            if (oneMap.getNodeType() == Node.ELEMENT_NODE) {
                Element oneMapElement = (Element) oneMap;

                String mapGuid = oneMapElement.getAttribute("guid");
                String mapLabel = XmlDocument.getText(oneMap, "label");

                // Is the map in an overviewmap? Use that label instead...
                Node parentNode = oneMap.getParentNode();
                if ("overviewmap".equals(parentNode.getNodeName())) {
                    String overviewMapTitle = XmlDocument.getText(parentNode, "title");

                    if (mapLabel.length() == 0) {
                        mapLabel = overviewMapTitle;
                    } else {
                        mapLabel = overviewMapTitle + " - " + mapLabel;
                    }
                }

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
                        String blockLabel = XmlDocument.getText(oneBlock, "label");
                        String blockImageSource = XmlDocument.getAttributeFromChildNode(oneBlock, "image", "source");

                        String contentText = null;

                        NodeList contentNodes = oneBlockElement.getElementsByTagName("content");
                        if (contentNodes.getLength() > 0) {
                            Element contentElement = (Element) contentNodes.item(0);

                            contentText = XmlDocument.nodeToString(contentElement).trim();
                        }

                        contentText = ImDocumentStructure.transformContentToHtml(contentText);

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
        }

        doc = imDocumentRepository.save(doc);

        processImagesInDocument(doc);
        return doc;
    }

    private void removeNodesByType(Document xmlDoc, String nodeName) {
        NodeList targets = xmlDoc.getElementsByTagName(nodeName);

        int numberToKeep = 0;
        int numberToDelete = targets.getLength() - numberToKeep;

        for (int i = 0; i < numberToDelete; i++) {
            Node target = targets.item(numberToKeep);
            Node parent = target.getParentNode();
            parent.removeChild(target);
        }
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

            if (".jpg".equals(extension) || ".jpeg".equals(extension)) {
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

                        Image savedImage = processUploadedImage(file, source, document, contentType, uploadingUser);

                        return ResponseEntity.ok()
                            .body(new ImageCreated(savedImage));


                    } else {
                        // Not the document owner, cannot upload
                        return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "only_owner_can_upload", "Only the owner of the document can upload images.")).body(null);
                    }

                } catch (Exception ex) {
                    return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "exception", ex.getMessage())).body(null);
                }

            } else {
                // Invalid extension
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "invalid_extension", "Only files with extension .jpg, .jpeg or .png are allowed.")).body(null);
            }

        } else {
            return accessDeniedResponse();
        }


//        redirectAttributes.addFlashAttribute("message",
//            "You successfully uploaded " + file.getOriginalFilename() + "!");


    }


    @PostMapping("/complete")
    @Timed
    public ResponseEntity<ImDocumentCompletelyUploaded> handleDocumentComplete(@RequestParam("access_token") String accessToken, @RequestParam("document_id") Long documentId, @RequestParam(value = "send_email", required = false) Boolean sendEmail) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        log.debug("Document complete request: {}", documentId);

        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

        if (imCloudSecurity.canUserUploadDocuments(uploadingUser)) {
            ImDocument doc = imDocumentRepository.findOne(documentId);

            doc = markDocumentComplete(doc);

            if (sendEmail) {
                mailService.sendDocumentUploadedEmail(uploadingUser, doc);
            }

            return ResponseEntity.ok().body(new ImDocumentCompletelyUploaded(doc, urlHelperService));

        } else {
            return accessDeniedResponse();
        }

    }


    @Transactional
    private Image processUploadedImage(MultipartFile file, String source, ImDocument document, String contentType, User uploadingUser) throws IOException, NoSuchAlgorithmException {
        Image image = imageService.createImageFromUpload(file, contentType, uploadingUser);

        // Prevent double data
        List<ImageSourcePath> existingSourcePaths = imageSourcePathRepository.findByDocumentIdAndSource(document.getId(), source);

        // Delete all previously incomplete uploads
        for (ImageSourcePath isp : existingSourcePaths) {
            if (isp.isUploadComplete()) {
                // This image is now in use -> keep it
            } else {
                // This is an other upload that was not completed -> delete it, we have a new one
                imageSourcePathRepository.delete(isp);
            }
        }

        // Remember the "source" for this image, so we can later reprocess the XML
        ImageSourcePath isp = new ImageSourcePath();
        isp.setImage(image);
        isp.setImDocument(document);
        isp.setSource(source);

        // Mark incomplete so it is kept alongside the completed, currently in use image.
        // When the upload is done, the new image will replace the old.
        isp.setUploadComplete(false);

        isp = imageSourcePathRepository.save(isp);

        //processImageInDocument(document, source, image);

        return image;
    }

    @Transactional
    @Timed
    protected ImDocument markDocumentComplete(ImDocument imDocument) throws ParserConfigurationException, TransformerException, SAXException, IOException {

        imDocument.setOriginalXml(imDocument.getTempXml());
        imDocument.setPassword(imDocument.getTempPassword());
        imDocument.setDefaultTemplate(imDocument.getTempTemplate());
        imDocument.setUploadComplete(true);

        // Delete all previously ok images
        List<ImageSourcePath> existingSourcePaths = imageSourcePathRepository.findByDocumentId(imDocument.getId());

        for (ImageSourcePath isp : existingSourcePaths) {
            if (isp.isUploadComplete()) {
                // This image is old -> remove it
                imageSourcePathRepository.delete(isp);
            } else {
                // This is the new image -> keep it
                isp.setUploadComplete(true);
                imageSourcePathRepository.save(isp);
            }
        }

        processXmlSavedInDocument(imDocument);

        return imDocument;
    }

    @Timed
    protected void processImagesInDocument(ImDocument doc) {
        List<ImageSourcePath> sourcePaths = imageSourcePathRepository.findByDocumentIdAndUploadComplete(doc.getId());

        if (doc.getMaps() == null) {
            // Reload before processing image, because the "maps" can be null due to earlier processing
            doc = imDocumentRepository.findOne(doc.getId());
        }

        for (ImMap map : doc.getMaps()) {
            for (ImBlock block : map.getBlocks()) {

                boolean isBlockChanged = false;

                String blockContent = block.getContent();
                Match root = DomHelper.getDomRoot(blockContent);
                Match imagesInBlock = root.find("img[data-source]");

                // Loop all image sources
                for (ImageSourcePath isp : sourcePaths) {
                    Image image = isp.getImage();
                    String source = isp.getSource();

                    // Check block image label
                    if (source.equals(block.getLabelImageSource())) {
                        block.setLabelImage(image);
                        //block.setLabelImageSource(null);

                        isBlockChanged = true;
                    }

                    // Check images in content
                    for (Match img : imagesInBlock.each()) {
                        String imgSource = img.attr("data-source");
                        if (source.equals(imgSource)) {
                            // This is the right image

                            img.attr("data-id", "" + image.getId());
                            //img.removeAttr("data-source");

                            isBlockChanged = true;

                            // This block needs this image
                            block.addImage(image);
                        }
                    }
                }


                if (isBlockChanged) {
                    // Save is needed

                    blockContent = DomHelper.domToString(root);
                    block.setContent(blockContent);

                    imBlockRepository.save(block);
                }


            }
        }


    }


    private ResponseEntity accessDeniedResponse() {
        return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("accessdenied", "Access denied")).body(null);
    }


    @PostMapping("/logo/")
    @Timed
    public ResponseEntity<ImageCreated> handleLogoFileUpload(@RequestParam("logo_file") MultipartFile file) {
        log.debug("Logo upload request: {}", file.getOriginalFilename());

        MyUserDetails currentUser = SecurityUtils.getCurrentUser();

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        } else if (file == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "missing_image", "There was no file upload with name \"image_file\" present.")).body(null);
        }

        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")).toLowerCase();

        String contentType = null;

        if (".jpg".equals(extension) || ".jpeg".equals(extension)) {
            // Check if this is a valid JPG
            contentType = "image/jpg";

        } else if (".png".equals(extension)) {
            // Check if this is a valid PNG
            contentType = "image/png";
        }

        if (contentType != null) {
            try {
                User uploadingUser = userRepository.findOne(currentUser.getId());
                Image savedImage = imageService.createImageFromUpload(file, contentType, uploadingUser);

                return ResponseEntity.ok()
                    .body(new ImageCreated(savedImage, urlHelperService));

            } catch (Exception ex) {
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "exception", ex.getMessage())).body(null);
            }

        } else {
            // Invalid extension
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "invalid_extension", "Only files with extension .jpg, .jpeg or .png are allowed.")).body(null);
        }

//        redirectAttributes.addFlashAttribute("message",
//            "You successfully uploaded " + file.getOriginalFilename() + "!");


    }

//    private ResponseEntity exceptionResponse(Exception ex){
//        return new ErrorVM(ex.getMessage());
//    }

}
