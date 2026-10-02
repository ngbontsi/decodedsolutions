package com.platform.marketplace.repository;

import com.platform.marketplace.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, String> {
    List<Business> findByOwnerId(String ownerId);
    List<Business> findByOwnerIdAndActiveTrue(String ownerId);
    Optional<Business> findBySlug(String slug);
}