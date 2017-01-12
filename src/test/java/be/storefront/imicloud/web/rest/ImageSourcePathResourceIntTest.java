package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.ImicloudApp;

import be.storefront.imicloud.domain.ImageSourcePath;
import be.storefront.imicloud.repository.ImageSourcePathRepository;
import be.storefront.imicloud.service.ImageSourcePathService;
import be.storefront.imicloud.repository.search.ImageSourcePathSearchRepository;
import be.storefront.imicloud.service.dto.ImageSourcePathDTO;
import be.storefront.imicloud.service.mapper.ImageSourcePathMapper;

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
 * Test class for the ImageSourcePathResource REST controller.
 *
 * @see ImageSourcePathResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImicloudApp.class)
public class ImageSourcePathResourceIntTest {

    private static final String DEFAULT_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_UPLOAD_COMPLETE = false;
    private static final Boolean UPDATED_UPLOAD_COMPLETE = true;

    @Inject
    private ImageSourcePathRepository imageSourcePathRepository;

    @Inject
    private ImageSourcePathMapper imageSourcePathMapper;

    @Inject
    private ImageSourcePathService imageSourcePathService;

    @Inject
    private ImageSourcePathSearchRepository imageSourcePathSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restImageSourcePathMockMvc;

    private ImageSourcePath imageSourcePath;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ImageSourcePathResource imageSourcePathResource = new ImageSourcePathResource();
        ReflectionTestUtils.setField(imageSourcePathResource, "imageSourcePathService", imageSourcePathService);
        this.restImageSourcePathMockMvc = MockMvcBuilders.standaloneSetup(imageSourcePathResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImageSourcePath createEntity(EntityManager em) {
        ImageSourcePath imageSourcePath = new ImageSourcePath()
                .source(DEFAULT_SOURCE)
                .uploadComplete(DEFAULT_UPLOAD_COMPLETE);
        return imageSourcePath;
    }

    @Before
    public void initTest() {
        imageSourcePathSearchRepository.deleteAll();
        imageSourcePath = createEntity(em);
    }

    @Test
    @Transactional
    public void createImageSourcePath() throws Exception {
        int databaseSizeBeforeCreate = imageSourcePathRepository.findAll().size();

        // Create the ImageSourcePath
        ImageSourcePathDTO imageSourcePathDTO = imageSourcePathMapper.imageSourcePathToImageSourcePathDTO(imageSourcePath);

        restImageSourcePathMockMvc.perform(post("/api/image-source-paths")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageSourcePathDTO)))
            .andExpect(status().isCreated());

        // Validate the ImageSourcePath in the database
        List<ImageSourcePath> imageSourcePathList = imageSourcePathRepository.findAll();
        assertThat(imageSourcePathList).hasSize(databaseSizeBeforeCreate + 1);
        ImageSourcePath testImageSourcePath = imageSourcePathList.get(imageSourcePathList.size() - 1);
        assertThat(testImageSourcePath.getSource()).isEqualTo(DEFAULT_SOURCE);
        assertThat(testImageSourcePath.isUploadComplete()).isEqualTo(DEFAULT_UPLOAD_COMPLETE);

