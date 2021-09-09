package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Adviser;
import com.mycompany.myapp.repository.AdviserRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AdviserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AdviserResourceIT {

    private static final String DEFAULT_REGISTRATION_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_REGISTRATION_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/advisers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AdviserRepository adviserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAdviserMockMvc;

    private Adviser adviser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adviser createEntity(EntityManager em) {
        Adviser adviser = new Adviser()
            .registrationNumber(DEFAULT_REGISTRATION_NUMBER)
            .company(DEFAULT_COMPANY)
            .department(DEFAULT_DEPARTMENT);
        return adviser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Adviser createUpdatedEntity(EntityManager em) {
        Adviser adviser = new Adviser()
            .registrationNumber(UPDATED_REGISTRATION_NUMBER)
            .company(UPDATED_COMPANY)
            .department(UPDATED_DEPARTMENT);
        return adviser;
    }

    @BeforeEach
    public void initTest() {
        adviser = createEntity(em);
    }

    @Test
    @Transactional
    void createAdviser() throws Exception {
        int databaseSizeBeforeCreate = adviserRepository.findAll().size();
        // Create the Adviser
        restAdviserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adviser)))
            .andExpect(status().isCreated());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeCreate + 1);
        Adviser testAdviser = adviserList.get(adviserList.size() - 1);
        assertThat(testAdviser.getRegistrationNumber()).isEqualTo(DEFAULT_REGISTRATION_NUMBER);
        assertThat(testAdviser.getCompany()).isEqualTo(DEFAULT_COMPANY);
        assertThat(testAdviser.getDepartment()).isEqualTo(DEFAULT_DEPARTMENT);
    }

    @Test
    @Transactional
    void createAdviserWithExistingId() throws Exception {
        // Create the Adviser with an existing ID
        adviser.setId(1L);

        int databaseSizeBeforeCreate = adviserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAdviserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adviser)))
            .andExpect(status().isBadRequest());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllAdvisers() throws Exception {
        // Initialize the database
        adviserRepository.saveAndFlush(adviser);

        // Get all the adviserList
        restAdviserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(adviser.getId().intValue())))
            .andExpect(jsonPath("$.[*].registrationNumber").value(hasItem(DEFAULT_REGISTRATION_NUMBER)))
            .andExpect(jsonPath("$.[*].company").value(hasItem(DEFAULT_COMPANY)))
            .andExpect(jsonPath("$.[*].department").value(hasItem(DEFAULT_DEPARTMENT)));
    }

    @Test
    @Transactional
    void getAdviser() throws Exception {
        // Initialize the database
        adviserRepository.saveAndFlush(adviser);

        // Get the adviser
        restAdviserMockMvc
            .perform(get(ENTITY_API_URL_ID, adviser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(adviser.getId().intValue()))
            .andExpect(jsonPath("$.registrationNumber").value(DEFAULT_REGISTRATION_NUMBER))
            .andExpect(jsonPath("$.company").value(DEFAULT_COMPANY))
            .andExpect(jsonPath("$.department").value(DEFAULT_DEPARTMENT));
    }

    @Test
    @Transactional
    void getNonExistingAdviser() throws Exception {
        // Get the adviser
        restAdviserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAdviser() throws Exception {
        // Initialize the database
        adviserRepository.saveAndFlush(adviser);

        int databaseSizeBeforeUpdate = adviserRepository.findAll().size();

        // Update the adviser
        Adviser updatedAdviser = adviserRepository.findById(adviser.getId()).get();
        // Disconnect from session so that the updates on updatedAdviser are not directly saved in db
        em.detach(updatedAdviser);
        updatedAdviser.registrationNumber(UPDATED_REGISTRATION_NUMBER).company(UPDATED_COMPANY).department(UPDATED_DEPARTMENT);

        restAdviserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedAdviser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedAdviser))
            )
            .andExpect(status().isOk());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
        Adviser testAdviser = adviserList.get(adviserList.size() - 1);
        assertThat(testAdviser.getRegistrationNumber()).isEqualTo(UPDATED_REGISTRATION_NUMBER);
        assertThat(testAdviser.getCompany()).isEqualTo(UPDATED_COMPANY);
        assertThat(testAdviser.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
    }

    @Test
    @Transactional
    void putNonExistingAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().size();
        adviser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdviserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, adviser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(adviser))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().size();
        adviser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdviserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(adviser))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().size();
        adviser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdviserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(adviser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAdviserWithPatch() throws Exception {
        // Initialize the database
        adviserRepository.saveAndFlush(adviser);

        int databaseSizeBeforeUpdate = adviserRepository.findAll().size();

        // Update the adviser using partial update
        Adviser partialUpdatedAdviser = new Adviser();
        partialUpdatedAdviser.setId(adviser.getId());

        partialUpdatedAdviser.registrationNumber(UPDATED_REGISTRATION_NUMBER).company(UPDATED_COMPANY).department(UPDATED_DEPARTMENT);

        restAdviserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdviser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdviser))
            )
            .andExpect(status().isOk());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
        Adviser testAdviser = adviserList.get(adviserList.size() - 1);
        assertThat(testAdviser.getRegistrationNumber()).isEqualTo(UPDATED_REGISTRATION_NUMBER);
        assertThat(testAdviser.getCompany()).isEqualTo(UPDATED_COMPANY);
        assertThat(testAdviser.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
    }

    @Test
    @Transactional
    void fullUpdateAdviserWithPatch() throws Exception {
        // Initialize the database
        adviserRepository.saveAndFlush(adviser);

        int databaseSizeBeforeUpdate = adviserRepository.findAll().size();

        // Update the adviser using partial update
        Adviser partialUpdatedAdviser = new Adviser();
        partialUpdatedAdviser.setId(adviser.getId());

        partialUpdatedAdviser.registrationNumber(UPDATED_REGISTRATION_NUMBER).company(UPDATED_COMPANY).department(UPDATED_DEPARTMENT);

        restAdviserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAdviser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAdviser))
            )
            .andExpect(status().isOk());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
        Adviser testAdviser = adviserList.get(adviserList.size() - 1);
        assertThat(testAdviser.getRegistrationNumber()).isEqualTo(UPDATED_REGISTRATION_NUMBER);
        assertThat(testAdviser.getCompany()).isEqualTo(UPDATED_COMPANY);
        assertThat(testAdviser.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
    }

    @Test
    @Transactional
    void patchNonExistingAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().size();
        adviser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAdviserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, adviser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(adviser))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().size();
        adviser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdviserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(adviser))
            )
            .andExpect(status().isBadRequest());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAdviser() throws Exception {
        int databaseSizeBeforeUpdate = adviserRepository.findAll().size();
        adviser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAdviserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(adviser)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Adviser in the database
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAdviser() throws Exception {
        // Initialize the database
        adviserRepository.saveAndFlush(adviser);

        int databaseSizeBeforeDelete = adviserRepository.findAll().size();

        // Delete the adviser
        restAdviserMockMvc
            .perform(delete(ENTITY_API_URL_ID, adviser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Adviser> adviserList = adviserRepository.findAll();
        assertThat(adviserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
