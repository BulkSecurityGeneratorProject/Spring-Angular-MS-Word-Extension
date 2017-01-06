package be.storefront.imicloud.service;

import be.storefront.imicloud.service.dto.ImageSourcePathDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing ImageSourcePath.
 */
public interface ImageSourcePathService {

    /**
     * Save a imageSourcePath.
     *
     * @param imageSourcePathDTO the entity to save
     * @return the persisted entity
     */
    ImageSourcePathDTO save(ImageSourcePathDTO imageSourcePathDTO);

    /**
     *  Get all the imageSourcePaths.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ImageSourcePathDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" imageSourcePath.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ImageSourcePathDTO findOne(Long id);

    /**
     *  Delete the "id" imageSourcePath.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the imageSourcePath corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ImageSourcePathDTO> search(String query, Pageable pageable);
}
