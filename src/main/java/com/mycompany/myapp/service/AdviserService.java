package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Adviser;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Adviser}.
 */
public interface AdviserService {
    /**
     * Save a adviser.
     *
     * @param adviser the entity to save.
     * @return the persisted entity.
     */
    Adviser save(Adviser adviser);

    /**
     * Partially updates a adviser.
     *
     * @param adviser the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Adviser> partialUpdate(Adviser adviser);

    /**
     * Get all the advisers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Adviser> findAll(Pageable pageable);

    /**
     * Get the "id" adviser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Adviser> findOne(Long id);

    /**
     * Delete the "id" adviser.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
