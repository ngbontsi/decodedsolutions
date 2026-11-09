package com.platform.familytree.evidence;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evidence")
public class EvidenceController {

    private final EvidenceService evidence;

    public EvidenceController(EvidenceService evidence) {
        this.evidence = evidence;
    }

    public record CreateSourceRequest(Source.Type sourceType,
                                      String title,
                                      String authorOrSpeaker) {
    }

    public record AddDocumentRequest(String mediaType,
                                     String storageKey,
                                     String originalFilename,
                                     String checksumSha256,
                                     String description) {
    }

    @PostMapping("/sources")
    @ResponseStatus(HttpStatus.CREATED)
    public Source createSource(@RequestBody CreateSourceRequest request) {
        return evidence.createSource(request.sourceType(), request.title(),
                request.authorOrSpeaker());
    }

    @GetMapping("/sources")
    public List<Source> listSources() {
        return evidence.listSources();
    }

    @PostMapping("/sources/{id}/documents")
    @ResponseStatus(HttpStatus.CREATED)
    public Document addDocument(@PathVariable UUID id, @RequestBody AddDocumentRequest request) {
        if (request.storageKey() == null) {
            return evidence.addTestimony(id, request.description());
        }
        return evidence.addFile(id, request.mediaType(), request.storageKey(),
                request.originalFilename(), request.checksumSha256());
    }

    @GetMapping("/sources/{id}/documents")
    public List<Document> documents(@PathVariable UUID id) {
        return evidence.documentsOf(id);
    }
}
