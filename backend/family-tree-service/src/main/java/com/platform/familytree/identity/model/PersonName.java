package com.platform.familytree.identity.model;

import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "person_name")
public class PersonName {

    public enum Type {
        BIRTH, MAIDEN, MARRIED, CLAN, NICKNAME, ALIAS, RELIGIOUS, OTHER
    }

    @Id
    private UUID id;

    @Column(name = "person_id", nullable = false)
    private UUID personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "name_type", nullable = false)
    private Type nameType;

    @Column(name = "given_names")
    private String givenNames;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String script = "LATIN";

    private String language;

    @Column(name = "is_preferred", nullable = false)
    private boolean preferred;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "source_id")
    private UUID sourceId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PersonName() {
    }

    public static PersonName of(UUID personId,
                                Type nameType,
                                String fullName,
                                String givenNames,
                                String familyName,
                                LocalDate validFrom) {
        if (fullName == null || fullName.isBlank()) {
            throw com.platform.familytree.shared.error.DomainException.invalid(
                    "fullName must not be blank");
        }
        PersonName name = new PersonName();
        name.id = Uuid7.generate();
        name.personId = personId;
        name.nameType = nameType;
        name.fullName = fullName.trim();
        name.givenNames = givenNames;
        name.familyName = familyName;
        name.validFrom = validFrom;
        name.createdAt = Instant.now();
        return name;
    }

    public void makePreferred() {
        this.preferred = true;
    }

    public void revokePreferred() {
        this.preferred = false;
    }

    public void closeOn(LocalDate date) {
        if (validFrom != null && date != null && !date.isAfter(validFrom)) {
            throw com.platform.familytree.shared.error.DomainException.rule(
                    "A name must end after it starts");
        }
        this.validTo = date;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPersonId() {
        return personId;
    }

    public Type getNameType() {
        return nameType;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getScript() {
        return script;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
