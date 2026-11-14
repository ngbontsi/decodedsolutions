package com.platform.familytree.evidence.model;

import com.platform.familytree.shared.error.DomainException;
import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * One assertion, by one contributor, with its own evidence trail.
 * Evidence attaches here, never to the person (decision D6).
 */
@Entity
@Table(name = "claim")
public class Claim {

    public enum Status {
        UNVERIFIED, UNDER_REVIEW, SUPPORTED, DISPUTED, RETRACTED
    }

    public enum Confidence {
        LOW, MEDIUM, HIGH
    }

    private static final java.util.Map<Status, Set<Status>> ALLOWED = java.util.Map.of(
            Status.UNVERIFIED, EnumSet.of(Status.UNDER_REVIEW),
            Status.UNDER_REVIEW, EnumSet.of(Status.SUPPORTED, Status.DISPUTED, Status.RETRACTED),
            Status.SUPPORTED, EnumSet.of(Status.DISPUTED, Status.RETRACTED),
            Status.DISPUTED, EnumSet.of(Status.UNDER_REVIEW, Status.SUPPORTED, Status.RETRACTED),
            Status.RETRACTED, EnumSet.noneOf(Status.class)
    );

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Column(name = "subject_person_id")
    private UUID subjectPersonId;

    @Column(name = "subject_organisation_id")
    private UUID subjectOrganisationId;

    @Column(name = "subject_event_id")
    private UUID subjectEventId;

    @Column(nullable = false)
    private String statement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.UNVERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Confidence confidence = Confidence.LOW;

    @Column(name = "submitted_by", nullable = false)
    private UUID submittedBy;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    protected Claim() {
    }

    public static Claim create(UUID archiveId, UUID subjectPersonId, UUID subjectOrganisationId,
                               UUID subjectEventId, String statement, UUID submittedBy) {
        long subjects = java.util.stream.Stream
                .of(subjectPersonId, subjectOrganisationId, subjectEventId)
                .filter(java.util.Objects::nonNull)
                .count();
        if (subjects != 1) {
            throw DomainException.invalid("A claim must have exactly one subject");
        }
        Claim claim = new Claim();
        claim.id = Uuid7.generate();
        claim.archiveId = archiveId;
        claim.subjectPersonId = subjectPersonId;
        claim.subjectOrganisationId = subjectOrganisationId;
        claim.subjectEventId = subjectEventId;
        claim.statement = statement;
        claim.submittedBy = submittedBy;
        claim.submittedAt = Instant.now();
        return claim;
    }

    public void moveTo(Status target) {
        if (!ALLOWED.getOrDefault(status, Set.of()).contains(target)) {
            throw DomainException.rule("Illegal claim transition: " + status + " -> " + target);
        }
        this.status = target;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchiveId() {
        return archiveId;
    }

    public UUID getSubjectPersonId() {
        return subjectPersonId;
    }

    public UUID getSubjectOrganisationId() {
        return subjectOrganisationId;
    }

    public UUID getSubjectEventId() {
        return subjectEventId;
    }

    public String getStatement() {
        return statement;
    }

    public Status getStatus() {
        return status;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public UUID getSubmittedBy() {
        return submittedBy;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
