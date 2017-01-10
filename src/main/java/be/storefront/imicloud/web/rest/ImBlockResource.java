package be.storefront.imicloud.web.rest;

import com.codahale.metrics.annotation.Timed;
import be.storefront.imicloud.service.ImBlockService;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import be.storefront.imicloud.service.dto.ImBlockDTO;

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
 * REST controller for managing ImBlock.
 */
@RestController
@RequestMapping("/api")
public class ImBlockResource {

    private final Logger log = LoggerFactory.getLogger(ImBlockResource.class);

    @Inject
    private ImBlockService imBlockService;

//    /**
//     * POST  /im-blocks : Create a new imBlock.
//     *
//     * @param imBlockDTO the imBlockDTO to create
//     * @return the ResponseEntity with status 201 (Created) and with body the new imBlockDTO, or with status 400 (Bad Request) if the imBlock has already an ID
//     * @throws URISyntaxException if the Location URI syntax is incorrect
//     */
//    @PostMapping("/im-blocks")
//    @Timed
//    public ResponseEntity<ImBlockDTO> createImBlock(@Valid @RequestBody ImBlockDTO imBlockDTO) throws URISyntaxException {
//        log.debug("REST request to save ImBlock : {}", imBlockDTO);
//        if (imBlockDTO.getId() != null) {
//            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("imBlock", "idexists", "A new imBlock cannot already have an ID")).body(null);
//        }
//        ImBlockDTO result = imBlockService.save(imBlockDTO);
//        return ResponseEntity.created(new URI("/api/im-blocks/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert("imBlock", result.getId().toString()))
//            .body(result);
//    }

//    /**
//     * PUT  /im-blocks : Updates an existing imBlock.
//     *
//     * @param imBlockDTO the imBlockDTO to update
//     * @return the ResponseEntity with status 200 (OK) and with body the updated imBlockDTO,
//     * or with status 400 (Bad Request) if the imBlockDTO is not valid,
//     * or with status 500 (Internal Server Error) if the imBlockDTO couldnt be updated
//     * @throws URISyntaxException if the Location URI syntax is incorrect
//     */
//    @PutMapping("/im-blocks")
//    @Timed
//    public ResponseEntity<ImBlockDTO> updateImBlock(@Valid @RequestBody ImBlockDTO imBlockDTO) throws URISyntaxException {
//        log.debug("REST request to update ImBlock : {}", imBlockDTO);
//        if (imBlockDTO.getId() == null) {
//            return createImBlock(imBlockDTO);
//        }
//        ImBlockDTO result = imBlockService.save(imBlockDTO);
//        return ResponseEntity.ok()
//            .headers(HeaderUtil.createEntityUpdateAlert("imBlock", imBlockDTO.getId().toString()))
//            .body(result);
//    }

    /**
     * GET  /im-blocks : get all the imBlocks.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of imBlocks in body
     */
    @GetMapping("/im-blocks")
    @Timed
    public List<ImBlockDTO> getAllImBlocks() {
        log.debug("REST request to get all ImBlocks");
        return imBlockService.findAll();
    }

    /**
     * GET  /im-blocks/:id : get the "id" imBlock.
     *
     * @param id the id of the imBlockDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the imBlockDTO, or with status 404 (Not Found)
     */
    @GetMapping("/im-blocks/{id}")
    @Timed
    public ResponseEntity<ImBlockDTO> getImBlock(@PathVariable Long id) {
        log.debug("REST request to get ImBlock : {}", id);
        ImBlockDTO imBlockDTO = imBlockService.findOne(id);
        return Optional.ofNullable(imBlockDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

//    /**
//     * DELETE  /im-blocks/:id : delete the "id" imBlock.
//     *
//     * @param id the id of the imBlockDTO to delete
//     * @return the ResponseEntity with status 200 (OK)
//     */
//    @DeleteMapping("/im-blocks/{id}")
//    @Timed
//    public ResponseEntity<Void> deleteImBlock(@PathVariable Long id) {
//        log.debug("REST request to delete ImBlock : {}", id);
//        imBlockService.delete(id);
//        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("imBlock", id.toString())).build();
//    }

//    /**
//     * SEARCH  /_search/im-blocks?query=:query : search for the imBlock corresponding
//     * to the query.
//     *
//     * @param query the query of the imBlock search
//     * @return the result of the search
//     */
//    @GetMapping("/_search/im-blocks")
//    @Timed
//    public List<ImBlockDTO> searchImBlocks(@RequestParam String query) {
//        log.debug("REST request to search ImBlocks for query {}", query);
//        return imBlockService.search(query);
//    }


}
