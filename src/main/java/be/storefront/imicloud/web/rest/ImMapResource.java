package be.storefront.imicloud.web.rest;

import com.codahale.metrics.annotation.Timed;
import be.storefront.imicloud.service.ImMapService;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import be.storefront.imicloud.service.dto.ImMapDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing ImMap.
 */
@RestController
@RequestMapping("/api")
public class ImMapResource {

    private final Logger log = LoggerFactory.getLogger(ImMapResource.class);
        
    @Inject
    private ImMapService imMapService;

    /**
     * POST  /im-maps : Create a new imMap.
     *
     * @param imMapDTO the imMapDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new imMapDTO, or with status 400 (Bad Request) if the imMap has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/im-maps")
    @Timed
    public ResponseEntity<ImMapDTO> createImMap(@Valid @RequestBody ImMapDTO imMapDTO) throws URISyntaxException {
        log.debug("REST request to save ImMap : {}", imMapDTO);
        if (imMapDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("imMap", "idexists", "A new imMap cannot already have an ID")).body(null);
        }
        ImMapDTO result = imMapService.save(imMapDTO);
        return ResponseEntity.created(new URI("/api/im-maps/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("imMap", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /im-maps : Updates an existing imMap.
     *
     * @param imMapDTO the imMapDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated imMapDTO,
     * or with status 400 (Bad Request) if the imMapDTO is not valid,
     * or with status 500 (Internal Server Error) if the imMapDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/im-maps")
    @Timed
    public ResponseEntity<ImMapDTO> updateImMap(@Valid @RequestBody ImMapDTO imMapDTO) throws URISyntaxException {
        log.debug("REST request to update ImMap : {}", imMapDTO);
        if (imMapDTO.getId() == null) {
            return createImMap(imMapDTO);
        }
        ImMapDTO result = imMapService.save(imMapDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("imMap", imMapDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /im-maps : get all the imMaps.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of imMaps in body
     */
    @GetMapping("/im-maps")
    @Timed
    public List<ImMapDTO> getAllImMaps() {
        log.debug("REST request to get all ImMaps");
        return imMapService.findAll();
    }

    /**
     * GET  /im-maps/:id : get the "id" imMap.
     *
     * @param id the id of the imMapDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the imMapDTO, or with status 404 (Not Found)
     */
    @GetMapping("/im-maps/{id}")
    @Timed
    public ResponseEntity<ImMapDTO> getImMap(@PathVariable Long id) {
        log.debug("REST request to get ImMap : {}", id);
        ImMapDTO imMapDTO = imMapService.findOne(id);
        return Optional.ofNullable(imMapDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /im-maps/:id : delete the "id" imMap.
     *
     * @param id the id of the imMapDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/im-maps/{id}")
    @Timed
    public ResponseEntity<Void> deleteImMap(@PathVariable Long id) {
        log.debug("REST request to delete ImMap : {}", id);
        imMapService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("imMap", id.toString())).build();
    }

    /**
     * SEARCH  /_search/im-maps?query=:query : search for the imMap corresponding
     * to the query.
     *
     * @param query the query of the imMap search 
     * @return the result of the search
     */
    @GetMapping("/_search/im-maps")
    @Timed
    public List<ImMapDTO> searchImMaps(@RequestParam String query) {
        log.debug("REST request to search ImMaps for query {}", query);
        return imMapService.search(query);
    }


}
