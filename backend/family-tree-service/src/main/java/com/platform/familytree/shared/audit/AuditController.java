package com.platform.familytree.shared.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    private final AuditLogRepository repository;

    public AuditController(AuditLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Page<AuditLog> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @GetMapping("/{entityType}/{entityId}")
    public java.util.List<AuditLog> forEntity(@PathVariable String entityType,
                                              @PathVariable UUID entityId) {
        return repository.findByEntityTypeAndEntityIdOrderByOccurredAtDesc(entityType, entityId);
    }
}
