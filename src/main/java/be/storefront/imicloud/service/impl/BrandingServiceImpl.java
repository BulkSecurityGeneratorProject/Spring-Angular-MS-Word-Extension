package be.storefront.imicloud.service.impl;

import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.domain.UserInfo;
import be.storefront.imicloud.repository.UserInfoRepository;
import be.storefront.imicloud.service.BrandingService;
import be.storefront.imicloud.domain.Branding;
import be.storefront.imicloud.repository.BrandingRepository;
import be.storefront.imicloud.repository.search.BrandingSearchRepository;
import be.storefront.imicloud.service.UserInfoService;
import be.storefront.imicloud.service.dto.BrandingDTO;
import be.storefront.imicloud.service.mapper.BrandingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Branding.
 */
@Service
@Transactional
public class BrandingServiceImpl implements BrandingService{

    private final Logger log = LoggerFactory.getLogger(BrandingServiceImpl.class);

    @Inject
    private BrandingRepository brandingRepository;

    @Inject
    private BrandingMapper brandingMapper;

    @Inject
    private BrandingSearchRepository brandingSearchRepository;

    @Inject private UserInfoRepository userInfoRepository;

    /**
     * Save a branding.
     *
     * @param brandingDTO the entity to save
     * @return the persisted entity
     */
    public BrandingDTO save(BrandingDTO brandingDTO) {
        log.debug("Request to save Branding : {}", brandingDTO);
        Branding branding = brandingMapper.brandingDTOToBranding(brandingDTO);
        branding = brandingRepository.save(branding);
        BrandingDTO result = brandingMapper.brandingToBrandingDTO(branding);
        brandingSearchRepository.save(branding);
        return result;
    }

    /**
     *  Get all the brandings.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<BrandingDTO> findAll() {
        log.debug("Request to get all Brandings");
        List<BrandingDTO> result = brandingRepository.findAll().stream()
            .map(brandingMapper::brandingToBrandingDTO)
            .collect(Collectors.toCollection(LinkedList::new));

        return result;
    }

    /**
     *  Get one branding by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public BrandingDTO findOne(Long id) {
        log.debug("Request to get Branding : {}", id);
        Branding branding = brandingRepository.findOne(id);
        BrandingDTO brandingDTO = brandingMapper.brandingToBrandingDTO(branding);
        return brandingDTO;
    }

    /**
     *  Delete the  branding by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Branding : {}", id);
        brandingRepository.delete(id);
        brandingSearchRepository.delete(id);
    }

    /**
     * Search for the branding corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<BrandingDTO> search(String query) {
        log.debug("Request to search Brandings for query {}", query);
        return StreamSupport
            .stream(brandingSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(brandingMapper::brandingToBrandingDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Object findByDocument(ImDocument imDocument) {
        if(imDocument != null){
            UserInfo ui = userInfoRepository.findByUserId(imDocument.getUser().getId());
            return ui.getOrganization().getBranding();
        }else{
            return null;
        }

    }
}
