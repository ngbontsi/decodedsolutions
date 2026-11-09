package com.platform.familytree.evidence;

import com.platform.familytree.identity.ArchiveService;
import com.platform.familytree.shared.audit.AuditLog;
import com.platform.familytree.shared.audit.AuditWriter;
import com.platform.familytree.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class EvidenceService {

    private final SourceRepository sources;
    private final DocumentRepository documents;
    private final ArchiveService archives;
    private final AuditWriter audit;

    public EvidenceService(SourceRepository sources,
                           DocumentRepository documents,
                           ArchiveService archives,
                           AuditWriter audit) {
        this.sources = sources;
        this.documents = documents;
        this.archives = archives;
        this.audit = audit;
    }

    @Transactional
    public Source createSource(Source.Type type, String title, String authorOrSpeaker) {
        Source source = Source.create(archives.defaultArchiveId(), type, title, authorOrSpeaker);
        sources.save(source);
        return source;
    }

    @Transactional(readOnly = true)
    public List<Source> listSources() {
        return sources.findByArchiveIdOrderByCreatedAtAsc(archives.defaultArchiveId());
    }

    @Transactional
    public Document addTestimony(UUID sourceId, String description) {
        requireSource(sourceId);
        return documents.save(Document.testimony(sourceId, description));
    }

    @Transactional
    public Document addFile(UUID sourceId, String mediaType, String storageKey,
                            String originalFilename, String checksumSha256) {
        requireSource(sourceId);
        return documents.save(Document.file(sourceId, mediaType, storageKey,
                originalFilename, checksumSha256));
    }

    @Transactional(readOnly = true)
    public List<Document> documentsOf(UUID sourceId) {
        return documents.findBySourceIdOrderByCreatedAtAsc(sourceId);
    }

    private Source requireSource(UUID id) {
        return sources.findById(id).orElseThrow(() -> DomainException.notFound("Source", id));
    }
}
