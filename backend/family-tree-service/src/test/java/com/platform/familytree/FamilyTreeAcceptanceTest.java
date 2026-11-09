package com.platform.familytree;

import com.platform.familytree.community.ContributorService;
import com.platform.familytree.evidence.Claim;
import com.platform.familytree.evidence.ClaimEvidence;
import com.platform.familytree.evidence.ClaimService;
import com.platform.familytree.evidence.Document;
import com.platform.familytree.evidence.EvidenceService;
import com.platform.familytree.evidence.Source;
import com.platform.familytree.family.CareRelationship;
import com.platform.familytree.family.CareService;
import com.platform.familytree.family.FamilyGroup;
import com.platform.familytree.family.FamilyMembership;
import com.platform.familytree.family.FamilyService;
import com.platform.familytree.family.Parentage;
import com.platform.familytree.family.ParentageService;
import com.platform.familytree.identity.Person;
import com.platform.familytree.identity.PersonName;
import com.platform.familytree.identity.PersonService;
import com.platform.familytree.shared.error.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Acceptance tests FT-001 .. FT-010 from docs/developer-docs/family-tree/04-test-plan.md.
 * Runs against a real Postgres because the invariants live in SQL as well as Java.
 */
@SpringBootTest
@ActiveProfiles("test")
class FamilyTreeAcceptanceTest {

    @Autowired private PersonService persons;
    @Autowired private FamilyService families;
    @Autowired private ParentageService parentages;
    @Autowired private CareService care;
    @Autowired private ClaimService claims;
    @Autowired private EvidenceService evidence;
    @Autowired private ContributorService contributors;
    @Autowired private com.platform.familytree.family.UnionService unionService;
    @Autowired private JdbcTemplate jdbc;

    @BeforeEach
    void cleanBusinessTables() {
        jdbc.execute("""
                TRUNCATE TABLE claim_evidence, review, claim, consent,
                               parentage, care_relationship,
                               family_union_participant, family_union,
                               organisation_membership, historical_event, organisation,
                               location, family_membership, family_group,
                               person_name, person, document, source, audit_log
                CASCADE
                """);
    }

    private UUID actor() {
        return contributors.systemContributorId();
    }

    private UUID newPerson(String name) {
        Person person = persons.create(Person.Gender.UNKNOWN, null, null, null,
                Person.Visibility.FAMILY, actor());
        persons.addName(person.getId(), PersonName.Type.BIRTH, name, null, null, null, true, actor());
        return person.getId();
    }

    @Test
    @DisplayName("FT-001: a person can exist with no name at all")
    void personWithoutNameIsValid() {
        Person person = persons.create(Person.Gender.UNKNOWN, null, null, null,
                Person.Visibility.FAMILY, actor());
        assertThat(persons.names(person.getId())).isEmpty();
        assertThat(persons.displayName(person.getId())).isNull();
        assertThat(person.getGender()).isEqualTo(Person.Gender.UNKNOWN);
    }

    @Test
    @DisplayName("FT-002: care never implies parentage")
    void careDoesNotCreateParentage() {
        UUID gran = newPerson("Nomvula Dlamini");
        UUID child = newPerson("Thandi Dlamini");
        care.record(gran, child, CareRelationship.Type.GUARDIAN, LocalDate.of(1994, 3, 1), actor());

        assertThat(parentages.forChild(child)).isEmpty();
        assertThat(care.forRecipient(child)).hasSize(1);
    }

    @Test
    @DisplayName("FT-003: membership is explicit, never derived from parentage")
    void membershipIsNotDerivedFromParentage() {
        UUID parent = newPerson("Sipho Nkosi");
        UUID child = newPerson("Lerato Nkosi");
        FamilyGroup group = families.createGroup("Nkosi", null,
                FamilyGroup.LineageType.PATRILINEAL, actor());

        parentages.recordIdentified(child, parent, Parentage.Type.BIOLOGICAL,
                Parentage.Role.FATHER, null, actor());
        assertThat(families.membershipsOfPerson(child)).isEmpty();

        families.addMembership(child, group.getId(), FamilyMembership.ConnectionType.MEMBER,
                FamilyMembership.Basis.SELF_IDENTIFIED, LocalDate.of(2020, 1, 1), actor());
        assertThat(families.membershipsOfPerson(child)).hasSize(1);
        assertThat(families.membershipsOfPerson(child).get(0).getBasis())
                .isEqualTo(FamilyMembership.Basis.SELF_IDENTIFIED);
    }

