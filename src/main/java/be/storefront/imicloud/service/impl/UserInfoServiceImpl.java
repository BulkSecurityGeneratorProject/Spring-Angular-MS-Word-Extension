package be.storefront.imicloud.service.impl;

import be.storefront.imicloud.service.UserInfoService;
import be.storefront.imicloud.domain.UserInfo;
import be.storefront.imicloud.repository.UserInfoRepository;
import be.storefront.imicloud.repository.search.UserInfoSearchRepository;
import be.storefront.imicloud.service.dto.UserInfoDTO;
import be.storefront.imicloud.service.mapper.UserInfoMapper;
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
 * Service Implementation for managing UserInfo.
 */
@Service
@Transactional
public class UserInfoServiceImpl implements UserInfoService{

    private final Logger log = LoggerFactory.getLogger(UserInfoServiceImpl.class);
    
    @Inject
    private UserInfoRepository userInfoRepository;

    @Inject
    private UserInfoMapper userInfoMapper;

    @Inject
    private UserInfoSearchRepository userInfoSearchRepository;

    /**
     * Save a userInfo.
     *
     * @param userInfoDTO the entity to save
     * @return the persisted entity
     */
    public UserInfoDTO save(UserInfoDTO userInfoDTO) {
        log.debug("Request to save UserInfo : {}", userInfoDTO);
        UserInfo userInfo = userInfoMapper.userInfoDTOToUserInfo(userInfoDTO);
        userInfo = userInfoRepository.save(userInfo);
        UserInfoDTO result = userInfoMapper.userInfoToUserInfoDTO(userInfo);
        userInfoSearchRepository.save(userInfo);
        return result;
    }

    /**
     *  Get all the userInfos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<UserInfoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserInfos");
        Page<UserInfo> result = userInfoRepository.findAll(pageable);
        return result.map(userInfo -> userInfoMapper.userInfoToUserInfoDTO(userInfo));
    }

    /**
     *  Get one userInfo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public UserInfoDTO findOne(Long id) {
        log.debug("Request to get UserInfo : {}", id);
        UserInfo userInfo = userInfoRepository.findOne(id);
        UserInfoDTO userInfoDTO = userInfoMapper.userInfoToUserInfoDTO(userInfo);
        return userInfoDTO;
    }

    /**
     *  Delete the  userInfo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete UserInfo : {}", id);
        userInfoRepository.delete(id);
        userInfoSearchRepository.delete(id);
    }

    /**
     * Search for the userInfo corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<UserInfoDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UserInfos for query {}", query);
        Page<UserInfo> result = userInfoSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(userInfo -> userInfoMapper.userInfoToUserInfoDTO(userInfo));
    }
}
