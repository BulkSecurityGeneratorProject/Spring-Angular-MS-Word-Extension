package be.storefront.imicloud.service.impl;

import be.storefront.imicloud.service.ImBlockService;
import be.storefront.imicloud.domain.ImBlock;
import be.storefront.imicloud.repository.ImBlockRepository;
import be.storefront.imicloud.repository.search.ImBlockSearchRepository;
import be.storefront.imicloud.service.dto.ImBlockDTO;
import be.storefront.imicloud.service.mapper.ImBlockMapper;
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
 * Service Implementation for managing ImBlock.
 */
@Service
@Transactional
public class ImBlockServiceImpl implements ImBlockService{

    private final Logger log = LoggerFactory.getLogger(ImBlockServiceImpl.class);
    
    @Inject
    private ImBlockRepository imBlockRepository;

    @Inject
    private ImBlockMapper imBlockMapper;

    @Inject
    private ImBlockSearchRepository imBlockSearchRepository;

    /**
     * Save a imBlock.
     *
     * @param imBlockDTO the entity to save
     * @return the persisted entity
     */
    public ImBlockDTO save(ImBlockDTO imBlockDTO) {
        log.debug("Request to save ImBlock : {}", imBlockDTO);
        ImBlock imBlock = imBlockMapper.imBlockDTOToImBlock(imBlockDTO);
        imBlock = imBlockRepository.save(imBlock);
        ImBlockDTO result = imBlockMapper.imBlockToImBlockDTO(imBlock);
        imBlockSearchRepository.save(imBlock);
        return result;
    }

    /**
     *  Get all the imBlocks.
     *  
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<ImBlockDTO> findAll() {
        log.debug("Request to get all ImBlocks");
        List<ImBlockDTO> result = imBlockRepository.findAll().stream()
            .map(imBlockMapper::imBlockToImBlockDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one imBlock by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ImBlockDTO findOne(Long id) {
        log.debug("Request to get ImBlock : {}", id);
        ImBlock imBlock = imBlockRepository.findOne(id);
        ImBlockDTO imBlockDTO = imBlockMapper.imBlockToImBlockDTO(imBlock);
        return imBlockDTO;
    }

    /**
     *  Delete the  imBlock by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ImBlock : {}", id);
        imBlockRepository.delete(id);
        imBlockSearchRepository.delete(id);
    }

    /**
     * Search for the imBlock corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<ImBlockDTO> search(String query) {
        log.debug("Request to search ImBlocks for query {}", query);
        return StreamSupport
            .stream(imBlockSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(imBlockMapper::imBlockToImBlockDTO)
            .collect(Collectors.toList());
    }
}