    @Test
    @DisplayName("FT-004: the four parent identity states are all recordable")
    void fourIdentityStates() {
        UUID child = newPerson("Bongani Mokoena");

        Parentage identified = parentages.recordIdentified(child, newPerson("Tata Mokoena"),
                Parentage.Type.BIOLOGICAL, Parentage.Role.FATHER, null, actor());
        Parentage unidentified = parentages.recordUnidentified(child, Parentage.Type.BIOLOGICAL,
                Parentage.Role.MOTHER, null, actor());
        Parentage unrecorded = parentages.recordUnrecorded(child, Parentage.Type.ADOPTIVE,
                Parentage.Role.PARENT, "Aunt Sarah", null, actor());
        Parentage unknown = parentages.recordUnknown(child, Parentage.Type.LEGAL,
                Parentage.Role.PARENT, null, actor());

        assertThat(identified.getIdentityStatus()).isEqualTo(Parentage.IdentityStatus.IDENTIFIED);
        assertThat(unidentified.getIdentityStatus()).isEqualTo(Parentage.IdentityStatus.UNIDENTIFIED);
        assertThat(unrecorded.getIdentityStatus()).isEqualTo(Parentage.IdentityStatus.UNRECORDED);
        assertThat(unrecorded.getReportedName()).isEqualTo("Aunt Sarah");
        assertThat(unknown.getIdentityStatus()).isEqualTo(Parentage.IdentityStatus.UNKNOWN);
    }

