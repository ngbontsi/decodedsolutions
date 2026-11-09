package com.platform.familytree.identity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonNameRepository extends JpaRepository<PersonName, UUID> {

    List<PersonName> findByPersonIdOrderByCreatedAtAsc(UUID personId);

    Optional<PersonName> findByPersonIdAndPreferredTrue(UUID personId);

    @Query(value = """
            SELECT v.display_name
              FROM v_person_display_name v
             WHERE v.person_id = :personId
            """, nativeQuery = true)
    Optional<String> findDisplayName(@Param("personId") UUID personId);
}
