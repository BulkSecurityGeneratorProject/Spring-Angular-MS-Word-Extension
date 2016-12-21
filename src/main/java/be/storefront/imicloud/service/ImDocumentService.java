package be.storefront.imicloud.service;

import be.storefront.imicloud.service.dto.ImDocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service Interface for managing ImDocument.
 */
public interface ImDocumentService {

    /**
     * Save a imDocument.
     *
     * @param imDocumentDTO the entity to save
     * @return the persisted entity
     */
    ImDocumentDTO save(ImDocumentDTO imDocumentDTO);

    /**
     *  Get all the imDocuments.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ImDocumentDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" imDocument.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ImDocumentDTO findOne(Long id);

    /**
     *  Delete the "id" imDocument.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the imDocument corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ImDocumentDTO> search(String query, Pageable pageable);
}
