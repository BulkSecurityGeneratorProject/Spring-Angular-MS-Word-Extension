package be.storefront.imicloud.service;

import be.storefront.imicloud.service.dto.ImageDTO;
import java.util.List;

/**
 * Service Interface for managing Image.
 */
public interface ImageService {

    /**
     * Save a image.
     *
     * @param imageDTO the entity to save
     * @return the persisted entity
     */
    ImageDTO save(ImageDTO imageDTO);

    /**
     *  Get all the images.
     *
     *  @return the list of entities
     */
    List<ImageDTO> findAll();

    /**
     *  Get the "id" image.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ImageDTO findOne(Long id);

    /**
     *  Delete the "id" image.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the image corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @return the list of entities
     */
    List<ImageDTO> search(String query);


    boolean isImageInUse(Long imageId);
}
