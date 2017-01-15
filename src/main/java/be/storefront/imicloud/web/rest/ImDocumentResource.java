package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.security.MyUserDetails;
import be.storefront.imicloud.security.SecurityUtils;
import be.storefront.imicloud.service.UrlHelperService;
import com.codahale.metrics.annotation.Timed;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import be.storefront.imicloud.web.rest.util.PaginationUtil;
import be.storefront.imicloud.service.dto.ImDocumentDTO;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ImDocument.
 */
@RestController
@RequestMapping("/api")
public class ImDocumentResource {

    private final Logger log = LoggerFactory.getLogger(ImDocumentResource.class);

    @Inject
    private ImDocumentService imDocumentService;

    @Inject
    private UrlHelperService urlHelperService;

//    /**
//     * POST  /im-documents : Create a new imDocument.
//     *
//     * @param imDocumentDTO the imDocumentDTO to create
//     * @return the ResponseEntity with status 201 (Created) and with body the new imDocumentDTO, or with status 400 (Bad Request) if the imDocument has already an ID
//     * @throws URISyntaxException if the Location URI syntax is incorrect
//     */
//    @PostMapping("/im-documents")
//    @Timed
//    public ResponseEntity<ImDocumentDTO> createImDocument(@Valid @RequestBody ImDocumentDTO imDocumentDTO) throws URISyntaxException {
//        log.debug("REST request to save ImDocument : {}", imDocumentDTO);
//        if (imDocumentDTO.getId() != null) {
//            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("imDocument", "idexists", "A new imDocument cannot already have an ID")).body(null);
//        }
//        ImDocumentDTO result = imDocumentService.save(imDocumentDTO);
//        return ResponseEntity.created(new URI("/api/im-documents/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert("imDocument", result.getId().toString()))
//            .body(result);
//    }

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
        if (imDocumentDTO.getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        MyUserDetails userDetails = SecurityUtils.getCurrentUser();

        if (imDocumentDTO.getUserId() == null) {
            // User ID is required => Access denied
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        } else if (imDocumentDTO.getUserId().equals(userDetails.getId())) {
            // This is my document

            ImDocumentDTO result = imDocumentService.save(imDocumentDTO);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("imDocument", imDocumentDTO.getId().toString()))
                .body(result);

        } else {
            // Cannot update someone else's document
            //return ResponseEntity.badRequest().body(null);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
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

        MyUserDetails myUserDetails = SecurityUtils.getCurrentUser();

        if(myUserDetails != null) {
            Page<ImDocumentDTO> page = imDocumentService.findByUserId(myUserDetails.getId(), pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/im-documents");

            for (ImDocumentDTO imDocumentDTO : page.getContent()) {
                imDocumentDTO.setUrlHelperService(urlHelperService);
            }

            return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
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

        MyUserDetails myUserDetails = SecurityUtils.getCurrentUser();

        if (imDocumentDTO.getUserId() != null && imDocumentDTO.getUserId().equals(myUserDetails.getId())) {
            // Access granted
            imDocumentDTO.setUrlHelperService(urlHelperService);

            return Optional.ofNullable(imDocumentDTO)
                .map(result -> new ResponseEntity<>(
                    result,
                    HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


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

        ImDocumentDTO imDocumentDTO = imDocumentService.findOne(id);

        MyUserDetails myUserDetails = SecurityUtils.getCurrentUser();

        if (imDocumentDTO.getUserId() != null && imDocumentDTO.getUserId().equals(myUserDetails.getId())) {
            // Access granted
            imDocumentService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("imDocument", id.toString())).build();

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    /**
//     * SEARCH  /_search/im-documents?query=:query : search for the imDocument corresponding
//     * to the query.
//     *
//     * @param query the query of the imDocument search
//     * @param pageable the pagination information
//     * @return the result of the search
//     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
//     */
//    @GetMapping("/_search/im-documents")
//    @Timed
//    public ResponseEntity<List<ImDocumentDTO>> searchImDocuments(@RequestParam String query, @ApiParam Pageable pageable)
//        throws URISyntaxException {
//        log.debug("REST request to search for a page of ImDocuments for query {}", query);
//        Page<ImDocumentDTO> page = imDocumentService.search(query, pageable);
//        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/im-documents");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }


}
