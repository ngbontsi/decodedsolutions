package com.platform.familytree.family.repository;

import com.platform.familytree.family.model.Parentage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParentageRepository extends JpaRepository<Parentage, UUID> {

    List<Parentage> findByChildIdOrderByCreatedAtAsc(UUID childId);

    List<Parentage> findByChildIdAndStatusOrderByCreatedAtAsc(UUID childId, Parentage.Status status);

    List<Parentage> findByParentIdAndStatusOrderByCreatedAtAsc(UUID parentId, Parentage.Status status);

    long countByChildIdAndTypeAndStatus(UUID childId, Parentage.Type type, Parentage.Status status);

    long countByChildIdAndTypeAndRoleAndStatus(UUID childId, Parentage.Type type,
                                               Parentage.Role role, Parentage.Status status);
}
