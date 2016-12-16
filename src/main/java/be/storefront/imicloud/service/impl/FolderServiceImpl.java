package be.storefront.imicloud.service.impl;

import be.storefront.imicloud.service.FolderService;
import be.storefront.imicloud.domain.Folder;
import be.storefront.imicloud.repository.FolderRepository;
import be.storefront.imicloud.repository.search.FolderSearchRepository;
import be.storefront.imicloud.service.dto.FolderDTO;
import be.storefront.imicloud.service.mapper.FolderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Folder.
 */
@Service
@Transactional
public class FolderServiceImpl implements FolderService{

    private final Logger log = LoggerFactory.getLogger(FolderServiceImpl.class);
    
    @Inject
    private FolderRepository folderRepository;

    @Inject
    private FolderMapper folderMapper;

    @Inject
    private FolderSearchRepository folderSearchRepository;

    /**
     * Save a folder.
     *
     * @param folderDTO the entity to save
     * @return the persisted entity
     */
    public FolderDTO save(FolderDTO folderDTO) {
        log.debug("Request to save Folder : {}", folderDTO);
        Folder folder = folderMapper.folderDTOToFolder(folderDTO);
        folder = folderRepository.save(folder);
        FolderDTO result = folderMapper.folderToFolderDTO(folder);
        folderSearchRepository.save(folder);
        return result;
    }

    /**
     *  Get all the folders.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<FolderDTO> findAll() {
        log.debug("Request to get all Folders");
        List<FolderDTO> result = folderRepository.findAll().stream()
            .map(folderMapper::folderToFolderDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one folder by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public FolderDTO findOne(Long id) {
        log.debug("Request to get Folder : {}", id);
        Folder folder = folderRepository.findOne(id);
        FolderDTO folderDTO = folderMapper.folderToFolderDTO(folder);
        return folderDTO;
    }

    /**
     *  Delete the  folder by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Folder : {}", id);
        folderRepository.delete(id);
        folderSearchRepository.delete(id);
    }

    /**
     * Search for the folder corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<FolderDTO> search(String query) {
        log.debug("Request to search Folders for query {}", query);
        return StreamSupport
            .stream(folderSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(folderMapper::folderToFolderDTO)
            .collect(Collectors.toList());
    }
}
