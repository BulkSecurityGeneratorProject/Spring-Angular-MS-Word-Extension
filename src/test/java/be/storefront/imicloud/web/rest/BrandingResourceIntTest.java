package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.ImicloudApp;

import be.storefront.imicloud.domain.Branding;
import be.storefront.imicloud.repository.BrandingRepository;
import be.storefront.imicloud.service.BrandingService;
import be.storefront.imicloud.repository.search.BrandingSearchRepository;
import be.storefront.imicloud.service.dto.BrandingDTO;
import be.storefront.imicloud.service.mapper.BrandingMapper;

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
 * Test class for the BrandingResource REST controller.
 *
 * @see BrandingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImicloudApp.class)
public class BrandingResourceIntTest {

    private static final String DEFAULT_PRIMARY_COLOR = "AAAAAA";
    private static final String UPDATED_PRIMARY_COLOR = "BBBBBB";

    private static final String DEFAULT_SECUNDARY_COLOR = "AAAAAA";
    private static final String UPDATED_SECUNDARY_COLOR = "BBBBBB";

    @Inject
    private BrandingRepository brandingRepository;

    @Inject
    private BrandingMapper brandingMapper;

    @Inject
    private BrandingService brandingService;

    @Inject
    private BrandingSearchRepository brandingSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restBrandingMockMvc;

    private Branding branding;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BrandingResource brandingResource = new BrandingResource();
        ReflectionTestUtils.setField(brandingResource, "brandingService", brandingService);
        this.restBrandingMockMvc = MockMvcBuilders.standaloneSetup(brandingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Branding createEntity(EntityManager em) {
        Branding branding = new Branding()
                .primaryColor(DEFAULT_PRIMARY_COLOR)
                .secundaryColor(DEFAULT_SECUNDARY_COLOR);
        return branding;
    }

    @Before
    public void initTest() {
        brandingSearchRepository.deleteAll();
        branding = createEntity(em);
    }

    @Test
    @Transactional
    public void createBranding() throws Exception {
        int databaseSizeBeforeCreate = brandingRepository.findAll().size();

        // Create the Branding
        BrandingDTO brandingDTO = brandingMapper.brandingToBrandingDTO(branding);

        restBrandingMockMvc.perform(post("/api/brandings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandingDTO)))
            .andExpect(status().isCreated());

        // Validate the Branding in the database
        List<Branding> brandingList = brandingRepository.findAll();
        assertThat(brandingList).hasSize(databaseSizeBeforeCreate + 1);
        Branding testBranding = brandingList.get(brandingList.size() - 1);
        assertThat(testBranding.getPrimaryColor()).isEqualTo(DEFAULT_PRIMARY_COLOR);
        assertThat(testBranding.getSecundaryColor()).isEqualTo(DEFAULT_SECUNDARY_COLOR);

        // Validate the Branding in ElasticSearch
        Branding brandingEs = brandingSearchRepository.findOne(testBranding.getId());
        assertThat(brandingEs).isEqualToComparingFieldByField(testBranding);
    }

    @Test
    @Transactional
    public void createBrandingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = brandingRepository.findAll().size();

        // Create the Branding with an existing ID
        Branding existingBranding = new Branding();
        existingBranding.setId(1L);
        BrandingDTO existingBrandingDTO = brandingMapper.brandingToBrandingDTO(existingBranding);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrandingMockMvc.perform(post("/api/brandings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingBrandingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Branding> brandingList = brandingRepository.findAll();
        assertThat(brandingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllBrandings() throws Exception {
        // Initialize the database
        brandingRepository.saveAndFlush(branding);

        // Get all the brandingList
        restBrandingMockMvc.perform(get("/api/brandings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(branding.getId().intValue())))
            .andExpect(jsonPath("$.[*].primaryColor").value(hasItem(DEFAULT_PRIMARY_COLOR.toString())))
            .andExpect(jsonPath("$.[*].secundaryColor").value(hasItem(DEFAULT_SECUNDARY_COLOR.toString())));
    }

    @Test
    @Transactional
    public void getBranding() throws Exception {
        // Initialize the database
        brandingRepository.saveAndFlush(branding);

        // Get the branding
        restBrandingMockMvc.perform(get("/api/brandings/{id}", branding.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(branding.getId().intValue()))
            .andExpect(jsonPath("$.primaryColor").value(DEFAULT_PRIMARY_COLOR.toString()))
            .andExpect(jsonPath("$.secundaryColor").value(DEFAULT_SECUNDARY_COLOR.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBranding() throws Exception {
        // Get the branding
        restBrandingMockMvc.perform(get("/api/brandings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBranding() throws Exception {
        // Initialize the database
        brandingRepository.saveAndFlush(branding);
        brandingSearchRepository.save(branding);
        int databaseSizeBeforeUpdate = brandingRepository.findAll().size();

        // Update the branding
        Branding updatedBranding = brandingRepository.findOne(branding.getId());
        updatedBranding
                .primaryColor(UPDATED_PRIMARY_COLOR)
                .secundaryColor(UPDATED_SECUNDARY_COLOR);
        BrandingDTO brandingDTO = brandingMapper.brandingToBrandingDTO(updatedBranding);

        restBrandingMockMvc.perform(put("/api/brandings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandingDTO)))
            .andExpect(status().isOk());

        // Validate the Branding in the database
        List<Branding> brandingList = brandingRepository.findAll();
        assertThat(brandingList).hasSize(databaseSizeBeforeUpdate);
        Branding testBranding = brandingList.get(brandingList.size() - 1);
        assertThat(testBranding.getPrimaryColor()).isEqualTo(UPDATED_PRIMARY_COLOR);
        assertThat(testBranding.getSecundaryColor()).isEqualTo(UPDATED_SECUNDARY_COLOR);

        // Validate the Branding in ElasticSearch
        Branding brandingEs = brandingSearchRepository.findOne(testBranding.getId());
        assertThat(brandingEs).isEqualToComparingFieldByField(testBranding);
    }

    @Test
    @Transactional
    public void updateNonExistingBranding() throws Exception {
        int databaseSizeBeforeUpdate = brandingRepository.findAll().size();

        // Create the Branding
        BrandingDTO brandingDTO = brandingMapper.brandingToBrandingDTO(branding);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBrandingMockMvc.perform(put("/api/brandings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(brandingDTO)))
            .andExpect(status().isCreated());

        // Validate the Branding in the database
        List<Branding> brandingList = brandingRepository.findAll();
        assertThat(brandingList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBranding() throws Exception {
        // Initialize the database
        brandingRepository.saveAndFlush(branding);
        brandingSearchRepository.save(branding);
        int databaseSizeBeforeDelete = brandingRepository.findAll().size();

        // Get the branding
        restBrandingMockMvc.perform(delete("/api/brandings/{id}", branding.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean brandingExistsInEs = brandingSearchRepository.exists(branding.getId());
        assertThat(brandingExistsInEs).isFalse();

        // Validate the database is empty
        List<Branding> brandingList = brandingRepository.findAll();
        assertThat(brandingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBranding() throws Exception {
        // Initialize the database
        brandingRepository.saveAndFlush(branding);
        brandingSearchRepository.save(branding);

        // Search the branding
        restBrandingMockMvc.perform(get("/api/_search/brandings?query=id:" + branding.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(branding.getId().intValue())))
            .andExpect(jsonPath("$.[*].primaryColor").value(hasItem(DEFAULT_PRIMARY_COLOR.toString())))
            .andExpect(jsonPath("$.[*].secundaryColor").value(hasItem(DEFAULT_SECUNDARY_COLOR.toString())));
    }
}
