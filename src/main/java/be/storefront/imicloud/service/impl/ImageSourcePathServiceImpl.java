package be.storefront.imicloud.service.impl;

import be.storefront.imicloud.service.ImageSourcePathService;
import be.storefront.imicloud.domain.ImageSourcePath;
import be.storefront.imicloud.repository.ImageSourcePathRepository;
import be.storefront.imicloud.repository.search.ImageSourcePathSearchRepository;
import be.storefront.imicloud.service.dto.ImageSourcePathDTO;
import be.storefront.imicloud.service.mapper.ImageSourcePathMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ImageSourcePath.
 */
@Service
@Transactional
public class ImageSourcePathServiceImpl implements ImageSourcePathService{

    private final Logger log = LoggerFactory.getLogger(ImageSourcePathServiceImpl.class);
    
    @Inject
    private ImageSourcePathRepository imageSourcePathRepository;

    @Inject
    private ImageSourcePathMapper imageSourcePathMapper;

    @Inject
    private ImageSourcePathSearchRepository imageSourcePathSearchRepository;

    /**
     * Save a imageSourcePath.
     *
     * @param imageSourcePathDTO the entity to save
     * @return the persisted entity
     */
    public ImageSourcePathDTO save(ImageSourcePathDTO imageSourcePathDTO) {
        log.debug("Request to save ImageSourcePath : {}", imageSourcePathDTO);
        ImageSourcePath imageSourcePath = imageSourcePathMapper.imageSourcePathDTOToImageSourcePath(imageSourcePathDTO);
        imageSourcePath = imageSourcePathRepository.save(imageSourcePath);
        ImageSourcePathDTO result = imageSourcePathMapper.imageSourcePathToImageSourcePathDTO(imageSourcePath);
        imageSourcePathSearchRepository.save(imageSourcePath);
        return result;
    }

    /**
     *  Get all the imageSourcePaths.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ImageSourcePathDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ImageSourcePaths");
        Page<ImageSourcePath> result = imageSourcePathRepository.findAll(pageable);
        return result.map(imageSourcePath -> imageSourcePathMapper.imageSourcePathToImageSourcePathDTO(imageSourcePath));
    }

    /**
     *  Get one imageSourcePath by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ImageSourcePathDTO findOne(Long id) {
        log.debug("Request to get ImageSourcePath : {}", id);
        ImageSourcePath imageSourcePath = imageSourcePathRepository.findOne(id);
        ImageSourcePathDTO imageSourcePathDTO = imageSourcePathMapper.imageSourcePathToImageSourcePathDTO(imageSourcePath);
        return imageSourcePathDTO;
    }

    /**
     *  Delete the  imageSourcePath by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ImageSourcePath : {}", id);
        imageSourcePathRepository.delete(id);
        imageSourcePathSearchRepository.delete(id);
    }

    /**
     * Search for the imageSourcePath corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ImageSourcePathDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ImageSourcePaths for query {}", query);
        Page<ImageSourcePath> result = imageSourcePathSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(imageSourcePath -> imageSourcePathMapper.imageSourcePathToImageSourcePathDTO(imageSourcePath));
    }
}
