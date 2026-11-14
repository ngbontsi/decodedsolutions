package com.platform.familytree.community.repository;

import com.platform.familytree.community.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    List<Permission> findByContributorId(UUID contributorId);
}