    @Test
    @DisplayName("FT-004: identified parentage requires a parent id")
    void identifiedRequiresParent() {
        UUID child = newPerson("Zanele Khumalo");
        assertThatThrownBy(() -> parentages.recordIdentified(child, null,
                Parentage.Type.BIOLOGICAL, Parentage.Role.MOTHER, null, actor()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("parentId");
    }

    @Test
    @DisplayName("FT-005: at most two biological parents, one per role")
    void biologicalParentLimit() {
        UUID child = newPerson("Ayanda Zulu");
        UUID mother = newPerson("Nomsa Zulu");
        UUID father = newPerson("Jabulani Zulu");
        UUID third = newPerson("Someone Else");

        parentages.recordIdentified(child, mother, Parentage.Type.BIOLOGICAL,
                Parentage.Role.MOTHER, null, actor());
        parentages.recordIdentified(child, father, Parentage.Type.BIOLOGICAL,
                Parentage.Role.FATHER, null, actor());

        assertThatThrownBy(() -> parentages.recordIdentified(child, third,
                Parentage.Type.BIOLOGICAL, Parentage.Role.FATHER, null, actor()))
                .isInstanceOf(DomainException.class);

        assertThat(parentages.forChild(child)).hasSize(2);
    }

    @Test
    @DisplayName("FT-006: a name change closes the old name, never deletes it")
    void nameChangePreservesHistory() {
        UUID person = newPerson("Thabo Mbeki");
        PersonName second = persons.addName(person, PersonName.Type.MARRIED,
                "Thabo Motsepe", null, "Motsepe", LocalDate.of(2015, 6, 1), true, actor());

        List<PersonName> names = persons.names(person);
        assertThat(names).hasSize(2);

        PersonName first = names.stream()
                .filter(n -> n.getNameType() == PersonName.Type.BIRTH)
                .findFirst().orElseThrow();
        assertThat(first.isPreferred()).isFalse();
        assertThat(second.isPreferred()).isTrue();
        assertThat(persons.displayName(person)).isEqualTo("Thabo Motsepe");
    }

    @Test
    @DisplayName("FT-007: evidence attaches to a claim, not to the person")
    void evidenceAttachesToClaim() {
        UUID person = newPerson("Winnie Madikizela");
        Claim claim = claims.create(person, null, null,
                "Winnie was born in Mbizana", actor());

        Source source = evidence.createSource(Source.Type.ORAL_HISTORY,
                "Family interview, 2019", "Aunt Grace");
        Document document = evidence.addTestimony(source.getId(), "Recorded at the family gathering");

        ClaimEvidence link = claims.attachEvidence(claim.getId(), document.getId(),
                ClaimEvidence.Stance.SUPPORTS, ClaimEvidence.Weight.STRONG, null, actor());

        assertThat(link.getStance()).isEqualTo(ClaimEvidence.Stance.SUPPORTS);
        assertThat(claims.evidenceOf(claim.getId())).hasSize(1);
    }

    @Test
    @DisplayName("FT-007c: a claim without a subject is rejected")
    void claimNeedsExactlyOneSubject() {
        assertThatThrownBy(() -> claims.create(null, null, null, "nowhere", actor()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("exactly one subject");
    }

    @Test
    @DisplayName("FT-008: identifying an unknown parent supersedes but never deletes")
    void supersedeKeepsOriginalRecord() {
        UUID child = newPerson("Sibusiso Ndlovu");
        Parentage original = parentages.recordUnidentified(child, Parentage.Type.BIOLOGICAL,
                Parentage.Role.FATHER, null, actor());
        UUID realFather = newPerson("Mandla Ndlovu");

        Parentage replacement = parentages.supersede(original.getId(), realFather,
                LocalDate.of(2024, 1, 1), actor());

        List<Parentage> all = parentages.forChild(child);
        assertThat(all).hasSize(2);

        Parentage closed = all.stream()
                .filter(p -> p.getId().equals(original.getId()))
                .findFirst().orElseThrow();
        assertThat(closed.getStatus()).isEqualTo(Parentage.Status.SUPERSEDED);
        assertThat(closed.getSupersededById()).isEqualTo(replacement.getId());
        assertThat(closed.getValidTo()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(replacement.getIdentityStatus()).isEqualTo(Parentage.IdentityStatus.IDENTIFIED);
    }

    @Test
    @DisplayName("FT-009: the claim state machine rejects illegal transitions")
    void claimStateMachine() {
        UUID person = newPerson("Chris Hani");
        Claim claim = claims.create(person, null, null, "Chris Hani was born in 1942", actor());

        assertThat(claim.getStatus()).isEqualTo(Claim.Status.UNVERIFIED);
        claims.transition(claim.getId(), Claim.Status.UNDER_REVIEW, actor());
        claims.transition(claim.getId(), Claim.Status.SUPPORTED, actor());

        assertThatThrownBy(() -> claims.transition(claim.getId(), Claim.Status.UNDER_REVIEW, actor()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Illegal claim transition");
    }

    @Test
    @DisplayName("FT-010: ancestry walks parentage edges upward and stops at unidentified parents")
    void ancestryTraversal() {
        UUID grandParent = newPerson("Gogo Nene");
        UUID parent = newPerson("Baba Nene");
        UUID child = newPerson("Sisi Nene");

        parentages.recordIdentified(child, parent, Parentage.Type.BIOLOGICAL,
                Parentage.Role.MOTHER, null, actor());
        parentages.recordIdentified(parent, grandParent, Parentage.Type.BIOLOGICAL,
                Parentage.Role.MOTHER, null, actor());
        parentages.recordUnidentified(grandParent, Parentage.Type.BIOLOGICAL,
                Parentage.Role.FATHER, null, actor());

        List<PersonService.AncestryEdge> edges = persons.ancestry(child, 5);

        assertThat(edges).hasSize(3);
        assertThat(edges).anySatisfy(edge -> {
            assertThat(edge.personId()).isEqualTo(child);
            assertThat(edge.parentId()).isEqualTo(parent);
            assertThat(edge.generation()).isEqualTo(1);
        });
        assertThat(edges).anySatisfy(edge -> {
            assertThat(edge.personId()).isEqualTo(grandParent);
            assertThat(edge.parentId()).isNull();
            assertThat(edge.identityStatus()).isEqualTo("UNIDENTIFIED");
            assertThat(edge.generation()).isEqualTo(3);
        });
    }

    @Test
    @DisplayName("FT-011: audit log rejects updates and deletes")
    void auditLogIsAppendOnly() {
        newPerson("Audit Probe");
        assertThat(jdbc.queryForObject("SELECT count(*) FROM audit_log", Long.class))
                .isGreaterThan(0);

        assertThatThrownBy(() -> jdbc.update("DELETE FROM audit_log"))
                .isInstanceOf(DataAccessException.class);
        assertThatThrownBy(() -> jdbc.update("UPDATE audit_log SET reason = 'rewritten'"))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    @DisplayName("FT-012: a union needs at least two distinct participants")
    void unionNeedsTwoPeople() {
        UUID one = newPerson("Person One");
        assertThatThrownBy(() -> unionService.create(
                com.platform.familytree.family.FamilyUnion.Type.MARRIAGE, null,
                List.of(new com.platform.familytree.family.UnionService.ParticipantInput(
                        one, com.platform.familytree.family.UnionParticipant.Role.SPOUSE)),
                actor()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("at least two participants");
    }

    @Test
    @DisplayName("FT-013: unions with two people are recorded with both participants")
    void unionWithTwoPeople() {
        UUID one = newPerson("Partner One");
        UUID two = newPerson("Partner Two");
        var union = unionService.create(com.platform.familytree.family.FamilyUnion.Type.MARRIAGE,
                LocalDate.of(1999, 9, 9),
                List.of(new com.platform.familytree.family.UnionService.ParticipantInput(
                                one, com.platform.familytree.family.UnionParticipant.Role.SPOUSE),
                        new com.platform.familytree.family.UnionService.ParticipantInput(
                                two, com.platform.familytree.family.UnionParticipant.Role.SPOUSE)),
                actor());
        assertThat(unionService.participantsOf(union.getId())).hasSize(2);
    }
}
