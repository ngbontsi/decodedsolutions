package com.platform.familytree.evidence;

import com.platform.familytree.shared.error.DomainException;
import com.platform.familytree.shared.ids.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** The physical form of evidence: a file, a recording, or a written testimony. */
@Entity
@Table(name = "document")
public class Document {

    @Id
    private UUID id;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "media_type", nullable = false)
    private String mediaType;

    @Column(name = "storage_key")
    private String storageKey;

    @Column(name = "original_filename")
    private String originalFilename;

    @Column(name = "checksum_sha256")
    private String checksumSha256;

    @Column(name = "captured_date")
    private LocalDate capturedDate;

    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Document() {
    }

    public static Document testimony(UUID sourceId, String description) {
        Document document = new Document();
        document.id = Uuid7.generate();
        document.sourceId = sourceId;
        document.mediaType = "TESTIMONY";
        document.description = description;
        document.createdAt = Instant.now();
        return document;
    }

    public static Document file(UUID sourceId, String mediaType, String storageKey,
                                String originalFilename, String checksumSha256) {
        if (checksumSha256 == null || checksumSha256.length() != 64) {
            throw DomainException.invalid(
                    "A stored file requires a 64-character SHA-256 checksum");
        }
        Document document = new Document();
        document.id = Uuid7.generate();
        document.sourceId = sourceId;
        document.mediaType = mediaType;
        document.storageKey = storageKey;
        document.originalFilename = originalFilename;
        document.checksumSha256 = checksumSha256;
        document.createdAt = Instant.now();
        return document;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getChecksumSha256() {
        return checksumSha256;
    }

    public LocalDate getCapturedDate() {
        return capturedDate;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
