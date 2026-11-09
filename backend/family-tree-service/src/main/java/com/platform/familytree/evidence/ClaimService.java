package com.platform.familytree.evidence;

import com.platform.familytree.identity.ArchiveService;
import com.platform.familytree.shared.audit.AuditLog;
import com.platform.familytree.shared.audit.AuditWriter;
import com.platform.familytree.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClaimService {

    private final ClaimRepository claims;
    private final ClaimEvidenceRepository evidenceLinks;
    private final DocumentRepository documents;
    private final ArchiveService archives;
    private final AuditWriter audit;

    public ClaimService(ClaimRepository claims,
                        ClaimEvidenceRepository evidenceLinks,
                        DocumentRepository documents,
                        ArchiveService archives,
                        AuditWriter audit) {
        this.claims = claims;
        this.evidenceLinks = evidenceLinks;
        this.documents = documents;
        this.archives = archives;
        this.audit = audit;
    }

    @Transactional
    public Claim create(UUID subjectPersonId, UUID subjectOrganisationId, UUID subjectEventId,
                        String statement, UUID submittedBy) {
        Claim claim = Claim.create(archives.defaultArchiveId(), subjectPersonId,
                subjectOrganisationId, subjectEventId, statement, submittedBy);
        claims.save(claim);
        audit.record(claim.getArchiveId(), submittedBy, "Claim", claim.getId(),
                AuditLog.Action.CREATE, null, "{\"statement\":\"" + claim.getStatement() + "\"}",
                "Claim submitted");
        return claim;
    }

    @Transactional
    public ClaimEvidence attachEvidence(UUID claimId, UUID documentId,
                                        ClaimEvidence.Stance stance,
                                        ClaimEvidence.Weight weight,
                                        String note, UUID addedBy) {
        Claim claim = claims.findById(claimId)
                .orElseThrow(() -> DomainException.notFound("Claim", claimId));
        if (!documents.existsById(documentId)) {
            throw DomainException.notFound("Evidence document", documentId);
        }
        ClaimEvidence link = ClaimEvidence.attach(claimId, documentId, stance, weight, note, addedBy);
        evidenceLinks.save(link);
        audit.record(claim.getArchiveId(), addedBy, "ClaimEvidence", claimId,
                AuditLog.Action.CREATE, null,
                "{\"evidenceId\":\"" + documentId + "\",\"stance\":\"" + stance + "\"}",
                "Evidence attached to claim");
        return link;
    }

    @Transactional
    public Claim transition(UUID claimId, Claim.Status target, UUID actorId) {
        Claim claim = claims.findById(claimId)
                .orElseThrow(() -> DomainException.notFound("Claim", claimId));
        Claim.Status before = claim.getStatus();
        claim.moveTo(target);
        claims.save(claim);
        audit.record(claim.getArchiveId(), actorId, "Claim", claimId,
                AuditLog.Action.STATUS_CHANGE,
                "{\"status\":\"" + before + "\"}",
                "{\"status\":\"" + claim.getStatus() + "\"}",
                "Claim status changed");
        return claim;
    }

    @Transactional(readOnly = true)
    public Claim get(UUID claimId) {
        return claims.findById(claimId)
                .orElseThrow(() -> DomainException.notFound("Claim", claimId));
    }

    @Transactional(readOnly = true)
    public List<Claim> forPerson(UUID personId) {
        return claims.findBySubjectPersonIdOrderBySubmittedAtDesc(personId);
    }

    @Transactional(readOnly = true)
    public List<ClaimEvidence> evidenceOf(UUID claimId) {
        return evidenceLinks.findByIdClaimId(claimId);
    }
}
