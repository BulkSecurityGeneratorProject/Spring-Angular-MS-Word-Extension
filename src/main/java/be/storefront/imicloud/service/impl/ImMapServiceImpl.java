package be.storefront.imicloud.service.impl;

import be.storefront.imicloud.service.ImMapService;
import be.storefront.imicloud.domain.ImMap;
import be.storefront.imicloud.repository.ImMapRepository;
import be.storefront.imicloud.repository.search.ImMapSearchRepository;
import be.storefront.imicloud.service.dto.ImMapDTO;
import be.storefront.imicloud.service.mapper.ImMapMapper;
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
 * Service Implementation for managing ImMap.
 */
@Service
@Transactional
public class ImMapServiceImpl implements ImMapService{

    private final Logger log = LoggerFactory.getLogger(ImMapServiceImpl.class);
    
    @Inject
    private ImMapRepository imMapRepository;

    @Inject
    private ImMapMapper imMapMapper;

    @Inject
    private ImMapSearchRepository imMapSearchRepository;

    /**
     * Save a imMap.
     *
     * @param imMapDTO the entity to save
     * @return the persisted entity
     */
    public ImMapDTO save(ImMapDTO imMapDTO) {
        log.debug("Request to save ImMap : {}", imMapDTO);
        ImMap imMap = imMapMapper.imMapDTOToImMap(imMapDTO);
        imMap = imMapRepository.save(imMap);
        ImMapDTO result = imMapMapper.imMapToImMapDTO(imMap);
        imMapSearchRepository.save(imMap);
        return result;
    }

    /**
     *  Get all the imMaps.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<ImMapDTO> findAll() {
        log.debug("Request to get all ImMaps");
        List<ImMapDTO> result = imMapRepository.findAll().stream()
            .map(imMapMapper::imMapToImMapDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one imMap by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ImMapDTO findOne(Long id) {
        log.debug("Request to get ImMap : {}", id);
        ImMap imMap = imMapRepository.findOne(id);
        ImMapDTO imMapDTO = imMapMapper.imMapToImMapDTO(imMap);
        return imMapDTO;
    }

    /**
     *  Delete the  imMap by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ImMap : {}", id);
        imMapRepository.delete(id);
        imMapSearchRepository.delete(id);
    }

    /**
     * Search for the imMap corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ImMapDTO> search(String query) {
        log.debug("Request to search ImMaps for query {}", query);
        return StreamSupport
            .stream(imMapSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(imMapMapper::imMapToImMapDTO)
            .collect(Collectors.toList());
    }
}
