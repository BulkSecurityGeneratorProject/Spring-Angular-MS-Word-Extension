package be.storefront.imicloud.web.template;

import be.storefront.imicloud.config.ImCloudProperties;
import be.storefront.imicloud.service.FileStorageService;
import be.storefront.imicloud.service.ImageService;
import be.storefront.imicloud.service.dto.ImageDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by wouter on 03/01/2017.
 */
public class ImageController {

    @Inject
    private ImageService imageService;

    @Inject
    private ImCloudProperties imCloudProperties;

    @Inject
    private FileStorageService fileStorageService;


    @GetMapping("/image/{imageId}")
    public ResponseEntity<InputStreamResource> image(@PathVariable(value = "imageId") Long imageId) throws IOException {

        // TODO check if user is allowed to see this image

        ImageDTO imageDTO = imageService.findOne(imageId);
        File file = fileStorageService.loadFile(imageDTO.getFilename());

        InputStream in = new FileInputStream(file);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        long contentLength =imageDTO.getContentLength();
        MediaType contentType = MediaType.parseMediaType(imageDTO.getContentType());


        return ResponseEntity.ok()
            .contentLength(contentLength)
            .contentType(contentType)
            .body(new InputStreamResource(in));

    }
}