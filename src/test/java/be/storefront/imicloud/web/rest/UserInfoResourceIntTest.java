package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.ImicloudApp;

import be.storefront.imicloud.domain.UserInfo;
import be.storefront.imicloud.repository.UserInfoRepository;
import be.storefront.imicloud.service.UserInfoService;
import be.storefront.imicloud.repository.search.UserInfoSearchRepository;
import be.storefront.imicloud.service.dto.UserInfoDTO;
import be.storefront.imicloud.service.mapper.UserInfoMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserInfoResource REST controller.
 *
 * @see UserInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImicloudApp.class)
public class UserInfoResourceIntTest {

    @Inject
    private UserInfoRepository userInfoRepository;

    @Inject
    private UserInfoMapper userInfoMapper;

    @Inject
    private UserInfoService userInfoService;

    @Inject
    private UserInfoSearchRepository userInfoSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restUserInfoMockMvc;

    private UserInfo userInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserInfoResource userInfoResource = new UserInfoResource();
        ReflectionTestUtils.setField(userInfoResource, "userInfoService", userInfoService);
        this.restUserInfoMockMvc = MockMvcBuilders.standaloneSetup(userInfoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserInfo createEntity(EntityManager em) {
        UserInfo userInfo = new UserInfo();
        return userInfo;
    }

    @Before
    public void initTest() {
        userInfoSearchRepository.deleteAll();
        userInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserInfo() throws Exception {
        int databaseSizeBeforeCreate = userInfoRepository.findAll().size();

        // Create the UserInfo
        UserInfoDTO userInfoDTO = userInfoMapper.userInfoToUserInfoDTO(userInfo);

        restUserInfoMockMvc.perform(post("/api/user-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userInfoDTO)))
            .andExpect(status().isCreated());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeCreate + 1);
        UserInfo testUserInfo = userInfoList.get(userInfoList.size() - 1);

        // Validate the UserInfo in ElasticSearch
        UserInfo userInfoEs = userInfoSearchRepository.findOne(testUserInfo.getId());
        assertThat(userInfoEs).isEqualToComparingFieldByField(testUserInfo);
    }

    @Test
    @Transactional
    public void createUserInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userInfoRepository.findAll().size();

        // Create the UserInfo with an existing ID
        UserInfo existingUserInfo = new UserInfo();
        existingUserInfo.setId(1L);
        UserInfoDTO existingUserInfoDTO = userInfoMapper.userInfoToUserInfoDTO(existingUserInfo);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserInfoMockMvc.perform(post("/api/user-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingUserInfoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllUserInfos() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get all the userInfoList
        restUserInfoMockMvc.perform(get("/api/user-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userInfo.getId().intValue())));
    }

    @Test
    @Transactional
    public void getUserInfo() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);

        // Get the userInfo
        restUserInfoMockMvc.perform(get("/api/user-infos/{id}", userInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userInfo.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingUserInfo() throws Exception {
        // Get the userInfo
        restUserInfoMockMvc.perform(get("/api/user-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserInfo() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);
        userInfoSearchRepository.save(userInfo);
        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();

        // Update the userInfo
        UserInfo updatedUserInfo = userInfoRepository.findOne(userInfo.getId());
        UserInfoDTO userInfoDTO = userInfoMapper.userInfoToUserInfoDTO(updatedUserInfo);

        restUserInfoMockMvc.perform(put("/api/user-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userInfoDTO)))
            .andExpect(status().isOk());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate);
        UserInfo testUserInfo = userInfoList.get(userInfoList.size() - 1);

        // Validate the UserInfo in ElasticSearch
        UserInfo userInfoEs = userInfoSearchRepository.findOne(testUserInfo.getId());
        assertThat(userInfoEs).isEqualToComparingFieldByField(testUserInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingUserInfo() throws Exception {
        int databaseSizeBeforeUpdate = userInfoRepository.findAll().size();

        // Create the UserInfo
        UserInfoDTO userInfoDTO = userInfoMapper.userInfoToUserInfoDTO(userInfo);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUserInfoMockMvc.perform(put("/api/user-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userInfoDTO)))
            .andExpect(status().isCreated());

        // Validate the UserInfo in the database
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteUserInfo() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);
        userInfoSearchRepository.save(userInfo);
        int databaseSizeBeforeDelete = userInfoRepository.findAll().size();

        // Get the userInfo
        restUserInfoMockMvc.perform(delete("/api/user-infos/{id}", userInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean userInfoExistsInEs = userInfoSearchRepository.exists(userInfo.getId());
        assertThat(userInfoExistsInEs).isFalse();

        // Validate the database is empty
        List<UserInfo> userInfoList = userInfoRepository.findAll();
        assertThat(userInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUserInfo() throws Exception {
        // Initialize the database
        userInfoRepository.saveAndFlush(userInfo);
        userInfoSearchRepository.save(userInfo);

        // Search the userInfo
        restUserInfoMockMvc.perform(get("/api/_search/user-infos?query=id:" + userInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userInfo.getId().intValue())));
    }
}
