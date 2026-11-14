package com.platform.familytree.family.repository;

import com.platform.familytree.family.model.UnionParticipant;
import com.platform.familytree.family.model.UnionParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UnionParticipantRepository extends JpaRepository<UnionParticipant, UnionParticipantId> {

    List<UnionParticipant> findByIdUnionId(UUID unionId);

    List<UnionParticipant> findByIdPersonId(UUID personId);
}
