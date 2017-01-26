package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.domain.Branding;
import be.storefront.imicloud.domain.Organization;
import be.storefront.imicloud.domain.UserInfo;
import be.storefront.imicloud.repository.UserInfoRepository;
import be.storefront.imicloud.security.MyUserDetails;
import be.storefront.imicloud.security.SecurityUtils;
import be.storefront.imicloud.service.OrganizationService;
import be.storefront.imicloud.service.dto.OrganizationDTO;
import com.codahale.metrics.annotation.Timed;
import be.storefront.imicloud.service.BrandingService;
import be.storefront.imicloud.web.rest.util.HeaderUtil;
import be.storefront.imicloud.service.dto.BrandingDTO;

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
 * REST controller for managing Branding.
 */
@RestController
@RequestMapping("/api")
public class BrandingResource {

    private final Logger log = LoggerFactory.getLogger(BrandingResource.class);

    @Inject
    private BrandingService brandingService;

    @Inject
    private UserInfoRepository userInfoRepository;

    @Inject
    private OrganizationService organizationService;


    protected boolean checkInput(BrandingDTO brandingDTO) {
        Organization myOrg = organizationService.getCurrentOrganization();

        MyUserDetails userDetails = SecurityUtils.getCurrentUser();
        if (myOrg == null) {
            // User is not logged in or does not have an organization
            return false;

        } else {
            if (brandingDTO.getOrganizationId() == null) {
                // Fill empty organization with my organization
                brandingDTO.setOrganizationId(myOrg.getId());

            } else if (!brandingDTO.getOrganizationId().equals(myOrg.getId())) {
                // User tried to create a branding for someone else
                return false;
            }
        }

        return true;
    }

    /**
     * POST  /brandings : Create a new branding.
     *
     * @param brandingDTO the brandingDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new brandingDTO, or with status 400 (Bad Request) if the branding has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/brandings")
    @Timed
    public ResponseEntity<BrandingDTO> createBranding(@Valid @RequestBody BrandingDTO brandingDTO) throws URISyntaxException {
        log.debug("REST request to save Branding : {}", brandingDTO);
        if (brandingDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("branding", "idexists", "A new branding cannot already have an ID")).body(null);
        }

        if (checkInput(brandingDTO)) {
            BrandingDTO result = brandingService.save(brandingDTO);
            return ResponseEntity.created(new URI("/api/brandings/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("branding", result.getId().toString()))
                .body(result);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * PUT  /brandings : Updates an existing branding.
     *
     * @param brandingDTO the brandingDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated brandingDTO,
     * or with status 400 (Bad Request) if the brandingDTO is not valid,
     * or with status 500 (Internal Server Error) if the brandingDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/brandings")
    @Timed
    public ResponseEntity<BrandingDTO> updateBranding(@Valid @RequestBody BrandingDTO brandingDTO) throws URISyntaxException {
        log.debug("REST request to update Branding : {}", brandingDTO);
        if (brandingDTO.getId() == null) {
            return createBranding(brandingDTO);
        }

        if (checkInput(brandingDTO)) {
            BrandingDTO result = brandingService.save(brandingDTO);
            return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert("branding", brandingDTO.getId().toString()))
                .body(result);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * GET  /brandings : get all the brandings.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of brandings in body
     */
    @GetMapping("/brandings")
    @Timed
    public List<BrandingDTO> getAllBrandings() {
        log.debug("REST request to get all Brandings");

        Organization myOrg = organizationService.getCurrentOrganization();
        if (myOrg == null) {
            return null;
        } else {
            return brandingService.findByOrganizationId(myOrg.getId());
        }

    }

    /**
     * GET  /brandings/:id : get the "id" branding.
     *
     * @param id the id of the brandingDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the brandingDTO, or with status 404 (Not Found)
     */
    @GetMapping("/brandings/{id}")
    @Timed
    public ResponseEntity<BrandingDTO> getBranding(@PathVariable Long id) {
        log.debug("REST request to get Branding : {}", id);

        // TODO check user access limitations

        Organization myOrg = organizationService.getCurrentOrganization();
        BrandingDTO brandingDTO = brandingService.findOne(id);

        if (brandingDTO != null && brandingDTO.getOrganizationId() != null && brandingDTO.getOrganizationId().equals(myOrg.getId())) {
            // This is one of my brandings
            return Optional.ofNullable(brandingDTO)
                .map(result -> new ResponseEntity<>(
                    result,
                    HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE  /brandings/:id : delete the "id" branding.
     *
     * @param id the id of the brandingDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/brandings/{id}")
    @Timed
    public ResponseEntity<Void> deleteBranding(@PathVariable Long id) {
        log.debug("REST request to delete Branding : {}", id);

        BrandingDTO brandingDto = brandingService.findOne(id);
        Organization myOrg = organizationService.getCurrentOrganization();

        if(brandingDto.getOrganizationId() != null && brandingDto.getOrganizationId().equals(myOrg.getId())){
            brandingService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("branding", id.toString())).build();
        }else{
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

//    /**
//     * SEARCH  /_search/brandings?query=:query : search for the branding corresponding
//     * to the query.
//     *
//     * @param query the query of the branding search
//     * @return the result of the search
//     */
//    @GetMapping("/_search/brandings")
//    @Timed
//    public List<BrandingDTO> searchBrandings(@RequestParam String query) {
//        log.debug("REST request to search Brandings for query {}", query);
//        return brandingService.search(query);
//    }


}
