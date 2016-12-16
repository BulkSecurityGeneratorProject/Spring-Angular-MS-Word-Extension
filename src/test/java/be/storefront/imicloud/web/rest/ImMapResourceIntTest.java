package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.ImicloudApp;

import be.storefront.imicloud.domain.ImMap;
import be.storefront.imicloud.repository.ImMapRepository;
import be.storefront.imicloud.service.ImMapService;
import be.storefront.imicloud.repository.search.ImMapSearchRepository;
import be.storefront.imicloud.service.dto.ImMapDTO;
import be.storefront.imicloud.service.mapper.ImMapMapper;

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
 * Test class for the ImMapResource REST controller.
 *
 * @see ImMapResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImicloudApp.class)
public class ImMapResourceIntTest {

    private static final String DEFAULT_GUID = "AAAAAAAAAA";
    private static final String UPDATED_GUID = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    @Inject
    private ImMapRepository imMapRepository;

    @Inject
    private ImMapMapper imMapMapper;

    @Inject
    private ImMapService imMapService;

    @Inject
    private ImMapSearchRepository imMapSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restImMapMockMvc;

    private ImMap imMap;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ImMapResource imMapResource = new ImMapResource();
        ReflectionTestUtils.setField(imMapResource, "imMapService", imMapService);
        this.restImMapMockMvc = MockMvcBuilders.standaloneSetup(imMapResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImMap createEntity(EntityManager em) {
        ImMap imMap = new ImMap()
                .guid(DEFAULT_GUID)
                .label(DEFAULT_LABEL);
        return imMap;
    }

    @Before
    public void initTest() {
        imMapSearchRepository.deleteAll();
        imMap = createEntity(em);
    }

    @Test
    @Transactional
    public void createImMap() throws Exception {
        int databaseSizeBeforeCreate = imMapRepository.findAll().size();

        // Create the ImMap
        ImMapDTO imMapDTO = imMapMapper.imMapToImMapDTO(imMap);

        restImMapMockMvc.perform(post("/api/im-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imMapDTO)))
            .andExpect(status().isCreated());

        // Validate the ImMap in the database
        List<ImMap> imMapList = imMapRepository.findAll();
        assertThat(imMapList).hasSize(databaseSizeBeforeCreate + 1);
        ImMap testImMap = imMapList.get(imMapList.size() - 1);
        assertThat(testImMap.getGuid()).isEqualTo(DEFAULT_GUID);
        assertThat(testImMap.getLabel()).isEqualTo(DEFAULT_LABEL);

        // Validate the ImMap in ElasticSearch
        ImMap imMapEs = imMapSearchRepository.findOne(testImMap.getId());
        assertThat(imMapEs).isEqualToComparingFieldByField(testImMap);
    }

    @Test
    @Transactional
    public void createImMapWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imMapRepository.findAll().size();

        // Create the ImMap with an existing ID
        ImMap existingImMap = new ImMap();
        existingImMap.setId(1L);
        ImMapDTO existingImMapDTO = imMapMapper.imMapToImMapDTO(existingImMap);

        // An entity with an existing ID cannot be created, so this API call must fail
        restImMapMockMvc.perform(post("/api/im-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingImMapDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ImMap> imMapList = imMapRepository.findAll();
        assertThat(imMapList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkLabelIsRequired() throws Exception {
        int databaseSizeBeforeTest = imMapRepository.findAll().size();
        // set the field null
        imMap.setLabel(null);

        // Create the ImMap, which fails.
        ImMapDTO imMapDTO = imMapMapper.imMapToImMapDTO(imMap);

        restImMapMockMvc.perform(post("/api/im-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imMapDTO)))
            .andExpect(status().isBadRequest());

        List<ImMap> imMapList = imMapRepository.findAll();
        assertThat(imMapList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllImMaps() throws Exception {
        // Initialize the database
        imMapRepository.saveAndFlush(imMap);

        // Get all the imMapList
        restImMapMockMvc.perform(get("/api/im-maps?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].guid").value(hasItem(DEFAULT_GUID.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }

    @Test
    @Transactional
    public void getImMap() throws Exception {
        // Initialize the database
        imMapRepository.saveAndFlush(imMap);

        // Get the imMap
        restImMapMockMvc.perform(get("/api/im-maps/{id}", imMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(imMap.getId().intValue()))
            .andExpect(jsonPath("$.guid").value(DEFAULT_GUID.toString()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingImMap() throws Exception {
        // Get the imMap
        restImMapMockMvc.perform(get("/api/im-maps/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImMap() throws Exception {
        // Initialize the database
        imMapRepository.saveAndFlush(imMap);
        imMapSearchRepository.save(imMap);
        int databaseSizeBeforeUpdate = imMapRepository.findAll().size();

        // Update the imMap
        ImMap updatedImMap = imMapRepository.findOne(imMap.getId());
        updatedImMap
                .guid(UPDATED_GUID)
                .label(UPDATED_LABEL);
        ImMapDTO imMapDTO = imMapMapper.imMapToImMapDTO(updatedImMap);

        restImMapMockMvc.perform(put("/api/im-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imMapDTO)))
            .andExpect(status().isOk());

        // Validate the ImMap in the database
        List<ImMap> imMapList = imMapRepository.findAll();
        assertThat(imMapList).hasSize(databaseSizeBeforeUpdate);
        ImMap testImMap = imMapList.get(imMapList.size() - 1);
        assertThat(testImMap.getGuid()).isEqualTo(UPDATED_GUID);
        assertThat(testImMap.getLabel()).isEqualTo(UPDATED_LABEL);

        // Validate the ImMap in ElasticSearch
        ImMap imMapEs = imMapSearchRepository.findOne(testImMap.getId());
        assertThat(imMapEs).isEqualToComparingFieldByField(testImMap);
    }

    @Test
    @Transactional
    public void updateNonExistingImMap() throws Exception {
        int databaseSizeBeforeUpdate = imMapRepository.findAll().size();

        // Create the ImMap
        ImMapDTO imMapDTO = imMapMapper.imMapToImMapDTO(imMap);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restImMapMockMvc.perform(put("/api/im-maps")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imMapDTO)))
            .andExpect(status().isCreated());

        // Validate the ImMap in the database
        List<ImMap> imMapList = imMapRepository.findAll();
        assertThat(imMapList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteImMap() throws Exception {
        // Initialize the database
        imMapRepository.saveAndFlush(imMap);
        imMapSearchRepository.save(imMap);
        int databaseSizeBeforeDelete = imMapRepository.findAll().size();

        // Get the imMap
        restImMapMockMvc.perform(delete("/api/im-maps/{id}", imMap.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean imMapExistsInEs = imMapSearchRepository.exists(imMap.getId());
        assertThat(imMapExistsInEs).isFalse();

        // Validate the database is empty
        List<ImMap> imMapList = imMapRepository.findAll();
        assertThat(imMapList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchImMap() throws Exception {
        // Initialize the database
        imMapRepository.saveAndFlush(imMap);
        imMapSearchRepository.save(imMap);

        // Search the imMap
        restImMapMockMvc.perform(get("/api/_search/im-maps?query=id:" + imMap.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imMap.getId().intValue())))
            .andExpect(jsonPath("$.[*].guid").value(hasItem(DEFAULT_GUID.toString())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())));
    }
}