        // Validate the ImageSourcePath in ElasticSearch
        ImageSourcePath imageSourcePathEs = imageSourcePathSearchRepository.findOne(testImageSourcePath.getId());
        assertThat(imageSourcePathEs).isEqualToComparingFieldByField(testImageSourcePath);
    }

    @Test
    @Transactional
    public void createImageSourcePathWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imageSourcePathRepository.findAll().size();

        // Create the ImageSourcePath with an existing ID
        ImageSourcePath existingImageSourcePath = new ImageSourcePath();
        existingImageSourcePath.setId(1L);
        ImageSourcePathDTO existingImageSourcePathDTO = imageSourcePathMapper.imageSourcePathToImageSourcePathDTO(existingImageSourcePath);

        // An entity with an existing ID cannot be created, so this API call must fail
        restImageSourcePathMockMvc.perform(post("/api/image-source-paths")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingImageSourcePathDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ImageSourcePath> imageSourcePathList = imageSourcePathRepository.findAll();
        assertThat(imageSourcePathList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkSourceIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageSourcePathRepository.findAll().size();
        // set the field null
        imageSourcePath.setSource(null);

        // Create the ImageSourcePath, which fails.
        ImageSourcePathDTO imageSourcePathDTO = imageSourcePathMapper.imageSourcePathToImageSourcePathDTO(imageSourcePath);

        restImageSourcePathMockMvc.perform(post("/api/image-source-paths")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageSourcePathDTO)))
            .andExpect(status().isBadRequest());

        List<ImageSourcePath> imageSourcePathList = imageSourcePathRepository.findAll();
        assertThat(imageSourcePathList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllImageSourcePaths() throws Exception {
        // Initialize the database
        imageSourcePathRepository.saveAndFlush(imageSourcePath);

        // Get all the imageSourcePathList
        restImageSourcePathMockMvc.perform(get("/api/image-source-paths?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imageSourcePath.getId().intValue())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())))
            .andExpect(jsonPath("$.[*].uploadComplete").value(hasItem(DEFAULT_UPLOAD_COMPLETE.booleanValue())));
    }

    @Test
    @Transactional
    public void getImageSourcePath() throws Exception {
        // Initialize the database
        imageSourcePathRepository.saveAndFlush(imageSourcePath);

        // Get the imageSourcePath
        restImageSourcePathMockMvc.perform(get("/api/image-source-paths/{id}", imageSourcePath.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(imageSourcePath.getId().intValue()))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE.toString()))
            .andExpect(jsonPath("$.uploadComplete").value(DEFAULT_UPLOAD_COMPLETE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingImageSourcePath() throws Exception {
        // Get the imageSourcePath
        restImageSourcePathMockMvc.perform(get("/api/image-source-paths/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImageSourcePath() throws Exception {
        // Initialize the database
        imageSourcePathRepository.saveAndFlush(imageSourcePath);
        imageSourcePathSearchRepository.save(imageSourcePath);
        int databaseSizeBeforeUpdate = imageSourcePathRepository.findAll().size();

        // Update the imageSourcePath
        ImageSourcePath updatedImageSourcePath = imageSourcePathRepository.findOne(imageSourcePath.getId());
        updatedImageSourcePath
                .source(UPDATED_SOURCE)
                .uploadComplete(UPDATED_UPLOAD_COMPLETE);
        ImageSourcePathDTO imageSourcePathDTO = imageSourcePathMapper.imageSourcePathToImageSourcePathDTO(updatedImageSourcePath);

        restImageSourcePathMockMvc.perform(put("/api/image-source-paths")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageSourcePathDTO)))
            .andExpect(status().isOk());

        // Validate the ImageSourcePath in the database
        List<ImageSourcePath> imageSourcePathList = imageSourcePathRepository.findAll();
        assertThat(imageSourcePathList).hasSize(databaseSizeBeforeUpdate);
        ImageSourcePath testImageSourcePath = imageSourcePathList.get(imageSourcePathList.size() - 1);
        assertThat(testImageSourcePath.getSource()).isEqualTo(UPDATED_SOURCE);
        assertThat(testImageSourcePath.isUploadComplete()).isEqualTo(UPDATED_UPLOAD_COMPLETE);

        // Validate the ImageSourcePath in ElasticSearch
        ImageSourcePath imageSourcePathEs = imageSourcePathSearchRepository.findOne(testImageSourcePath.getId());
        assertThat(imageSourcePathEs).isEqualToComparingFieldByField(testImageSourcePath);
    }

    @Test
    @Transactional
    public void updateNonExistingImageSourcePath() throws Exception {
        int databaseSizeBeforeUpdate = imageSourcePathRepository.findAll().size();

        // Create the ImageSourcePath
        ImageSourcePathDTO imageSourcePathDTO = imageSourcePathMapper.imageSourcePathToImageSourcePathDTO(imageSourcePath);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restImageSourcePathMockMvc.perform(put("/api/image-source-paths")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageSourcePathDTO)))
            .andExpect(status().isCreated());

        // Validate the ImageSourcePath in the database
        List<ImageSourcePath> imageSourcePathList = imageSourcePathRepository.findAll();
        assertThat(imageSourcePathList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteImageSourcePath() throws Exception {
        // Initialize the database
        imageSourcePathRepository.saveAndFlush(imageSourcePath);
        imageSourcePathSearchRepository.save(imageSourcePath);
        int databaseSizeBeforeDelete = imageSourcePathRepository.findAll().size();

        // Get the imageSourcePath
        restImageSourcePathMockMvc.perform(delete("/api/image-source-paths/{id}", imageSourcePath.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean imageSourcePathExistsInEs = imageSourcePathSearchRepository.exists(imageSourcePath.getId());
        assertThat(imageSourcePathExistsInEs).isFalse();

        // Validate the database is empty
        List<ImageSourcePath> imageSourcePathList = imageSourcePathRepository.findAll();
        assertThat(imageSourcePathList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchImageSourcePath() throws Exception {
        // Initialize the database
        imageSourcePathRepository.saveAndFlush(imageSourcePath);
        imageSourcePathSearchRepository.save(imageSourcePath);

        // Search the imageSourcePath
        restImageSourcePathMockMvc.perform(get("/api/_search/image-source-paths?query=id:" + imageSourcePath.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imageSourcePath.getId().intValue())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())))
            .andExpect(jsonPath("$.[*].uploadComplete").value(hasItem(DEFAULT_UPLOAD_COMPLETE.booleanValue())));
    }
}
