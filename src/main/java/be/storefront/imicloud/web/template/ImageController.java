package be.storefront.imicloud.web.template;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.service.FileStorageService;
import be.storefront.imicloud.service.ImageService;
import be.storefront.imicloud.service.dto.ImageDTO;
import be.storefront.imicloud.web.exception.NotFoundException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.inject.Inject;
import java.io.*;

/**
 * Created by wouter on 03/01/2017.
 */
@Controller
public class ImageController {

    @Inject
    private ImageService imageService;

    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private FileStorageService fileStorageService;


    @GetMapping("/image/{imageId}/{secret}")
    public ResponseEntity<InputStreamResource> image(@PathVariable(value = "imageId") Long imageId, @PathVariable(value = "secret") String secret) throws IOException {

        ImageDTO imageDTO = imageService.findOne(imageId);
        if (secret != null && secret.equals(imageDTO.getSecret())) {
            File file = fileStorageService.loadFile(imageDTO.getFilename());

            InputStream in = new FileInputStream(file);

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            long contentLength = imageDTO.getContentLength();
            MediaType contentType = MediaType.parseMediaType(imageDTO.getContentType());


            return ResponseEntity.ok()
                .contentLength(contentLength)
                .contentType(contentType)
                .body(new InputStreamResource(in));
        }else{
            throw new NotFoundException();
        }

    }
}
