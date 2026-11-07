package com.platform.familytree.identity;

import com.platform.familytree.family.Parentage;
import com.platform.familytree.family.ParentageRepository;
import com.platform.familytree.shared.audit.AuditLog;
import com.platform.familytree.shared.audit.AuditWriter;
import com.platform.familytree.shared.error.DomainException;
import com.platform.familytree.shared.ids.Uuid7;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class PersonService {

    public record AncestryEdge(UUID personId,
                               String personName,
                               UUID parentId,
                               String parentName,
                               String parentageType,
                               String parentRole,
                               String identityStatus,
                               int generation) {
    }

    private final PersonRepository persons;
    private final PersonNameRepository names;
    private final ParentageRepository parentages;
    private final ArchiveService archives;
    private final AuditWriter audit;

    public PersonService(PersonRepository persons,
                         PersonNameRepository names,
                         ParentageRepository parentages,
                         ArchiveService archives,
                         AuditWriter audit) {
        this.persons = persons;
        this.names = names;
        this.parentages = parentages;
        this.archives = archives;
        this.audit = audit;
    }

    @Transactional
    public Person create(Person.Gender gender,
                         LocalDate birthDate,
                         Person.DatePrecision birthPrecision,
                         String biography,
                         Person.Visibility visibility,
                         UUID actorId) {
        UUID archiveId = archives.defaultArchiveId();
        Person person = Person.create(archiveId, gender);
        person.describe(birthDate, birthPrecision, biography, visibility);
        persons.save(person);
        audit.record(archiveId, actorId, "Person", person.getId(),
                AuditLog.Action.CREATE, null, "{\"gender\":\"" + person.getGender() + "\"}",
                "Person created");
        return person;
    }

    @Transactional(readOnly = true)
    public Person get(UUID id) {
        return persons.findById(id).orElseThrow(() -> DomainException.notFound("Person", id));
    }

    @Transactional(readOnly = true)
    public List<Person> list() {
        return persons.findByArchiveIdAndStatusOrderByCreatedAtAsc(
                archives.defaultArchiveId(), Person.Status.ACTIVE);
    }

    /**
     * Adds a name. A name change closes the previous row and inserts a new one —
     * history is never overwritten (FT-006).
     */
    @Transactional
    public PersonName addName(UUID personId,
                              PersonName.Type type,
                              String fullName,
                              String givenNames,
                              String familyName,
                              LocalDate validFrom,
                              boolean preferred,
                              UUID actorId) {
        Person person = get(personId);
        if (preferred) {
            names.findByPersonIdAndPreferredTrue(personId).ifPresent(existing -> {
                existing.revokePreferred();
                // Flush now: Hibernate runs inserts before updates in the same flush,
                // and the partial unique index would reject the new preferred name.
                names.saveAndFlush(existing);
            });
        }
        PersonName name = PersonName.of(personId, type, fullName, givenNames, familyName, validFrom);
        if (preferred) {
            name.makePreferred();
        }
        names.save(name);
        audit.record(person.getArchiveId(), actorId, "PersonName", name.getId(),
                AuditLog.Action.CREATE, null,
                "{\"personId\":\"" + personId + "\",\"fullName\":\"" + name.getFullName() + "\"}",
                "Name recorded as " + type);
        return name;
    }

    @Transactional(readOnly = true)
    public List<PersonName> names(UUID personId) {
        get(personId);
        return names.findByPersonIdOrderByCreatedAtAsc(personId);
    }

    @Transactional(readOnly = true)
    public String displayName(UUID personId) {
        get(personId);
        return names.findDisplayName(personId).orElse(null);
    }

    @Transactional
    public Person recordDeath(UUID personId, LocalDate deathDate,
                              Person.DatePrecision precision, UUID actorId) {
        Person person = get(personId);
        person.recordDeath(deathDate, precision);
        persons.save(person);
        audit.record(person.getArchiveId(), actorId, "Person", personId,
                AuditLog.Action.STATUS_CHANGE, null,
                "{\"deathDate\":\"" + deathDate + "\"}", "Death recorded");
        return person;
    }

    /**
     * Walks ACTIVE + IDENTIFIED parentage edges upward. Unidentified parents are
     * reported as a stop marker, not invented as people (decision D5).
     */
    @Transactional(readOnly = true)
    public List<AncestryEdge> ancestry(UUID personId, int maxGenerations) {
        Person start = get(personId);
        List<AncestryEdge> edges = new ArrayList<>();
        Deque<UUID> frontier = new ArrayDeque<>();
        Deque<Integer> depth = new ArrayDeque<>();
        Set<UUID> visited = new HashSet<>();
        frontier.add(personId);
        depth.add(0);
        visited.add(personId);

        while (!frontier.isEmpty()) {
            UUID current = frontier.removeFirst();
            int generation = depth.removeFirst();
            if (generation >= maxGenerations) {
                continue;
            }
            String currentName = names.findDisplayName(current).orElse(null);
            for (Parentage parentage : parentages.findByChildIdAndStatusOrderByCreatedAtAsc(
                    current, Parentage.Status.ACTIVE)) {
                UUID parentId = parentage.getParentId();
                String parentName = parentId == null ? null
                        : names.findDisplayName(parentId).orElse(null);
                edges.add(new AncestryEdge(current, currentName, parentId, parentName,
                        parentage.getType().name(), parentage.getRole().name(),
                        parentage.getIdentityStatus().name(), generation + 1));
                if (parentId != null && visited.add(parentId)) {
                    frontier.addLast(parentId);
                    depth.addLast(generation + 1);
                }
            }
        }
        if (edges.isEmpty() && start != null) {
            return List.of();
        }
        return edges;
    }
}
