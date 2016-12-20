package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.ImicloudApp;
import be.storefront.imicloud.domain.ImDocument;
import be.storefront.imicloud.domain.ImMap;
import be.storefront.imicloud.domain.User;
import be.storefront.imicloud.repository.ImDocumentRepository;
import be.storefront.imicloud.repository.ImMapRepository;
import be.storefront.imicloud.repository.search.ImDocumentSearchRepository;
import be.storefront.imicloud.repository.search.ImMapSearchRepository;
import be.storefront.imicloud.service.ImDocumentService;
import be.storefront.imicloud.service.ImMapService;
import be.storefront.imicloud.service.dto.ImDocumentDTO;
import be.storefront.imicloud.service.dto.ImMapDTO;
import be.storefront.imicloud.service.mapper.ImDocumentMapper;
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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
public class UploadControllerTest {


    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_FILENAME = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_FILENAME = "BBBBBBBBBB";

    private static final String DEFAULT_ORIGINAL_XML = "AAAAAAAAAA";
    private static final String UPDATED_ORIGINAL_XML = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED_AT = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED_AT = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

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



    @Before
    public void initTest() {
        imDocumentSearchRepository.deleteAll();
        imDocument = ImDocumentResourceIntTest.createEntity(em);
    }

//    @Test
//    @Transactional
//    public void createImDocument() throws Exception {
//        int databaseSizeBeforeCreate = imDocumentRepository.findAll().size();
//
//        // Create the ImDocument
//        ImDocumentDTO imDocumentDTO = imDocumentMapper.imDocumentToImDocumentDTO(imDocument);
//
//        restImDocumentMockMvc.perform(post("/upload/xml")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(imDocumentDTO)))
//            .andExpect(status().isCreated());
//
//        // Validate the ImDocument in the database
//        List<ImDocument> imDocumentList = imDocumentRepository.findAll();
//        assertThat(imDocumentList).hasSize(databaseSizeBeforeCreate + 1);
//        ImDocument testImDocument = imDocumentList.get(imDocumentList.size() - 1);
//        assertThat(testImDocument.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
//        assertThat(testImDocument.getPassword()).isEqualTo(DEFAULT_PASSWORD);
//        assertThat(testImDocument.getOriginalFilename()).isEqualTo(DEFAULT_ORIGINAL_FILENAME);
//        assertThat(testImDocument.getOriginalXml()).isEqualTo(DEFAULT_ORIGINAL_XML);
//        assertThat(testImDocument.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
//
//        // Validate the ImDocument in ElasticSearch
//        ImDocument imDocumentEs = imDocumentSearchRepository.findOne(testImDocument.getId());
//        assertThat(imDocumentEs).isEqualToComparingFieldByField(testImDocument);
//    }

}
