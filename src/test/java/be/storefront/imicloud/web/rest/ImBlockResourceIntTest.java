package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.ImicloudApp;

import be.storefront.imicloud.domain.ImBlock;
import be.storefront.imicloud.repository.ImBlockRepository;
import be.storefront.imicloud.service.ImBlockService;
import be.storefront.imicloud.repository.search.ImBlockSearchRepository;
import be.storefront.imicloud.service.dto.ImBlockDTO;
import be.storefront.imicloud.service.mapper.ImBlockMapper;

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
import org.springframework.util.Base64Utils;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ImBlockResource REST controller.
 *
 * @see ImBlockResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImicloudApp.class)
public class ImBlockResourceIntTest {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Float DEFAULT_POSITION = 1F;
    private static final Float UPDATED_POSITION = 2F;

    @Inject
    private ImBlockRepository imBlockRepository;

    @Inject
    private ImBlockMapper imBlockMapper;

    @Inject
    private ImBlockService imBlockService;

    @Inject
    private ImBlockSearchRepository imBlockSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restImBlockMockMvc;

    private ImBlock imBlock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ImBlockResource imBlockResource = new ImBlockResource();
        ReflectionTestUtils.setField(imBlockResource, "imBlockService", imBlockService);
        this.restImBlockMockMvc = MockMvcBuilders.standaloneSetup(imBlockResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImBlock createEntity(EntityManager em) {
        ImBlock imBlock = new ImBlock()
                .label(DEFAULT_LABEL)
                .content(DEFAULT_CONTENT)
                .position(DEFAULT_POSITION);
        return imBlock;
    }

    @Before
    public void initTest() {
        imBlockSearchRepository.deleteAll();
        imBlock = createEntity(em);
    }

    @Test
    @Transactional
    public void createImBlock() throws Exception {
        int databaseSizeBeforeCreate = imBlockRepository.findAll().size();

        // Create the ImBlock
        ImBlockDTO imBlockDTO = imBlockMapper.imBlockToImBlockDTO(imBlock);

        restImBlockMockMvc.perform(post("/api/im-blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imBlockDTO)))
            .andExpect(status().isCreated());

        // Validate the ImBlock in the database
        List<ImBlock> imBlockList = imBlockRepository.findAll();
        assertThat(imBlockList).hasSize(databaseSizeBeforeCreate + 1);
        ImBlock testImBlock = imBlockList.get(imBlockList.size() - 1);
        assertThat(testImBlock.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testImBlock.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testImBlock.getPosition()).isEqualTo(DEFAULT_POSITION);

        // Validate the ImBlock in ElasticSearch
        ImBlock imBlockEs = imBlockSearchRepository.findOne(testImBlock.getId());
        assertThat(imBlockEs).isEqualToComparingFieldByField(testImBlock);
    }

    @Test
    @Transactional
    public void createImBlockWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imBlockRepository.findAll().size();

        // Create the ImBlock with an existing ID
        ImBlock existingImBlock = new ImBlock();
        existingImBlock.setId(1L);
        ImBlockDTO existingImBlockDTO = imBlockMapper.imBlockToImBlockDTO(existingImBlock);

        // An entity with an existing ID cannot be created, so this API call must fail
        restImBlockMockMvc.perform(post("/api/im-blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingImBlockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ImBlock> imBlockList = imBlockRepository.findAll();
        assertThat(imBlockList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPositionIsRequired() throws Exception {
        int databaseSizeBeforeTest = imBlockRepository.findAll().size();
        // set the field null
        imBlock.setPosition(null);

        // Create the ImBlock, which fails.
        ImBlockDTO imBlockDTO = imBlockMapper.imBlockToImBlockDTO(imBlock);

        restImBlockMockMvc.perform(post("/api/im-blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imBlockDTO)))
            .andExpect(status().isBadRequest());

        List<ImBlock> imBlockList = imBlockRepository.findAll();
        assertThat(imBlockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllImBlocks() throws Exception {
        // Initialize the database
        imBlockRepository.saveAndFlush(imBlock);

        // Get all the imBlockList
        restImBlockMockMvc.perform(get("/api/im-blocks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imBlock.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.doubleValue())));
    }

    @Test
    @Transactional
    public void getImBlock() throws Exception {
        // Initialize the database
        imBlockRepository.saveAndFlush(imBlock);

        // Get the imBlock
        restImBlockMockMvc.perform(get("/api/im-blocks/{id}", imBlock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(imBlock.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingImBlock() throws Exception {
        // Get the imBlock
        restImBlockMockMvc.perform(get("/api/im-blocks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImBlock() throws Exception {
        // Initialize the database
        imBlockRepository.saveAndFlush(imBlock);
        imBlockSearchRepository.save(imBlock);
        int databaseSizeBeforeUpdate = imBlockRepository.findAll().size();

        // Update the imBlock
        ImBlock updatedImBlock = imBlockRepository.findOne(imBlock.getId());
        updatedImBlock
                .label(UPDATED_LABEL)
                .content(UPDATED_CONTENT)
                .position(UPDATED_POSITION);
        ImBlockDTO imBlockDTO = imBlockMapper.imBlockToImBlockDTO(updatedImBlock);

        restImBlockMockMvc.perform(put("/api/im-blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imBlockDTO)))
            .andExpect(status().isOk());

        // Validate the ImBlock in the database
        List<ImBlock> imBlockList = imBlockRepository.findAll();
        assertThat(imBlockList).hasSize(databaseSizeBeforeUpdate);
        ImBlock testImBlock = imBlockList.get(imBlockList.size() - 1);
        assertThat(testImBlock.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testImBlock.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testImBlock.getPosition()).isEqualTo(UPDATED_POSITION);

        // Validate the ImBlock in ElasticSearch
        ImBlock imBlockEs = imBlockSearchRepository.findOne(testImBlock.getId());
        assertThat(imBlockEs).isEqualToComparingFieldByField(testImBlock);
    }

    @Test
    @Transactional
    public void updateNonExistingImBlock() throws Exception {
        int databaseSizeBeforeUpdate = imBlockRepository.findAll().size();

        // Create the ImBlock
        ImBlockDTO imBlockDTO = imBlockMapper.imBlockToImBlockDTO(imBlock);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restImBlockMockMvc.perform(put("/api/im-blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imBlockDTO)))
            .andExpect(status().isCreated());

        // Validate the ImBlock in the database
        List<ImBlock> imBlockList = imBlockRepository.findAll();
        assertThat(imBlockList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteImBlock() throws Exception {
        // Initialize the database
        imBlockRepository.saveAndFlush(imBlock);
        imBlockSearchRepository.save(imBlock);
        int databaseSizeBeforeDelete = imBlockRepository.findAll().size();

        // Get the imBlock
        restImBlockMockMvc.perform(delete("/api/im-blocks/{id}", imBlock.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean imBlockExistsInEs = imBlockSearchRepository.exists(imBlock.getId());
        assertThat(imBlockExistsInEs).isFalse();

        // Validate the database is empty
        List<ImBlock> imBlockList = imBlockRepository.findAll();
        assertThat(imBlockList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchImBlock() throws Exception {
        // Initialize the database
        imBlockRepository.saveAndFlush(imBlock);
        imBlockSearchRepository.save(imBlock);

        // Search the imBlock
        restImBlockMockMvc.perform(get("/api/_search/im-blocks?query=id:" + imBlock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imBlock.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].position").value(hasItem(DEFAULT_POSITION.doubleValue())));
    }
}
