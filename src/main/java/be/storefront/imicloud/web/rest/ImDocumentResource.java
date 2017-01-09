package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.domain.ImDocument;
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
        // TODO limit to current user documents
        log.debug("REST request to get a page of ImDocuments");
        Page<ImDocumentDTO> page = imDocumentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/im-documents");

        for(ImDocumentDTO imDocumentDTO : page.getContent()){
            imDocumentDTO.setUrlHelperService(urlHelperService);
        }

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
        // TODO check accesa for current user
        log.debug("REST request to get ImDocument : {}", id);
        ImDocumentDTO imDocumentDTO = imDocumentService.findOne(id);

        imDocumentDTO.setUrlHelperService(urlHelperService);

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
        // TODO check accesa for current user
        log.debug("REST request to delete ImDocument : {}", id);
        imDocumentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("imDocument", id.toString())).build();
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
