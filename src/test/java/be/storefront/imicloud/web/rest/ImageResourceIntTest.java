package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.ImicloudApp;

import be.storefront.imicloud.domain.Image;
import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.repository.ImageRepository;
import be.storefront.imicloud.service.ImageService;
import be.storefront.imicloud.repository.search.ImageSearchRepository;
import be.storefront.imicloud.service.dto.ImageDTO;
import be.storefront.imicloud.service.mapper.ImageMapper;

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
 * Test class for the ImageResource REST controller.
 *
 * @see ImageResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImicloudApp.class)
public class ImageResourceIntTest {

    private static final String DEFAULT_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_IMAGE_WIDTH = 1;
    private static final Integer UPDATED_IMAGE_WIDTH = 2;

    private static final Integer DEFAULT_IMAGE_HEIGHT = 1;
    private static final Integer UPDATED_IMAGE_HEIGHT = 2;

    private static final Long DEFAULT_CONTENT_LENGTH = 1L;
    private static final Long UPDATED_CONTENT_LENGTH = 2L;

    private static final String DEFAULT_SECRET = "AAAAAAAAAA";
    private static final String UPDATED_SECRET = "BBBBBBBBBB";

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private ImageMapper imageMapper;

    @Inject
    private ImageService imageService;

    @Inject
    private ImageSearchRepository imageSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restImageMockMvc;

