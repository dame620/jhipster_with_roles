package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Adviser;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Adviser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AdviserRepository extends JpaRepository<Adviser, Long> {}
