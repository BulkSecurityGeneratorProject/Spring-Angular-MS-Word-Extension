package be.storefront.imicloud.service.impl;

import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.repository.ImDocumentRepository;
import be.storefront.imicloud.repository.search.ImDocumentSearchRepository;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.mapper.ImDocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing ImDocument.
 */
@Service
@Transactional
public class ImDocumentServiceImpl implements ImDocumentService{

    private final Logger log = LoggerFactory.getLogger(ImDocumentServiceImpl.class);

    @Inject
    private ImDocumentRepository imDocumentRepository;

    @Inject
    private ImDocumentMapper imDocumentMapper;

    @Inject
    private ImDocumentSearchRepository imDocumentSearchRepository;

    /**
     * Save a imDocument.
     *
     * @param imDocumentDTO the entity to save
     * @return the persisted entity
     */
    public ImDocumentDTO save(ImDocumentDTO imDocumentDTO) {
        log.debug("Request to save ImDocument : {}", imDocumentDTO);
        ImDocument imDocument = imDocumentMapper.imDocumentDTOToImDocument(imDocumentDTO);
        imDocument = imDocumentRepository.save(imDocument);
        ImDocumentDTO result = imDocumentMapper.imDocumentToImDocumentDTO(imDocument);
        imDocumentSearchRepository.save(imDocument);
        return result;
    }

    /**
     *  Get all the imDocuments.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ImDocumentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all ImDocuments");
        Page<ImDocument> result = imDocumentRepository.findAll(pageable);
        return result.map(imDocument -> imDocumentMapper.imDocumentToImDocumentDTO(imDocument));
    }

    /**
     *  Get one imDocument by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public ImDocumentDTO findOne(Long id) {
        log.debug("Request to get ImDocument : {}", id);
        ImDocument imDocument = imDocumentRepository.findOne(id);
        ImDocumentDTO imDocumentDTO = imDocumentMapper.imDocumentToImDocumentDTO(imDocument);
        return imDocumentDTO;
    }

    /**
     *  Delete the  imDocument by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete ImDocument : {}", id);
        imDocumentRepository.delete(id);
        imDocumentSearchRepository.delete(id);
    }

    /**
     * Search for the imDocument corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ImDocumentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of ImDocuments for query {}", query);
        Page<ImDocument> result = imDocumentSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(imDocument -> imDocumentMapper.imDocumentToImDocumentDTO(imDocument));
    }
}
