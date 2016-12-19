package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.security.AuthoritiesConstants;
import be.storefront.imicloud.security.SecurityUtils;
import com.codahale.metrics.annotation.Timed;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import be.storefront.imicloud.web.rest.util.PaginationUtil;
import be.storefront.imicloud.service.dto.ImDocumentDTO;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ImDocument.
 */
@RestController
@RequestMapping("/api")
public class ImDocumentResource {

    private final Logger log = LoggerFactory.getLogger(ImDocumentResource.class);

    @Inject
    private ImDocumentService imDocumentService;

    /**
     * POST  /im-documents : Create a new imDocument.
     *
     * @param imDocumentDTO the imDocumentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new imDocumentDTO, or with status 400 (Bad Request) if the imDocument has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/im-documents")
    @Timed
    public ResponseEntity<ImDocumentDTO> createImDocument(@Valid @RequestBody ImDocumentDTO imDocumentDTO) throws URISyntaxException {
        log.debug("REST request to save ImDocument : {}", imDocumentDTO);
        if (imDocumentDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("imDocument", "idexists", "A new imDocument cannot already have an ID")).body(null);
        }

        // Restrict to current user
        if(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
            // full access is okay
        }else{
            // limit to current user
            Long currentUserId = SecurityUtils.getCurrentUser().getId();
            if(imDocumentDTO.getUserId() != null && !imDocumentDTO.getUserId().equals(currentUserId)){
                // Cannot create a document for someone else!
                return ResponseEntity.badRequest().body(null);
            }else{
                imDocumentDTO.setUserId(currentUserId);
            }
        }

        ImDocumentDTO result = imDocumentService.save(imDocumentDTO);
        return ResponseEntity.created(new URI("/api/im-documents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("imDocument", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /im-documents : Updates an existing imDocument.
     *
     * @param imDocumentDTO the imDocumentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated imDocumentDTO,
     * or with status 400 (Bad Request) if the imDocumentDTO is not valid,
     * or with status 500 (Internal Server Error) if the imDocumentDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/im-documents")
    @Timed
    public ResponseEntity<ImDocumentDTO> updateImDocument(@Valid @RequestBody ImDocumentDTO imDocumentDTO) throws URISyntaxException {
        log.debug("REST request to update ImDocument : {}", imDocumentDTO);

        // Restrict to current user
        if(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
            // full access is okay
        }else {
            // limit to current user
            Long currentUserId = SecurityUtils.getCurrentUser().getId();
            if(imDocumentDTO.getUserId() != null && !imDocumentDTO.getUserId().equals(currentUserId)){
                // Cannot create a document for someone else!
                return ResponseEntity.badRequest().body(null);
            }else{
                imDocumentDTO.setUserId(currentUserId);
            }
        }

        if (imDocumentDTO.getId() == null) {
            return createImDocument(imDocumentDTO);
        }
        ImDocumentDTO result = imDocumentService.save(imDocumentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("imDocument", imDocumentDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /im-documents : get all the imDocuments.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of imDocuments in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/im-documents")
    @Timed
    public ResponseEntity<List<ImDocumentDTO>> getAllImDocuments(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ImDocuments");

        Page<ImDocumentDTO> page;

        // Restrict to current user
        if(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
            // full access is okay
            page = imDocumentService.findAll(pageable);

        }else {
            // limit to current user
            Long currentUserId = SecurityUtils.getCurrentUser().getId();

            page = imDocumentService.findAllByUserId(currentUserId, pageable);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/im-documents");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /im-documents/:id : get the "id" imDocument.
     *
     * @param id the id of the imDocumentDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the imDocumentDTO, or with status 404 (Not Found)
     */
    @GetMapping("/im-documents/{id}")
    @Timed
    public ResponseEntity<ImDocumentDTO> getImDocument(@PathVariable Long id) {
        log.debug("REST request to get ImDocument : {}", id);
        ImDocumentDTO imDocumentDTO = imDocumentService.findOne(id);

        // TODO restrict access in phase 2 when we have more roles...

        return Optional.ofNullable(imDocumentDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /im-documents/:id : delete the "id" imDocument.
     *
     * @param id the id of the imDocumentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/im-documents/{id}")
    @Timed
    public ResponseEntity<Void> deleteImDocument(@PathVariable Long id) {
        log.debug("REST request to delete ImDocument : {}", id);

        ImDocumentDTO document = imDocumentService.findOne(id);

        // Restrict to current user
        if(SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
            // full access is okay

        }else {
            // limit to current user
            Long currentUserId = SecurityUtils.getCurrentUser().getId();

            if(document.getUserId().equals(currentUserId)){
                // Delete is allowed
                imDocumentService.delete(id);
                return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("imDocument", id.toString())).build();
            }else{
                // Delete someone else's document is forbidden
                return ResponseEntity.badRequest().body(null);
            }

        }



    }

    /**
     * SEARCH  /_search/im-documents?query=:query : search for the imDocument corresponding
     * to the query.
     *
     * @param query the query of the imDocument search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
//    @GetMapping("/_search/im-documents")
//    @Timed
//    public ResponseEntity<List<ImDocumentDTO>> searchImDocuments(@RequestParam String query, @ApiParam Pageable pageable)
//        throws URISyntaxException {
//
//        log.debug("REST request to search for a page of ImDocuments for query {}", query);
//        Page<ImDocumentDTO> page = imDocumentService.search(query, pageable);
//        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/im-documents");
//
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }



}
