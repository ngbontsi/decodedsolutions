package com.platform.guesthouse.repository;

import com.platform.guesthouse.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, String> {
    List<Store> findByBusinessId(String businessId);
    List<Store> findByBusinessIdAndActiveTrue(String businessId);
    List<Store> findByOwnerId(String ownerId);
}