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
public class ParentageService {

    private final ParentageRepository parentages;
    private final PersonRepository persons;
    private final AuditWriter audit;

    public ParentageService(ParentageRepository parentages,
                            PersonRepository persons,
                            AuditWriter audit) {
        this.parentages = parentages;
        this.persons = persons;
        this.audit = audit;
    }

    @Transactional
    public Parentage recordIdentified(UUID childId, UUID parentId, Parentage.Type type,
                                      Parentage.Role role, LocalDate validFrom, UUID actorId) {
        Person child = requirePerson(childId);
        // Build first: Parentage.identified() is what rejects a null or self parent.
        Parentage parentage = Parentage.identified(childId, parentId, type, role, actorId, validFrom);
        requirePerson(parentId);
        enforceBiologicalLimits(childId, type, role);
        parentages.save(parentage);
        audit.record(child.getArchiveId(), actorId, "Parentage", parentage.getId(),
                AuditLog.Action.CREATE, null,
                "{\"childId\":\"" + childId + "\",\"parentId\":\"" + parentId
                        + "\",\"type\":\"" + type + "\"}", "Parentage recorded");
        return parentage;
    }

    @Transactional
    public Parentage recordUnidentified(UUID childId, Parentage.Type type, Parentage.Role role,
                                        LocalDate validFrom, UUID actorId) {
        Person child = requirePerson(childId);
        enforceBiologicalLimits(childId, type, role);
        Parentage parentage = Parentage.unidentified(childId, type, role, actorId, validFrom);
        parentages.save(parentage);
        audit.record(child.getArchiveId(), actorId, "Parentage", parentage.getId(),
                AuditLog.Action.CREATE, null, "{\"childId\":\"" + childId + "\"}",
                "Unidentified parent recorded");
        return parentage;
    }

    @Transactional
    public Parentage recordUnrecorded(UUID childId, Parentage.Type type, Parentage.Role role,
                                      String reportedName, LocalDate validFrom, UUID actorId) {
        Person child = requirePerson(childId);
        enforceBiologicalLimits(childId, type, role);
        Parentage parentage = Parentage.unrecorded(childId, type, role, reportedName,
                actorId, validFrom);
        parentages.save(parentage);
        audit.record(child.getArchiveId(), actorId, "Parentage", parentage.getId(),
                AuditLog.Action.CREATE, null, "{\"childId\":\"" + childId + "\"}",
                "Reported but unresearched parent recorded");
        return parentage;
    }

    @Transactional
    public Parentage recordUnknown(UUID childId, Parentage.Type type, Parentage.Role role,
                                   LocalDate validFrom, UUID actorId) {
        Person child = requirePerson(childId);
        enforceBiologicalLimits(childId, type, role);
        Parentage parentage = Parentage.unknown(childId, type, role, actorId, validFrom);
        parentages.save(parentage);
        audit.record(child.getArchiveId(), actorId, "Parentage", parentage.getId(),
                AuditLog.Action.CREATE, null, "{\"childId\":\"" + childId + "\"}",
                "Unknown parent recorded");
        return parentage;
    }

    /**
     * FT-008: an unidentified parent is identified later. The original row is closed
     * and marked SUPERSEDED; a new row is inserted. Nothing is deleted.
     */
    @Transactional
    public Parentage supersede(UUID parentageId, UUID newParentId, LocalDate on, UUID actorId) {
        Parentage existing = parentages.findById(parentageId)
                .orElseThrow(() -> DomainException.notFound("Parentage", parentageId));
        if (existing.getStatus() != Parentage.Status.ACTIVE) {
            throw DomainException.rule("Only an ACTIVE parentage can be superseded");
        }
        Person child = requirePerson(existing.getChildId());
        requirePerson(newParentId);

        Parentage replacement = Parentage.identified(existing.getChildId(), newParentId,
                existing.getType(), existing.getRole(), actorId, on);
        parentages.save(replacement);

        String before = "{\"id\":\"" + existing.getId() + "\",\"status\":\"ACTIVE\"}";
        existing.supersedeBy(replacement, on);
        parentages.save(existing);

        audit.record(child.getArchiveId(), actorId, "Parentage", existing.getId(),
                AuditLog.Action.SUPERSEDE, before,
                "{\"status\":\"SUPERSEDED\",\"supersededBy\":\"" + replacement.getId() + "\"}",
                "Parent identified; original record preserved");
        return replacement;
    }

    @Transactional(readOnly = true)
    public List<Parentage> forChild(UUID childId) {
        return parentages.findByChildIdOrderByCreatedAtAsc(childId);
    }

    private Person requirePerson(UUID id) {
        return persons.findById(id).orElseThrow(() -> DomainException.notFound("Person", id));
    }

    /**
     * SQL cannot express "at most two active BIOLOGICAL parents, one per role",
     * so the rule lives here and is covered by a test.
     */
    private void enforceBiologicalLimits(UUID childId, Parentage.Type type, Parentage.Role role) {
        if (type != Parentage.Type.BIOLOGICAL) {
            return;
        }
        long total = parentages.countByChildIdAndTypeAndStatus(
                childId, Parentage.Type.BIOLOGICAL, Parentage.Status.ACTIVE);
        if (total >= 2) {
            throw DomainException.rule("A child cannot have more than two biological parents");
        }
        if (role != Parentage.Role.UNKNOWN) {
            long sameRole = parentages.countByChildIdAndTypeAndRoleAndStatus(
                    childId, Parentage.Type.BIOLOGICAL, role, Parentage.Status.ACTIVE);
            if (sameRole > 0) {
                throw DomainException.rule("A child already has an active biological " + role);
            }
        }
    }
}