    private Image image;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ImageResource imageResource = new ImageResource();
        ReflectionTestUtils.setField(imageResource, "imageService", imageService);
        this.restImageMockMvc = MockMvcBuilders.standaloneSetup(imageResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Image createEntity(EntityManager em) {
        Image image = new Image()
                .filename(DEFAULT_FILENAME)
                .contentType(DEFAULT_CONTENT_TYPE)
                .imageWidth(DEFAULT_IMAGE_WIDTH)
                .imageHeight(DEFAULT_IMAGE_HEIGHT)
                .contentLength(DEFAULT_CONTENT_LENGTH)
                .secret(DEFAULT_SECRET);
        // Add required entity
        User uploadedByUser = UserResourceIntTest.createEntity(em);
        em.persist(uploadedByUser);
        em.flush();
        image.setUploadedByUser(uploadedByUser);
        return image;
    }

    @Before
    public void initTest() {
        imageSearchRepository.deleteAll();
        image = createEntity(em);
    }

    @Test
    @Transactional
    public void createImage() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().size();

        // Create the Image
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isCreated());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate + 1);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getFilename()).isEqualTo(DEFAULT_FILENAME);
        assertThat(testImage.getContentType()).isEqualTo(DEFAULT_CONTENT_TYPE);
        assertThat(testImage.getImageWidth()).isEqualTo(DEFAULT_IMAGE_WIDTH);
        assertThat(testImage.getImageHeight()).isEqualTo(DEFAULT_IMAGE_HEIGHT);
        assertThat(testImage.getContentLength()).isEqualTo(DEFAULT_CONTENT_LENGTH);
        assertThat(testImage.getSecret()).isEqualTo(DEFAULT_SECRET);

        // Validate the Image in ElasticSearch
        Image imageEs = imageSearchRepository.findOne(testImage.getId());
        assertThat(imageEs).isEqualToComparingFieldByField(testImage);
    }

    @Test
    @Transactional
    public void createImageWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imageRepository.findAll().size();

        // Create the Image with an existing ID
        Image existingImage = new Image();
        existingImage.setId(1L);
        ImageDTO existingImageDTO = imageMapper.imageToImageDTO(existingImage);

        // An entity with an existing ID cannot be created, so this API call must fail
        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingImageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkFilenameIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageRepository.findAll().size();
        // set the field null
        image.setFilename(null);

        // Create the Image, which fails.
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isBadRequest());

        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageRepository.findAll().size();
        // set the field null
        image.setContentType(null);

        // Create the Image, which fails.
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isBadRequest());

        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkImageWidthIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageRepository.findAll().size();
        // set the field null
        image.setImageWidth(null);

        // Create the Image, which fails.
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isBadRequest());

        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkImageHeightIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageRepository.findAll().size();
        // set the field null
        image.setImageHeight(null);

        // Create the Image, which fails.
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isBadRequest());

        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkContentLengthIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageRepository.findAll().size();
        // set the field null
        image.setContentLength(null);

        // Create the Image, which fails.
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isBadRequest());

        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSecretIsRequired() throws Exception {
        int databaseSizeBeforeTest = imageRepository.findAll().size();
        // set the field null
        image.setSecret(null);

        // Create the Image, which fails.
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        restImageMockMvc.perform(post("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isBadRequest());

        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllImages() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get all the imageList
        restImageMockMvc.perform(get("/api/images?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.getId().intValue())))
            .andExpect(jsonPath("$.[*].filename").value(hasItem(DEFAULT_FILENAME.toString())))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].imageWidth").value(hasItem(DEFAULT_IMAGE_WIDTH)))
            .andExpect(jsonPath("$.[*].imageHeight").value(hasItem(DEFAULT_IMAGE_HEIGHT)))
            .andExpect(jsonPath("$.[*].contentLength").value(hasItem(DEFAULT_CONTENT_LENGTH.intValue())))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET.toString())));
    }

    @Test
    @Transactional
    public void getImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);

        // Get the image
        restImageMockMvc.perform(get("/api/images/{id}", image.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(image.getId().intValue()))
            .andExpect(jsonPath("$.filename").value(DEFAULT_FILENAME.toString()))
            .andExpect(jsonPath("$.contentType").value(DEFAULT_CONTENT_TYPE.toString()))
            .andExpect(jsonPath("$.imageWidth").value(DEFAULT_IMAGE_WIDTH))
            .andExpect(jsonPath("$.imageHeight").value(DEFAULT_IMAGE_HEIGHT))
            .andExpect(jsonPath("$.contentLength").value(DEFAULT_CONTENT_LENGTH.intValue()))
            .andExpect(jsonPath("$.secret").value(DEFAULT_SECRET.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingImage() throws Exception {
        // Get the image
        restImageMockMvc.perform(get("/api/images/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);
        imageSearchRepository.save(image);
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // Update the image
        Image updatedImage = imageRepository.findOne(image.getId());
        updatedImage
                .filename(UPDATED_FILENAME)
                .contentType(UPDATED_CONTENT_TYPE)
                .imageWidth(UPDATED_IMAGE_WIDTH)
                .imageHeight(UPDATED_IMAGE_HEIGHT)
                .contentLength(UPDATED_CONTENT_LENGTH)
                .secret(UPDATED_SECRET);
        ImageDTO imageDTO = imageMapper.imageToImageDTO(updatedImage);

        restImageMockMvc.perform(put("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isOk());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate);
        Image testImage = imageList.get(imageList.size() - 1);
        assertThat(testImage.getFilename()).isEqualTo(UPDATED_FILENAME);
        assertThat(testImage.getContentType()).isEqualTo(UPDATED_CONTENT_TYPE);
        assertThat(testImage.getImageWidth()).isEqualTo(UPDATED_IMAGE_WIDTH);
        assertThat(testImage.getImageHeight()).isEqualTo(UPDATED_IMAGE_HEIGHT);
        assertThat(testImage.getContentLength()).isEqualTo(UPDATED_CONTENT_LENGTH);
        assertThat(testImage.getSecret()).isEqualTo(UPDATED_SECRET);

        // Validate the Image in ElasticSearch
        Image imageEs = imageSearchRepository.findOne(testImage.getId());
        assertThat(imageEs).isEqualToComparingFieldByField(testImage);
    }

    @Test
    @Transactional
    public void updateNonExistingImage() throws Exception {
        int databaseSizeBeforeUpdate = imageRepository.findAll().size();

        // Create the Image
        ImageDTO imageDTO = imageMapper.imageToImageDTO(image);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restImageMockMvc.perform(put("/api/images")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imageDTO)))
            .andExpect(status().isCreated());

        // Validate the Image in the database
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);
        imageSearchRepository.save(image);
        int databaseSizeBeforeDelete = imageRepository.findAll().size();

        // Get the image
        restImageMockMvc.perform(delete("/api/images/{id}", image.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean imageExistsInEs = imageSearchRepository.exists(image.getId());
        assertThat(imageExistsInEs).isFalse();

        // Validate the database is empty
        List<Image> imageList = imageRepository.findAll();
        assertThat(imageList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchImage() throws Exception {
        // Initialize the database
        imageRepository.saveAndFlush(image);
        imageSearchRepository.save(image);

        // Search the image
        restImageMockMvc.perform(get("/api/_search/images?query=id:" + image.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(image.getId().intValue())))
            .andExpect(jsonPath("$.[*].filename").value(hasItem(DEFAULT_FILENAME.toString())))
            .andExpect(jsonPath("$.[*].contentType").value(hasItem(DEFAULT_CONTENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].imageWidth").value(hasItem(DEFAULT_IMAGE_WIDTH)))
            .andExpect(jsonPath("$.[*].imageHeight").value(hasItem(DEFAULT_IMAGE_HEIGHT)))
            .andExpect(jsonPath("$.[*].contentLength").value(hasItem(DEFAULT_CONTENT_LENGTH.intValue())))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET.toString())));
    }
}
