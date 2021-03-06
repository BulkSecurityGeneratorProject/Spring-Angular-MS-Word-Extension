package be.storefront.imicloud.service;

import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.service.dto.BrandingDTO;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service Interface for managing Branding.
 */
public interface BrandingService {

    /**
     * Save a branding.
     *
     * @param brandingDTO the entity to save
     * @return the persisted entity
     */
    BrandingDTO save(BrandingDTO brandingDTO);

    /**
     *  Get all the brandings.
     *
     *  @return the list of entities
     */
    List<BrandingDTO> findAll();

    /**
     *  Get the "id" branding.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    BrandingDTO findOne(Long id);

    /**
     *  Delete the "id" branding.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the branding corresponding to the query.
     *
     *  @param query the query of the search
     *
     *  @return the list of entities
     */
    List<BrandingDTO> search(String query);

    BrandingDTO findByDocument(ImDocument imDocument);

    BrandingDTO findByUserId(Long userId);

    List<BrandingDTO> findByOrganizationId(Long id);

    Page<BrandingDTO> findByOrganizationId(Long id, Pageable pageable);
}
