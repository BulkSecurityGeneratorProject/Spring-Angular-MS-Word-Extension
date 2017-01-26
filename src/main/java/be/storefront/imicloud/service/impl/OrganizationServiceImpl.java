package be.storefront.imicloud.service.impl;

import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.domain.UserInfo;
import be.storefront.imicloud.repository.UserInfoRepository;
import be.storefront.imicloud.security.MyUserDetails;
import be.storefront.imicloud.security.SecurityUtils;
import be.storefront.imicloud.service.OrganizationService;
import be.storefront.imicloud.domain.Organization;
import be.storefront.imicloud.repository.OrganizationRepository;
import be.storefront.imicloud.repository.search.OrganizationSearchRepository;
import be.storefront.imicloud.service.UserInfoService;
import be.storefront.imicloud.service.dto.OrganizationDTO;
import be.storefront.imicloud.service.mapper.OrganizationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Organization.
 */
@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final Logger log = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private OrganizationMapper organizationMapper;

    @Inject
    private OrganizationSearchRepository organizationSearchRepository;

    @Inject
    private UserInfoRepository userInfoRepository;

    /**
     * Save a organization.
     *
     * @param organizationDTO the entity to save
     * @return the persisted entity
     */
    public OrganizationDTO save(OrganizationDTO organizationDTO) {
        log.debug("Request to save Organization : {}", organizationDTO);
        Organization organization = organizationMapper.organizationDTOToOrganization(organizationDTO);
        organization = organizationRepository.save(organization);
        OrganizationDTO result = organizationMapper.organizationToOrganizationDTO(organization);
        organizationSearchRepository.save(organization);
        return result;
    }

    /**
     * Get all the organizations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OrganizationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Organizations");
        Page<Organization> result = organizationRepository.findAll(pageable);
        return result.map(organization -> organizationMapper.organizationToOrganizationDTO(organization));
    }

    /**
     * Get one organization by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public OrganizationDTO findOne(Long id) {
        log.debug("Request to get Organization : {}", id);
        Organization organization = organizationRepository.findOne(id);
        OrganizationDTO organizationDTO = organizationMapper.organizationToOrganizationDTO(organization);
        return organizationDTO;
    }

    /**
     * Delete the  organization by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Organization : {}", id);
        organizationRepository.delete(id);
        organizationSearchRepository.delete(id);
    }

    /**
     * Search for the organization corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<OrganizationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Organizations for query {}", query);
        Page<Organization> result = organizationSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(organization -> organizationMapper.organizationToOrganizationDTO(organization));
    }

    @Override
    public Organization getCurrentOrganization() {
        MyUserDetails userDetails = SecurityUtils.getCurrentUser();
        if (userDetails == null) {
            // User is not logged in
            return null;

        } else {

            // User is logged in
            UserInfo userInfo = userInfoRepository.findByUserId(userDetails.getId());
            Organization myOrg = userInfo.getOrganization();

            return myOrg;
        }
    }
}
