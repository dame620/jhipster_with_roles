package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Adviser;
import com.mycompany.myapp.repository.AdviserRepository;
import com.mycompany.myapp.service.AdviserService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Adviser}.
 */
@RestController
@RequestMapping("/api")
public class AdviserResource {

    private final Logger log = LoggerFactory.getLogger(AdviserResource.class);

    private static final String ENTITY_NAME = "adviser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AdviserService adviserService;

    private final AdviserRepository adviserRepository;

    public AdviserResource(AdviserService adviserService, AdviserRepository adviserRepository) {
        this.adviserService = adviserService;
        this.adviserRepository = adviserRepository;
    }

    /**
     * {@code POST  /advisers} : Create a new adviser.
     *
     * @param adviser the adviser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new adviser, or with status {@code 400 (Bad Request)} if the adviser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/advisers")
    @PreAuthorize("hasAuthority('ROLE_SUPADVISER')")
    public ResponseEntity<Adviser> createAdviser(@RequestBody Adviser adviser) throws URISyntaxException {
        log.debug("REST request to save Adviser : {}", adviser);
        if (adviser.getId() != null) {
            throw new BadRequestAlertException("A new adviser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Adviser result = adviserService.save(adviser);
        return ResponseEntity
            .created(new URI("/api/advisers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /advisers/:id} : Updates an existing adviser.
     *
     * @param id the id of the adviser to save.
     * @param adviser the adviser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adviser,
     * or with status {@code 400 (Bad Request)} if the adviser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the adviser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/advisers/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPADVISER')")
    public ResponseEntity<Adviser> updateAdviser(@PathVariable(value = "id", required = false) final Long id, @RequestBody Adviser adviser)
        throws URISyntaxException {
        log.debug("REST request to update Adviser : {}, {}", id, adviser);
        if (adviser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, adviser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!adviserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Adviser result = adviserService.save(adviser);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, adviser.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /advisers/:id} : Partial updates given fields of an existing adviser, field will ignore if it is null
     *
     * @param id the id of the adviser to save.
     * @param adviser the adviser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated adviser,
     * or with status {@code 400 (Bad Request)} if the adviser is not valid,
     * or with status {@code 404 (Not Found)} if the adviser is not found,
     * or with status {@code 500 (Internal Server Error)} if the adviser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/advisers/{id}", consumes = "application/merge-patch+json")
    @PreAuthorize("hasAuthority('ROLE_SUPADVISER')")
    public ResponseEntity<Adviser> partialUpdateAdviser(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Adviser adviser
    ) throws URISyntaxException {
        log.debug("REST request to partial update Adviser partially : {}, {}", id, adviser);
        if (adviser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, adviser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!adviserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Adviser> result = adviserService.partialUpdate(adviser);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, adviser.getId().toString())
        );
    }

    /**
     * {@code GET  /advisers} : get all the advisers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of advisers in body.
     */
    @GetMapping("/advisers")
    @PreAuthorize("hasAuthority('ROLE_SUPADVISER')")
    public ResponseEntity<List<Adviser>> getAllAdvisers(Pageable pageable) {
        log.debug("REST request to get a page of Advisers");
        Page<Adviser> page = adviserService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /advisers/:id} : get the "id" adviser.
     *
     * @param id the id of the adviser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the adviser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/advisers/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPADVISER')")
    public ResponseEntity<Adviser> getAdviser(@PathVariable Long id) {
        log.debug("REST request to get Adviser : {}", id);
        Optional<Adviser> adviser = adviserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(adviser);
    }

    /**
     * {@code DELETE  /advisers/:id} : delete the "id" adviser.
     *
     * @param id the id of the adviser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/advisers/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPADVISER')")
    public ResponseEntity<Void> deleteAdviser(@PathVariable Long id) {
        log.debug("REST request to delete Adviser : {}", id);
        adviserService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
