package com.platform.familytree.family;

import com.platform.familytree.identity.Person;
import com.platform.familytree.identity.PersonRepository;
import com.platform.familytree.shared.audit.AuditLog;
import com.platform.familytree.shared.audit.AuditWriter;
import com.platform.familytree.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CareService {

    private final CareRelationshipRepository care;
    private final PersonRepository persons;
    private final AuditWriter audit;

    public CareService(CareRelationshipRepository care,
                       PersonRepository persons,
                       AuditWriter audit) {
        this.care = care;
        this.persons = persons;
        this.audit = audit;
    }

    @Transactional
    public CareRelationship record(UUID caregiverId, UUID recipientId, CareRelationship.Type type,
                                   LocalDate startDate, UUID actorId) {
        Person caregiver = persons.findById(caregiverId)
                .orElseThrow(() -> DomainException.notFound("Person", caregiverId));
        persons.findById(recipientId)
                .orElseThrow(() -> DomainException.notFound("Person", recipientId));

        CareRelationship relationship = CareRelationship.record(caregiverId, recipientId,
                type, startDate, actorId);
        care.save(relationship);
        audit.record(caregiver.getArchiveId(), actorId, "CareRelationship", relationship.getId(),
                AuditLog.Action.CREATE, null,
                "{\"caregiverId\":\"" + caregiverId + "\",\"recipientId\":\"" + recipientId
                        + "\",\"careType\":\"" + type + "\"}", "Care relationship recorded");
        return relationship;
    }

    @Transactional(readOnly = true)
    public List<CareRelationship> forRecipient(UUID recipientId) {
        return care.findByRecipientIdOrderByCreatedAtAsc(recipientId);
    }

    @Transactional(readOnly = true)
    public List<CareRelationship> byCaregiver(UUID caregiverId) {
        return care.findByCaregiverIdOrderByCreatedAtAsc(caregiverId);
    }
}
