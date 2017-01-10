package be.storefront.imicloud.service.impl;

import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.security.SecurityUtils;
import be.storefront.imicloud.service.FileStorageService;
import be.storefront.imicloud.service.ImageService;
import be.storefront.imicloud.domain.Image;
import be.storefront.imicloud.repository.ImageRepository;
import be.storefront.imicloud.repository.search.ImageSearchRepository;
import be.storefront.imicloud.service.dto.ImageDTO;
import be.storefront.imicloud.service.mapper.ImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Image.
 */
@Service
@Transactional
public class ImageServiceImpl implements ImageService{

    private final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private ImageMapper imageMapper;

    @Inject
    private ImageSearchRepository imageSearchRepository;

    @Inject private FileStorageService fileStorageService;

    /**
     * Save a image.
     *
     * @param imageDTO the entity to save
     * @return the persisted entity
     */
    public ImageDTO save(ImageDTO imageDTO) {
        log.debug("Request to save Image : {}", imageDTO);
        Image image = imageMapper.imageDTOToImage(imageDTO);
        image = imageRepository.save(image);
        ImageDTO result = imageMapper.imageToImageDTO(image);
        imageSearchRepository.save(image);
        return result;
    }

    /**
     *  Get all the images.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ImageDTO> findAll() {
        log.debug("Request to get all Images");
        List<ImageDTO> result = imageRepository.findAllWithEagerRelationships().stream()
            .map(imageMapper::imageToImageDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one image by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public ImageDTO findOne(Long id) {
        log.debug("Request to get Image : {}", id);
        Image image = imageRepository.findOneWithEagerRelationships(id);
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);
        return imageDTO;
    }

    /**
     *  Delete the  image by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Image : {}", id);
        imageRepository.delete(id);
        imageSearchRepository.delete(id);
    }

    /**
     * Search for the image corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ImageDTO> search(String query) {
        log.debug("Request to search Images for query {}", query);
        return StreamSupport
            .stream(imageSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(imageMapper::imageToImageDTO)
            .collect(Collectors.toList());
    }

    public boolean isImageInUse(Long imageId){
        // Check use in block content

        // Check use in block label (background)

        // Check use as logo in branding

        return true;
    }


    public Image createImageFromUpload(MultipartFile file, String contentType, User uploadingUser) throws IOException, NoSuchAlgorithmException {
        String filename = fileStorageService.saveFileAndGetPath(file);

        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        Integer width = bufferedImage.getWidth();
        Integer height = bufferedImage.getHeight();

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
            image.setSecret(SecurityUtils.generateSecret());

            image = imageRepository.save(image);
        }
        return image;
    }
}
