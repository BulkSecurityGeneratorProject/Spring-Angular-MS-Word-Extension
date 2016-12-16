package be.storefront.imicloud.web;

import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.service.FileStorageService;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.service.ImageService;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/upload")
public class UploadController {


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
    public ResponseEntity<ResponseEntity.BodyBuilder> handleXmlFileUpload(@RequestParam("xml_file") MultipartFile file,
                                                                          RedirectAttributes redirectAttributes) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
            String xmlString = IOUtils.toString(stream, "UTF-8");

            ImDocumentDTO newDocument = new ImDocumentDTO();
            newDocument.setOriginalXml(xmlString);
            imDocumentService.save(newDocument);

            fileStorageService.saveFile(file);

            return ResponseEntity.created(null).body(null);
//                .body(newUser);

        } catch (Exception ex) {

            return ResponseEntity.status(500).body(null);

        }


//        redirectAttributes.addFlashAttribute("message",
//            "You successfully uploaded " + file.getOriginalFilename() + "!");


    }

    @PostMapping("/image/")
    public ResponseEntity<ResponseEntity.BodyBuilder> handleImageFileUpload(@RequestParam("image_file") MultipartFile file,
                                                                            RedirectAttributes redirectAttributes) {

        try {
            fileStorageService.saveFile(file);


            //.body(newUser);

        } catch (Exception ex) {

        }

        return ResponseEntity.created(null).body(null);
//        redirectAttributes.addFlashAttribute("message",
//            "You successfully uploaded " + file.getOriginalFilename() + "!");


    }

}
