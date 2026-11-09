package com.platform.familytree.family;

import com.platform.familytree.identity.ArchiveService;
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
public class FamilyService {

    private final FamilyGroupRepository groups;
    private final FamilyMembershipRepository memberships;
    private final PersonRepository persons;
    private final ArchiveService archives;
    private final AuditWriter audit;

    public FamilyService(FamilyGroupRepository groups,
                         FamilyMembershipRepository memberships,
                         PersonRepository persons,
                         ArchiveService archives,
                         AuditWriter audit) {
        this.groups = groups;
        this.memberships = memberships;
        this.persons = persons;
        this.archives = archives;
        this.audit = audit;
    }

    @Transactional
    public FamilyGroup createGroup(String name, String description,
                                   FamilyGroup.LineageType lineageType, UUID actorId) {
        UUID archiveId = archives.defaultArchiveId();
        FamilyGroup group = FamilyGroup.create(archiveId, name, description, lineageType);
        groups.save(group);
        audit.record(archiveId, actorId, "FamilyGroup", group.getId(),
                AuditLog.Action.CREATE, null, "{\"name\":\"" + name + "\"}", "Family group created");
        return group;
    }

    @Transactional(readOnly = true)
    public List<FamilyGroup> listGroups() {
        return groups.findByArchiveIdOrderByCreatedAtAsc(archives.defaultArchiveId());
    }

    /**
     * Membership is always an explicit act. Nothing here is derived from parentage.
     */
    @Transactional
    public FamilyMembership addMembership(UUID personId,
                                          UUID familyGroupId,
                                          FamilyMembership.ConnectionType connectionType,
                                          FamilyMembership.Basis basis,
                                          LocalDate validFrom,
                                          UUID assertedBy) {
        Person person = persons.findById(personId)
                .orElseThrow(() -> DomainException.notFound("Person", personId));
        FamilyGroup group = groups.findById(familyGroupId)
                .orElseThrow(() -> DomainException.notFound("Family group", familyGroupId));

        FamilyMembership membership = FamilyMembership.of(personId, familyGroupId,
                connectionType, basis, assertedBy, validFrom);
        memberships.save(membership);
        audit.record(group.getArchiveId(), assertedBy, "FamilyMembership", membership.getId(),
                AuditLog.Action.CREATE, null,
                "{\"personId\":\"" + personId + "\",\"familyGroupId\":\"" + familyGroupId
                        + "\",\"basis\":\"" + basis + "\"}",
                "Membership asserted");
        return membership;
    }

    @Transactional(readOnly = true)
    public List<FamilyMembership> membershipsOfPerson(UUID personId) {
        return memberships.findByPersonIdOrderByCreatedAtAsc(personId);
    }

    @Transactional(readOnly = true)
    public List<FamilyMembership> membersOfGroup(UUID familyGroupId) {
        return memberships.findByFamilyGroupIdOrderByCreatedAtAsc(familyGroupId);
    }
}
