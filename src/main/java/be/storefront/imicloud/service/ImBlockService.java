package be.storefront.imicloud.service;

import be.storefront.imicloud.service.dto.ImBlockDTO;
import java.util.List;

/**
 * Service Interface for managing ImBlock.
 */
public interface ImBlockService {

    /**
     * Save a imBlock.
     *
     * @param imBlockDTO the entity to save
     * @return the persisted entity
     */
    ImBlockDTO save(ImBlockDTO imBlockDTO);

    /**
     *  Get all the imBlocks.
     *  
     *  @return the list of entities
     */
    List<ImBlockDTO> findAll();

    /**
     *  Get the "id" imBlock.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ImBlockDTO findOne(Long id);

    /**
     *  Delete the "id" imBlock.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the imBlock corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @return the list of entities
     */
    List<ImBlockDTO> search(String query);
}
