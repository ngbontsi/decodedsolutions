package com.platform.familytree.identity;

import com.platform.familytree.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ArchiveService {

    private final ArchiveRepository repository;

    public ArchiveService(ArchiveRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public UUID defaultArchiveId() {
        return repository.findFirstByStatusOrderByCreatedAtAsc("ACTIVE")
                .map(Archive::getId)
                .orElseThrow(() -> new DomainException(
                        com.platform.familytree.shared.error.ErrorCode.NOT_FOUND,
                        "No active archive. Run the V1 migration."));
    }

    @Transactional(readOnly = true)
    public Archive get(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("Archive", id));
    }
}
