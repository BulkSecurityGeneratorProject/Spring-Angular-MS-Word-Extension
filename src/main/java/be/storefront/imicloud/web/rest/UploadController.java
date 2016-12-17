package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.security.AuthoritiesConstants;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private final Logger log = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    ImageService imageService;

    @Autowired
    ImDocumentService imDocumentService;

    @GetMapping("/")
    public String index() {
        return "upload";
    }


    @PostMapping("/xml/")
    @Timed
    public
    @ResponseBody
    XmlUploadResponse handleXmlFileUpload(@RequestParam("password") String password, @RequestParam("xml_file") MultipartFile file,
                                          @RequestParam("template_code") String templateCode,
                                          RedirectAttributes redirectAttributes) {

        log.debug("XML upload request: {}", file.getOriginalFilename());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // TODO check permissions

        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
            String xmlString = IOUtils.toString(stream, "UTF-8");

            String hashedPassword = passwordEncoder.encode(password);

            ImDocumentDTO newDocument = new ImDocumentDTO();
            newDocument.setOriginalFilename(file.getOriginalFilename());
            newDocument.setOriginalXml(xmlString);
            newDocument.setPassword(hashedPassword);
            imDocumentService.save(newDocument);

            ImDocumentCreatedResponse response = new ImDocumentCreatedResponse();
            response.documentId = newDocument.getId();

            return response;

        } catch (Exception ex) {

            ExceptionResponse response = new ExceptionResponse();
            response.errorMessage = ex.getMessage();

            return response;
        }
    }

    @PostMapping("/image/")
    @Timed
    public ResponseEntity<ImageDTO> handleImageFileUpload(@RequestParam("image_file") MultipartFile file, RedirectAttributes redirectAttributes) {

        log.debug("Image upload request: {}", file.getOriginalFilename());

        if (file == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("image", "missing_image", "There was no file upload with name \"image_file\" present.")).body(null);
        }

        try {

            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".")).toLowerCase();

            if (".jpg".equals(extension) || ".png".equals(extension)) {

                BufferedImage image = ImageIO.read(file.getInputStream());
                Integer width = image.getWidth();
                Integer height = image.getHeight();

                if(width > 0 && height > 0) {

                    // TODO check if image belongs to a valid ImDocument
                    // TODO check permissions

                    String filename = fileStorageService.saveFile(file);

                    ImageDTO newImage = new ImageDTO();
                    newImage.setFilename(filename);

                    ImageDTO savedImage = imageService.save(newImage);

                    return ResponseEntity.ok()
                        .body(savedImage);
                }else{
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


//        redirectAttributes.addFlashAttribute("message",
//            "You successfully uploaded " + file.getOriginalFilename() + "!");


    }

}
