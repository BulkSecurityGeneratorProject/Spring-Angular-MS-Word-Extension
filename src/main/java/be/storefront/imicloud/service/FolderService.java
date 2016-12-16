package be.storefront.imicloud.service;

import be.storefront.imicloud.service.dto.FolderDTO;
import java.util.List;

/**
 * Service Interface for managing Folder.
 */
public interface FolderService {

    /**
     * Save a folder.
     *
     * @param folderDTO the entity to save
     * @return the persisted entity
     */
    FolderDTO save(FolderDTO folderDTO);

    /**
     *  Get all the folders.
     *  
     *  @return the list of entities
     */
    List<FolderDTO> findAll();

    /**
     *  Get the "id" folder.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    FolderDTO findOne(Long id);

    /**
     *  Delete the "id" folder.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the folder corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @return the list of entities
     */
    List<FolderDTO> search(String query);
}
