package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Bank;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Bank}.
 */
public interface BankService {
    /**
     * Save a bank.
     *
     * @param bank the entity to save.
     * @return the persisted entity.
     */
    Bank save(Bank bank);

    /**
     * Partially updates a bank.
     *
     * @param bank the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Bank> partialUpdate(Bank bank);

    /**
     * Get all the banks.
     *
     * @return the list of entities.
     */
    List<Bank> findAll();

    /**
     * Get the "id" bank.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Bank> findOne(Long id);

    /**
     * Delete the "id" bank.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
