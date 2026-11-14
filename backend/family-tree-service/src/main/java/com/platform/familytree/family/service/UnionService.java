package com.platform.familytree.family.service;

import com.platform.familytree.family.model.FamilyUnion;
import com.platform.familytree.family.model.UnionParticipant;
import com.platform.familytree.family.repository.FamilyUnionRepository;
import com.platform.familytree.family.repository.UnionParticipantRepository;
import com.platform.familytree.identity.service.ArchiveService;
import com.platform.familytree.identity.repository.PersonRepository;
import com.platform.familytree.shared.audit.AuditLog;
import com.platform.familytree.shared.audit.AuditWriter;
import com.platform.familytree.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class UnionService {

    private final FamilyUnionRepository unions;
    private final UnionParticipantRepository participants;
    private final PersonRepository persons;
    private final ArchiveService archives;
    private final AuditWriter audit;

    public UnionService(FamilyUnionRepository unions,
                        UnionParticipantRepository participants,
                        PersonRepository persons,
                        ArchiveService archives,
                        AuditWriter audit) {
        this.unions = unions;
        this.participants = participants;
        this.persons = persons;
        this.archives = archives;
        this.audit = audit;
    }

    @Transactional
    public FamilyUnion create(FamilyUnion.Type type,
                              LocalDate startDate,
                              List<ParticipantInput> people,
                              UUID assertedBy) {
        if (people == null || people.size() < 2) {
            throw DomainException.rule("A union needs at least two participants");
        }
        if (people.stream().map(ParticipantInput::personId).distinct().count() != people.size()) {
            throw DomainException.rule("A person may appear only once in a union");
        }
        people.forEach(p -> persons.findById(p.personId())
                .orElseThrow(() -> DomainException.notFound("Person", p.personId())));

        UUID archiveId = archives.defaultArchiveId();
        FamilyUnion union = FamilyUnion.create(archiveId, type, startDate, assertedBy);
        unions.save(union);

        for (ParticipantInput input : people) {
            participants.save(UnionParticipant.of(union.getId(), input.personId(),
                    input.role() == null ? UnionParticipant.Role.PARTNER : input.role()));
        }
        audit.record(archiveId, assertedBy, "FamilyUnion", union.getId(),
                AuditLog.Action.CREATE, null,
                "{\"type\":\"" + union.getType() + "\"}", "Union recorded");
        return union;
    }

    @Transactional(readOnly = true)
    public List<FamilyUnion> list() {
        return unions.findByArchiveIdOrderByCreatedAtAsc(archives.defaultArchiveId());
    }

    @Transactional(readOnly = true)
    public List<UnionParticipant> participantsOf(UUID unionId) {
        return participants.findByIdUnionId(unionId);
    }

    public record ParticipantInput(UUID personId, UnionParticipant.Role role) {
    }
}
