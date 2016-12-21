package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.ImicloudApp;

import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.repository.ImDocumentRepository;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.repository.search.ImDocumentSearchRepository;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.mapper.ImDocumentMapper;

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
 * Test class for the ImDocumentResource REST controller.
 *
 * @see ImDocumentResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImicloudApp.class)
public class ImDocumentResourceIntTest {

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_XML = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_XML = "BBBBBBBBBB";

    private static final String DEFAULT_SECRET = "AAAAAAAAAA";
    private static final String UPDATED_SECRET = "BBBBBBBBBB";

    @Inject
    private ImDocumentRepository imDocumentRepository;

    @Inject
    private ImDocumentMapper imDocumentMapper;

    @Inject
    private ImDocumentService imDocumentService;

    @Inject
    private ImDocumentSearchRepository imDocumentSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restImDocumentMockMvc;

    private ImDocument imDocument;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ImDocumentResource imDocumentResource = new ImDocumentResource();
        ReflectionTestUtils.setField(imDocumentResource, "imDocumentService", imDocumentService);
        this.restImDocumentMockMvc = MockMvcBuilders.standaloneSetup(imDocumentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ImDocument createEntity(EntityManager em) {
        ImDocument imDocument = new ImDocument()
                .language(DEFAULT_LANGUAGE)
                .password(DEFAULT_PASSWORD)
                .originalFilename(DEFAULT_ORIGINAL_FILENAME)
                .originalXml(DEFAULT_ORIGINAL_XML)
                .secret(DEFAULT_SECRET);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        imDocument.setUser(user);
        return imDocument;
    }

    @Before
    public void initTest() {
        imDocumentSearchRepository.deleteAll();
        imDocument = createEntity(em);
    }

    @Test
    @Transactional
    public void createImDocument() throws Exception {
        int databaseSizeBeforeCreate = imDocumentRepository.findAll().size();

        // Create the ImDocument
        ImDocumentDTO imDocumentDTO = imDocumentMapper.imDocumentToImDocumentDTO(imDocument);

        restImDocumentMockMvc.perform(post("/api/im-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imDocumentDTO)))
            .andExpect(status().isCreated());

        // Validate the ImDocument in the database
        List<ImDocument> imDocumentList = imDocumentRepository.findAll();
        assertThat(imDocumentList).hasSize(databaseSizeBeforeCreate + 1);
        ImDocument testImDocument = imDocumentList.get(imDocumentList.size() - 1);
        assertThat(testImDocument.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
        assertThat(testImDocument.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testImDocument.getOriginalFilename()).isEqualTo(DEFAULT_ORIGINAL_FILENAME);
        assertThat(testImDocument.getOriginalXml()).isEqualTo(DEFAULT_ORIGINAL_XML);
        assertThat(testImDocument.getSecret()).isEqualTo(DEFAULT_SECRET);

        // Validate the ImDocument in ElasticSearch
        ImDocument imDocumentEs = imDocumentSearchRepository.findOne(testImDocument.getId());
        assertThat(imDocumentEs).isEqualToComparingFieldByField(testImDocument);
    }

    @Test
    @Transactional
    public void createImDocumentWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = imDocumentRepository.findAll().size();

        // Create the ImDocument with an existing ID
        ImDocument existingImDocument = new ImDocument();
        existingImDocument.setId(1L);
        ImDocumentDTO existingImDocumentDTO = imDocumentMapper.imDocumentToImDocumentDTO(existingImDocument);

        // An entity with an existing ID cannot be created, so this API call must fail
        restImDocumentMockMvc.perform(post("/api/im-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingImDocumentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ImDocument> imDocumentList = imDocumentRepository.findAll();
        assertThat(imDocumentList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkOriginalFilenameIsRequired() throws Exception {
        int databaseSizeBeforeTest = imDocumentRepository.findAll().size();
        // set the field null
        imDocument.setOriginalFilename(null);

        // Create the ImDocument, which fails.
        ImDocumentDTO imDocumentDTO = imDocumentMapper.imDocumentToImDocumentDTO(imDocument);

        restImDocumentMockMvc.perform(post("/api/im-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<ImDocument> imDocumentList = imDocumentRepository.findAll();
        assertThat(imDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOriginalXmlIsRequired() throws Exception {
        int databaseSizeBeforeTest = imDocumentRepository.findAll().size();
        // set the field null
        imDocument.setOriginalXml(null);

        // Create the ImDocument, which fails.
        ImDocumentDTO imDocumentDTO = imDocumentMapper.imDocumentToImDocumentDTO(imDocument);

        restImDocumentMockMvc.perform(post("/api/im-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<ImDocument> imDocumentList = imDocumentRepository.findAll();
        assertThat(imDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSecretIsRequired() throws Exception {
        int databaseSizeBeforeTest = imDocumentRepository.findAll().size();
        // set the field null
        imDocument.setSecret(null);

        // Create the ImDocument, which fails.
        ImDocumentDTO imDocumentDTO = imDocumentMapper.imDocumentToImDocumentDTO(imDocument);

        restImDocumentMockMvc.perform(post("/api/im-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imDocumentDTO)))
            .andExpect(status().isBadRequest());

        List<ImDocument> imDocumentList = imDocumentRepository.findAll();
        assertThat(imDocumentList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllImDocuments() throws Exception {
        // Initialize the database
        imDocumentRepository.saveAndFlush(imDocument);

        // Get all the imDocumentList
        restImDocumentMockMvc.perform(get("/api/im-documents?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())))
            .andExpect(jsonPath("$.[*].originalFilename").value(hasItem(DEFAULT_ORIGINAL_FILENAME.toString())))
            .andExpect(jsonPath("$.[*].originalXml").value(hasItem(DEFAULT_ORIGINAL_XML.toString())))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET.toString())));
    }

    @Test
    @Transactional
    public void getImDocument() throws Exception {
        // Initialize the database
        imDocumentRepository.saveAndFlush(imDocument);

        // Get the imDocument
        restImDocumentMockMvc.perform(get("/api/im-documents/{id}", imDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(imDocument.getId().intValue()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()))
            .andExpect(jsonPath("$.originalFilename").value(DEFAULT_ORIGINAL_FILENAME.toString()))
            .andExpect(jsonPath("$.originalXml").value(DEFAULT_ORIGINAL_XML.toString()))
            .andExpect(jsonPath("$.secret").value(DEFAULT_SECRET.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingImDocument() throws Exception {
        // Get the imDocument
        restImDocumentMockMvc.perform(get("/api/im-documents/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateImDocument() throws Exception {
        // Initialize the database
        imDocumentRepository.saveAndFlush(imDocument);
        imDocumentSearchRepository.save(imDocument);
        int databaseSizeBeforeUpdate = imDocumentRepository.findAll().size();

        // Update the imDocument
        ImDocument updatedImDocument = imDocumentRepository.findOne(imDocument.getId());
        updatedImDocument
                .language(UPDATED_LANGUAGE)
                .password(UPDATED_PASSWORD)
                .originalFilename(UPDATED_ORIGINAL_FILENAME)
                .originalXml(UPDATED_ORIGINAL_XML)
                .secret(UPDATED_SECRET);
        ImDocumentDTO imDocumentDTO = imDocumentMapper.imDocumentToImDocumentDTO(updatedImDocument);

        restImDocumentMockMvc.perform(put("/api/im-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imDocumentDTO)))
            .andExpect(status().isOk());

        // Validate the ImDocument in the database
        List<ImDocument> imDocumentList = imDocumentRepository.findAll();
        assertThat(imDocumentList).hasSize(databaseSizeBeforeUpdate);
        ImDocument testImDocument = imDocumentList.get(imDocumentList.size() - 1);
        assertThat(testImDocument.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
        assertThat(testImDocument.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testImDocument.getOriginalFilename()).isEqualTo(UPDATED_ORIGINAL_FILENAME);
        assertThat(testImDocument.getOriginalXml()).isEqualTo(UPDATED_ORIGINAL_XML);
        assertThat(testImDocument.getSecret()).isEqualTo(UPDATED_SECRET);

        // Validate the ImDocument in ElasticSearch
        ImDocument imDocumentEs = imDocumentSearchRepository.findOne(testImDocument.getId());
        assertThat(imDocumentEs).isEqualToComparingFieldByField(testImDocument);
    }

    @Test
    @Transactional
    public void updateNonExistingImDocument() throws Exception {
        int databaseSizeBeforeUpdate = imDocumentRepository.findAll().size();

        // Create the ImDocument
        ImDocumentDTO imDocumentDTO = imDocumentMapper.imDocumentToImDocumentDTO(imDocument);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restImDocumentMockMvc.perform(put("/api/im-documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(imDocumentDTO)))
            .andExpect(status().isCreated());

        // Validate the ImDocument in the database
        List<ImDocument> imDocumentList = imDocumentRepository.findAll();
        assertThat(imDocumentList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteImDocument() throws Exception {
        // Initialize the database
        imDocumentRepository.saveAndFlush(imDocument);
        imDocumentSearchRepository.save(imDocument);
        int databaseSizeBeforeDelete = imDocumentRepository.findAll().size();

        // Get the imDocument
        restImDocumentMockMvc.perform(delete("/api/im-documents/{id}", imDocument.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean imDocumentExistsInEs = imDocumentSearchRepository.exists(imDocument.getId());
        assertThat(imDocumentExistsInEs).isFalse();

        // Validate the database is empty
        List<ImDocument> imDocumentList = imDocumentRepository.findAll();
        assertThat(imDocumentList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchImDocument() throws Exception {
        // Initialize the database
        imDocumentRepository.saveAndFlush(imDocument);
        imDocumentSearchRepository.save(imDocument);

        // Search the imDocument
        restImDocumentMockMvc.perform(get("/api/_search/im-documents?query=id:" + imDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(imDocument.getId().intValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())))
            .andExpect(jsonPath("$.[*].originalFilename").value(hasItem(DEFAULT_ORIGINAL_FILENAME.toString())))
            .andExpect(jsonPath("$.[*].originalXml").value(hasItem(DEFAULT_ORIGINAL_XML.toString())))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET.toString())));
    }
}
