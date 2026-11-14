package com.platform.familytree.community.model;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "review")
public class Review {

    public enum Decision {
        APPROVE, REJECT, REQUEST_CHANGES
    }

    @Id
    private UUID id;

    @Column(name = "claim_id", nullable = false)
    private UUID claimId;

    @Column(name = "reviewer_id", nullable = false)
    private UUID reviewerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision decision;

    private String comment;

    @Column(name = "reviewed_at", nullable = false)
    private Instant reviewedAt;

    protected Review() {
    }

    public static Review of(UUID claimId, UUID reviewerId, Decision decision, String comment) {
        Review review = new Review();
        review.id = Uuid7.generate();
        review.claimId = claimId;
        review.reviewerId = reviewerId;
        review.decision = decision;
        review.comment = comment;
        review.reviewedAt = Instant.now();
        return review;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClaimId() {
        return claimId;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public Decision getDecision() {
        return decision;
    }

    public String getComment() {
        return comment;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }
}
