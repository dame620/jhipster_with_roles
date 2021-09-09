package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Manager;
import com.mycompany.myapp.repository.ManagerRepository;
import com.mycompany.myapp.service.ManagerService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Manager}.
 */
@Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    private final Logger log = LoggerFactory.getLogger(ManagerServiceImpl.class);

    private final ManagerRepository managerRepository;

    public ManagerServiceImpl(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public Manager save(Manager manager) {
        log.debug("Request to save Manager : {}", manager);
        return managerRepository.save(manager);
    }

    @Override
    public Optional<Manager> partialUpdate(Manager manager) {
        log.debug("Request to partially update Manager : {}", manager);

        return managerRepository
            .findById(manager.getId())
            .map(
                existingManager -> {
                    if (manager.getRegistrationNumber() != null) {
                        existingManager.setRegistrationNumber(manager.getRegistrationNumber());
                    }
                    if (manager.getDepartment() != null) {
                        existingManager.setDepartment(manager.getDepartment());
                    }

                    return existingManager;
                }
            )
            .map(managerRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Manager> findAll(Pageable pageable) {
        log.debug("Request to get all Managers");
        return managerRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Manager> findOne(Long id) {
        log.debug("Request to get Manager : {}", id);
        return managerRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Manager : {}", id);
        managerRepository.deleteById(id);
    }
}
