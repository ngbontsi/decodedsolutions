package com.platform.familytree.evidence.dto;

public record AddDocumentRequest(String mediaType,
                                 String storageKey,
                                 String originalFilename,
                                 String checksumSha256,
                                 String description) {
}