package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.security.ImCloudSecurity;
import be.storefront.imicloud.service.FileStorageService;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.service.ImageService;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.dto.ImageDTO;
import be.storefront.imicloud.web.rest.response.ExceptionResponse;
import be.storefront.imicloud.web.rest.response.ImDocumentCreatedResponse;
import be.storefront.imicloud.web.rest.response.XmlUploadResponse;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

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

    @GetMapping("/")
    public String index() {
        return "upload";
    }


    @PostMapping("/xml/")
    @Timed
    public
    @ResponseBody
    ResponseEntity<ImDocumentDTO> handleXmlFileUpload(@RequestParam(value = "password", required = false) String password, @RequestParam("xml_file") MultipartFile file,
                                                      @RequestParam("template_code") String templateCode, @RequestParam("access_token") String accessToken,
                                                      RedirectAttributes redirectAttributes) {

        log.debug("XML upload request: {}", file.getOriginalFilename());

        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

        if (imCloudSecurity.canUserUploadDocuments(uploadingUser)) {

            try {
                ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
                String xmlString = IOUtils.toString(stream, "UTF-8");

                String hashedPassword = null;
                if (password != null && password.length() > 0) {
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    hashedPassword = passwordEncoder.encode(password);
                }

                ImDocumentDTO newDocument = new ImDocumentDTO();
                newDocument.setOriginalFilename(file.getOriginalFilename());
                newDocument.setOriginalXml(xmlString);
                newDocument.setPassword(hashedPassword);
                newDocument.setUserId(uploadingUser.getId());


                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlString);

                //optional, but recommended, read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

                NodeList mapList = doc.getElementsByTagName("map");



                for (int i = 0; i < mapList.getLength(); i++) {

                    Node oneMap = mapList.item(i);
                    if (oneMap.getNodeType() == Node.ELEMENT_NODE) {
                        Element oneMapElement = (Element) oneMap;

                        String mapGuid = oneMapElement.getAttribute("guid");
                        String mapLabel = getText(oneMap, "label");

                        String wouter = "wouter";

                        NodeList blockList = oneMapElement.getElementsByTagName("block");

                        for (int j = 0; j < blockList.getLength(); j++) {
                            Node oneBlock = mapList.item(j);

                            if (oneBlock.getNodeType() == Node.ELEMENT_NODE) {
                                Element oneBlockElement = (Element) oneBlock;

                                String blockGuid = oneBlockElement.getAttribute("guid");
                                String blockLabel = getText(oneBlock, "label");

                                NodeList contentNodes = oneBlockElement.getElementsByTagName("content");
                                if(contentNodes.getLength() > 0){
                                    Element contentElement = (Element)contentNodes.item(0);
                                    
                                }

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


                newDocument = imDocumentService.save(newDocument);

                return ResponseEntity.ok()
                    .body(newDocument);

            } catch (Exception ex) {
                ExceptionResponse response = new ExceptionResponse();
                response.errorMessage = ex.getMessage();

                return ResponseEntity.badRequest()
                    .body(null);
            }
        } else {
            return accessDeniedResponse();
        }
    }

    protected String getText(Node node, String childNodeName){
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if(childNode.getNodeType() == Node.ELEMENT_NODE && childNodeName.equals(childNode.getNodeName())){
                return childNode.getTextContent();
            }
        }
        return null;
    }

    @PostMapping("/image/")
    @Timed
    public ResponseEntity<ImageDTO> handleImageFileUpload(@RequestParam("image_file") MultipartFile file, @RequestParam("access_token") String accessToken, RedirectAttributes redirectAttributes) {

        log.debug("Image upload request: {}", file.getOriginalFilename());

        User uploadingUser = imCloudSecurity.getUserByFsProAccessToken(accessToken);

        if (imCloudSecurity.canUserUploadDocuments(uploadingUser)) {


            if (file == null) {
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "missing_image", "There was no file upload with name \"image_file\" present.")).body(null);
            }

            try {

                String extension = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")).toLowerCase();

                if (".jpg".equals(extension) || ".png".equals(extension)) {

                    BufferedImage image = ImageIO.read(file.getInputStream());
                    Integer width = image.getWidth();
                    Integer height = image.getHeight();

                    if (width > 0 && height > 0) {

                        // TODO check if image belongs to a valid ImDocument
                        // TODO check if image belongs to an ImDocument that I have access to!!
                        // TODO check permissions

                        String filename = fileStorageService.saveFile(file);

                        ImageDTO newImage = new ImageDTO();
                        newImage.setFilename(filename);

                        ImageDTO savedImage = imageService.save(newImage);

                        return ResponseEntity.ok()
                            .body(savedImage);
                    } else {
                        // Invalid image dimensions
                        return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "invalid_image", "The uploaded file is not an image.")).body(null);
                    }

                } else {
                    // Invalid extension
                    return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "invalid_extension", "Only files with extension .jpg or .png are allowed.")).body(null);
                }

            } catch (Exception ex) {
                return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "exception", ex.getMessage())).body(null);
            }
        } else {
            return accessDeniedResponse();
        }


//        redirectAttributes.addFlashAttribute("message",
//            "You successfully uploaded " + file.getOriginalFilename() + "!");


    }

    private ResponseEntity accessDeniedResponse() {
        return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("accessdenied", "Access denied")).body(null);
    }

}
