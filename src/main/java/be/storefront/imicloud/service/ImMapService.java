package be.storefront.imicloud.service;

import be.storefront.imicloud.service.dto.ImMapDTO;
import java.util.List;

/**
 * Service Interface for managing ImMap.
 */
public interface ImMapService {

    /**
     * Save a imMap.
     *
     * @param imMapDTO the entity to save
     * @return the persisted entity
     */
    ImMapDTO save(ImMapDTO imMapDTO);

    /**
     *  Get all the imMaps.
     *  
     *  @return the list of entities
     */
    List<ImMapDTO> findAll();

    /**
     *  Get the "id" imMap.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ImMapDTO findOne(Long id);

    /**
     *  Delete the "id" imMap.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the imMap corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @return the list of entities
     */
    List<ImMapDTO> search(String query);
}
