package be.storefront.imicloud.web.rest;

import be.storefront.imicloud.ImicloudApp;

import be.storefront.imicloud.domain.Organization;
import be.storefront.imicloud.repository.OrganizationRepository;
import be.storefront.imicloud.service.OrganizationService;
import be.storefront.imicloud.repository.search.OrganizationSearchRepository;
import be.storefront.imicloud.service.dto.OrganizationDTO;
import be.storefront.imicloud.service.mapper.OrganizationMapper;

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
 * Test class for the OrganizationResource REST controller.
 *
 * @see OrganizationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImicloudApp.class)
public class OrganizationResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_MAGENTO_CUSTOMER_ID = 1;
    private static final Integer UPDATED_MAGENTO_CUSTOMER_ID = 2;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private OrganizationMapper organizationMapper;

    @Inject
    private OrganizationService organizationService;

    @Inject
    private OrganizationSearchRepository organizationSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restOrganizationMockMvc;

    private Organization organization;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrganizationResource organizationResource = new OrganizationResource();
        ReflectionTestUtils.setField(organizationResource, "organizationService", organizationService);
        this.restOrganizationMockMvc = MockMvcBuilders.standaloneSetup(organizationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Organization createEntity(EntityManager em) {
        Organization organization = new Organization()
                .name(DEFAULT_NAME)
                .magentoCustomerId(DEFAULT_MAGENTO_CUSTOMER_ID);
        return organization;
    }

    @Before
    public void initTest() {
        organizationSearchRepository.deleteAll();
        organization = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrganization() throws Exception {
        int databaseSizeBeforeCreate = organizationRepository.findAll().size();

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.organizationToOrganizationDTO(organization);

        restOrganizationMockMvc.perform(post("/api/organizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organizationDTO)))
            .andExpect(status().isCreated());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate + 1);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOrganization.getMagentoCustomerId()).isEqualTo(DEFAULT_MAGENTO_CUSTOMER_ID);

        // Validate the Organization in ElasticSearch
        Organization organizationEs = organizationSearchRepository.findOne(testOrganization.getId());
        assertThat(organizationEs).isEqualToComparingFieldByField(testOrganization);
    }

    @Test
    @Transactional
    public void createOrganizationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = organizationRepository.findAll().size();

        // Create the Organization with an existing ID
        Organization existingOrganization = new Organization();
        existingOrganization.setId(1L);
        OrganizationDTO existingOrganizationDTO = organizationMapper.organizationToOrganizationDTO(existingOrganization);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrganizationMockMvc.perform(post("/api/organizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingOrganizationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = organizationRepository.findAll().size();
        // set the field null
        organization.setName(null);

        // Create the Organization, which fails.
        OrganizationDTO organizationDTO = organizationMapper.organizationToOrganizationDTO(organization);

        restOrganizationMockMvc.perform(post("/api/organizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organizationDTO)))
            .andExpect(status().isBadRequest());

        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOrganizations() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get all the organizationList
        restOrganizationMockMvc.perform(get("/api/organizations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organization.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].magentoCustomerId").value(hasItem(DEFAULT_MAGENTO_CUSTOMER_ID)));
    }

    @Test
    @Transactional
    public void getOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);

        // Get the organization
        restOrganizationMockMvc.perform(get("/api/organizations/{id}", organization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(organization.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.magentoCustomerId").value(DEFAULT_MAGENTO_CUSTOMER_ID));
    }

    @Test
    @Transactional
    public void getNonExistingOrganization() throws Exception {
        // Get the organization
        restOrganizationMockMvc.perform(get("/api/organizations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);
        organizationSearchRepository.save(organization);
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();

        // Update the organization
        Organization updatedOrganization = organizationRepository.findOne(organization.getId());
        updatedOrganization
                .name(UPDATED_NAME)
                .magentoCustomerId(UPDATED_MAGENTO_CUSTOMER_ID);
        OrganizationDTO organizationDTO = organizationMapper.organizationToOrganizationDTO(updatedOrganization);

        restOrganizationMockMvc.perform(put("/api/organizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organizationDTO)))
            .andExpect(status().isOk());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate);
        Organization testOrganization = organizationList.get(organizationList.size() - 1);
        assertThat(testOrganization.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOrganization.getMagentoCustomerId()).isEqualTo(UPDATED_MAGENTO_CUSTOMER_ID);

        // Validate the Organization in ElasticSearch
        Organization organizationEs = organizationSearchRepository.findOne(testOrganization.getId());
        assertThat(organizationEs).isEqualToComparingFieldByField(testOrganization);
    }

    @Test
    @Transactional
    public void updateNonExistingOrganization() throws Exception {
        int databaseSizeBeforeUpdate = organizationRepository.findAll().size();

        // Create the Organization
        OrganizationDTO organizationDTO = organizationMapper.organizationToOrganizationDTO(organization);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOrganizationMockMvc.perform(put("/api/organizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organizationDTO)))
            .andExpect(status().isCreated());

        // Validate the Organization in the database
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);
        organizationSearchRepository.save(organization);
        int databaseSizeBeforeDelete = organizationRepository.findAll().size();

        // Get the organization
        restOrganizationMockMvc.perform(delete("/api/organizations/{id}", organization.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean organizationExistsInEs = organizationSearchRepository.exists(organization.getId());
        assertThat(organizationExistsInEs).isFalse();

        // Validate the database is empty
        List<Organization> organizationList = organizationRepository.findAll();
        assertThat(organizationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOrganization() throws Exception {
        // Initialize the database
        organizationRepository.saveAndFlush(organization);
        organizationSearchRepository.save(organization);

        // Search the organization
        restOrganizationMockMvc.perform(get("/api/_search/organizations?query=id:" + organization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(organization.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].magentoCustomerId").value(hasItem(DEFAULT_MAGENTO_CUSTOMER_ID)));
    }
}
