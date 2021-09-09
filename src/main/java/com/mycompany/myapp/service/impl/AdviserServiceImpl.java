package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Adviser;
import com.mycompany.myapp.repository.AdviserRepository;
import com.mycompany.myapp.service.AdviserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Adviser}.
 */
@Service
@Transactional
public class AdviserServiceImpl implements AdviserService {

    private final Logger log = LoggerFactory.getLogger(AdviserServiceImpl.class);

    private final AdviserRepository adviserRepository;

    public AdviserServiceImpl(AdviserRepository adviserRepository) {
        this.adviserRepository = adviserRepository;
    }

    @Override
    public Adviser save(Adviser adviser) {
        log.debug("Request to save Adviser : {}", adviser);
        return adviserRepository.save(adviser);
    }

    @Override
    public Optional<Adviser> partialUpdate(Adviser adviser) {
        log.debug("Request to partially update Adviser : {}", adviser);

        return adviserRepository
            .findById(adviser.getId())
            .map(
                existingAdviser -> {
                    if (adviser.getRegistrationNumber() != null) {
                        existingAdviser.setRegistrationNumber(adviser.getRegistrationNumber());
                    }
                    if (adviser.getCompany() != null) {
                        existingAdviser.setCompany(adviser.getCompany());
                    }
                    if (adviser.getDepartment() != null) {
                        existingAdviser.setDepartment(adviser.getDepartment());
                    }

                    return existingAdviser;
                }
            )
            .map(adviserRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Adviser> findAll(Pageable pageable) {
        log.debug("Request to get all Advisers");
        return adviserRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Adviser> findOne(Long id) {
        log.debug("Request to get Adviser : {}", id);
        return adviserRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Adviser : {}", id);
        adviserRepository.deleteById(id);
    }
}
