package be.storefront.imicloud.web.rest;

import com.codahale.metrics.annotation.Timed;
import be.storefront.imicloud.service.FolderService;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import be.storefront.imicloud.service.dto.FolderDTO;

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
 * REST controller for managing Folder.
 */
@RestController
@RequestMapping("/api")
public class FolderResource {

    private final Logger log = LoggerFactory.getLogger(FolderResource.class);
        
    @Inject
    private FolderService folderService;

    /**
     * POST  /folders : Create a new folder.
     *
     * @param folderDTO the folderDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new folderDTO, or with status 400 (Bad Request) if the folder has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/folders")
    @Timed
    public ResponseEntity<FolderDTO> createFolder(@Valid @RequestBody FolderDTO folderDTO) throws URISyntaxException {
        log.debug("REST request to save Folder : {}", folderDTO);
        if (folderDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("folder", "idexists", "A new folder cannot already have an ID")).body(null);
        }
        FolderDTO result = folderService.save(folderDTO);
        return ResponseEntity.created(new URI("/api/folders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("folder", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /folders : Updates an existing folder.
     *
     * @param folderDTO the folderDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated folderDTO,
     * or with status 400 (Bad Request) if the folderDTO is not valid,
     * or with status 500 (Internal Server Error) if the folderDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/folders")
    @Timed
    public ResponseEntity<FolderDTO> updateFolder(@Valid @RequestBody FolderDTO folderDTO) throws URISyntaxException {
        log.debug("REST request to update Folder : {}", folderDTO);
        if (folderDTO.getId() == null) {
            return createFolder(folderDTO);
        }
        FolderDTO result = folderService.save(folderDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("folder", folderDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /folders : get all the folders.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of folders in body
     */
    @GetMapping("/folders")
    @Timed
    public List<FolderDTO> getAllFolders() {
        log.debug("REST request to get all Folders");
        return folderService.findAll();
    }

    /**
     * GET  /folders/:id : get the "id" folder.
     *
     * @param id the id of the folderDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the folderDTO, or with status 404 (Not Found)
     */
    @GetMapping("/folders/{id}")
    @Timed
    public ResponseEntity<FolderDTO> getFolder(@PathVariable Long id) {
        log.debug("REST request to get Folder : {}", id);
        FolderDTO folderDTO = folderService.findOne(id);
        return Optional.ofNullable(folderDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /folders/:id : delete the "id" folder.
     *
     * @param id the id of the folderDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/folders/{id}")
    @Timed
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        log.debug("REST request to delete Folder : {}", id);
        folderService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("folder", id.toString())).build();
    }

    /**
     * SEARCH  /_search/folders?query=:query : search for the folder corresponding
     * to the query.
     *
     * @param query the query of the folder search 
     * @return the result of the search
     */
    @GetMapping("/_search/folders")
    @Timed
    public List<FolderDTO> searchFolders(@RequestParam String query) {
        log.debug("REST request to search Folders for query {}", query);
        return folderService.search(query);
    }


}
