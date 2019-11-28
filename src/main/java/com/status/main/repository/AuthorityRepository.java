package com.status.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.status.main.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {
}
