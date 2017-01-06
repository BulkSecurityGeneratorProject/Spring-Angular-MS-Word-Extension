package be.storefront.imicloud.web.rest;

import com.codahale.metrics.annotation.Timed;
import be.storefront.imicloud.service.ImageSourcePathService;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import be.storefront.imicloud.web.rest.util.PaginationUtil;
import be.storefront.imicloud.service.dto.ImageSourcePathDTO;

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
 * REST controller for managing ImageSourcePath.
 */
@RestController
@RequestMapping("/api")
public class ImageSourcePathResource {

    private final Logger log = LoggerFactory.getLogger(ImageSourcePathResource.class);
        
    @Inject
    private ImageSourcePathService imageSourcePathService;

    /**
     * POST  /image-source-paths : Create a new imageSourcePath.
     *
     * @param imageSourcePathDTO the imageSourcePathDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new imageSourcePathDTO, or with status 400 (Bad Request) if the imageSourcePath has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/image-source-paths")
    @Timed
    public ResponseEntity<ImageSourcePathDTO> createImageSourcePath(@Valid @RequestBody ImageSourcePathDTO imageSourcePathDTO) throws URISyntaxException {
        log.debug("REST request to save ImageSourcePath : {}", imageSourcePathDTO);
        if (imageSourcePathDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("imageSourcePath", "idexists", "A new imageSourcePath cannot already have an ID")).body(null);
        }
        ImageSourcePathDTO result = imageSourcePathService.save(imageSourcePathDTO);
        return ResponseEntity.created(new URI("/api/image-source-paths/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("imageSourcePath", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /image-source-paths : Updates an existing imageSourcePath.
     *
     * @param imageSourcePathDTO the imageSourcePathDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated imageSourcePathDTO,
     * or with status 400 (Bad Request) if the imageSourcePathDTO is not valid,
     * or with status 500 (Internal Server Error) if the imageSourcePathDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/image-source-paths")
    @Timed
    public ResponseEntity<ImageSourcePathDTO> updateImageSourcePath(@Valid @RequestBody ImageSourcePathDTO imageSourcePathDTO) throws URISyntaxException {
        log.debug("REST request to update ImageSourcePath : {}", imageSourcePathDTO);
        if (imageSourcePathDTO.getId() == null) {
            return createImageSourcePath(imageSourcePathDTO);
        }
        ImageSourcePathDTO result = imageSourcePathService.save(imageSourcePathDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("imageSourcePath", imageSourcePathDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /image-source-paths : get all the imageSourcePaths.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of imageSourcePaths in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/image-source-paths")
    @Timed
    public ResponseEntity<List<ImageSourcePathDTO>> getAllImageSourcePaths(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of ImageSourcePaths");
        Page<ImageSourcePathDTO> page = imageSourcePathService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/image-source-paths");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /image-source-paths/:id : get the "id" imageSourcePath.
     *
     * @param id the id of the imageSourcePathDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the imageSourcePathDTO, or with status 404 (Not Found)
     */
    @GetMapping("/image-source-paths/{id}")
    @Timed
    public ResponseEntity<ImageSourcePathDTO> getImageSourcePath(@PathVariable Long id) {
        log.debug("REST request to get ImageSourcePath : {}", id);
        ImageSourcePathDTO imageSourcePathDTO = imageSourcePathService.findOne(id);
        return Optional.ofNullable(imageSourcePathDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /image-source-paths/:id : delete the "id" imageSourcePath.
     *
     * @param id the id of the imageSourcePathDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/image-source-paths/{id}")
    @Timed
    public ResponseEntity<Void> deleteImageSourcePath(@PathVariable Long id) {
        log.debug("REST request to delete ImageSourcePath : {}", id);
        imageSourcePathService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("imageSourcePath", id.toString())).build();
    }

    /**
     * SEARCH  /_search/image-source-paths?query=:query : search for the imageSourcePath corresponding
     * to the query.
     *
     * @param query the query of the imageSourcePath search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/image-source-paths")
    @Timed
    public ResponseEntity<List<ImageSourcePathDTO>> searchImageSourcePaths(@RequestParam String query, @ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of ImageSourcePaths for query {}", query);
        Page<ImageSourcePathDTO> page = imageSourcePathService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/image-source-paths");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
