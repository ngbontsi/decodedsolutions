package com.platform.familytree.community;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContributorRepository extends JpaRepository<Contributor, UUID> {

    Optional<Contributor> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
