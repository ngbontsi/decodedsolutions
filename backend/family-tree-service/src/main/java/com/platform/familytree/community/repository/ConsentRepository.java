package com.platform.familytree.community.repository;

import com.platform.familytree.community.model.Consent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsentRepository extends JpaRepository<Consent, UUID> {

    List<Consent> findByPersonId(UUID personId);
}
