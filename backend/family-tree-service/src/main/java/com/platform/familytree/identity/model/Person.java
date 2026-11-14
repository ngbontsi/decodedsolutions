package com.platform.familytree.identity.model;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "person")
public class Person {

    public enum Gender {
        MALE, FEMALE, OTHER, UNKNOWN
    }

    public enum DatePrecision {
        DAY, MONTH, YEAR, DECADE, APPROXIMATE, UNKNOWN
    }

    public enum Visibility {
        PUBLIC, FAMILY, RESTRICTED, PRIVATE
    }

    public enum Status {
        ACTIVE, MERGED, ENTERED_IN_ERROR
    }

    @Id
    private UUID id;

    @Column(name = "archive_id", nullable = false)
    private UUID archiveId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender = Gender.UNKNOWN;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "birth_date_precision", nullable = false)
    private DatePrecision birthDatePrecision = DatePrecision.UNKNOWN;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "death_date_precision", nullable = false)
    private DatePrecision deathDatePrecision = DatePrecision.UNKNOWN;

    private String biography;

    @Column(name = "is_living", nullable = false)
    private boolean living = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility = Visibility.FAMILY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "merged_into_id")
    private UUID mergedIntoId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Person() {
    }

    public static Person create(UUID archiveId, Gender gender) {
        Person person = new Person();
        person.id = Uuid7.generate();
        person.archiveId = archiveId;
        person.gender = gender == null ? Gender.UNKNOWN : gender;
        return person;
    }

    public void describe(LocalDate birthDate, DatePrecision birthPrecision,
                         String biography, Visibility visibility) {
        ensureOrder(birthDate, deathDate, "birth date is after death date");
        this.birthDate = birthDate;
        this.birthDatePrecision = birthPrecision == null ? DatePrecision.UNKNOWN : birthPrecision;
        this.biography = biography;
        if (visibility != null) {
            this.visibility = visibility;
        }
    }

    public void recordDeath(LocalDate deathDate, DatePrecision precision) {
        ensureOrder(birthDate, deathDate, "death date is before birth date");
        this.deathDate = deathDate;
        this.deathDatePrecision = precision == null ? DatePrecision.UNKNOWN : precision;
        this.living = deathDate == null;
    }

    public void mergeInto(UUID targetId) {
        if (targetId == null || targetId.equals(id)) {
            throw com.platform.familytree.shared.error.DomainException.rule(
                    "Cannot merge a person into itself");
        }
        this.status = Status.MERGED;
        this.mergedIntoId = targetId;
    }

    private static void ensureOrder(LocalDate earlier, LocalDate later, String message) {
        if (earlier != null && later != null && later.isBefore(earlier)) {
            throw com.platform.familytree.shared.error.DomainException.rule(message);
        }
    }

    @jakarta.persistence.PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getArchiveId() {
        return archiveId;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public DatePrecision getBirthDatePrecision() {
        return birthDatePrecision;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public DatePrecision getDeathDatePrecision() {
        return deathDatePrecision;
    }

    public String getBiography() {
        return biography;
    }

    public boolean isLiving() {
        return living;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public Status getStatus() {
        return status;
    }

    public UUID getMergedIntoId() {
        return mergedIntoId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
