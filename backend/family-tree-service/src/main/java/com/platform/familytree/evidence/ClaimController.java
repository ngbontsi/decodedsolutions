package com.platform.familytree.evidence;

import com.platform.familytree.shared.web.ActorResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
public class ClaimController {

    private final ClaimService claims;
    private final ActorResolver actors;

    public ClaimController(ClaimService claims, ActorResolver actors) {
        this.claims = claims;
        this.actors = actors;
    }

    public record CreateClaimRequest(UUID subjectPersonId,
                                     UUID subjectOrganisationId,
                                     UUID subjectEventId,
                                     String statement,
                                     UUID actorId) {
    }

    public record AttachEvidenceRequest(UUID documentId,
                                        ClaimEvidence.Stance stance,
                                        ClaimEvidence.Weight weight,
                                        String note,
                                        UUID actorId) {
    }

    public record TransitionRequest(Claim.Status status, UUID actorId) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Claim create(@RequestBody CreateClaimRequest request) {
        return claims.create(request.subjectPersonId(), request.subjectOrganisationId(),
                request.subjectEventId(), request.statement(), actors.resolve(request.actorId()));
    }

    @GetMapping("/{id}")
    public Claim get(@PathVariable UUID id) {
        return claims.get(id);
    }

    @GetMapping("/person/{personId}")
    public List<Claim> forPerson(@PathVariable UUID personId) {
        return claims.forPerson(personId);
    }

    @PostMapping("/{id}/evidence")
    @ResponseStatus(HttpStatus.CREATED)
    public ClaimEvidence attachEvidence(@PathVariable UUID id,
                                        @RequestBody AttachEvidenceRequest request) {
        return claims.attachEvidence(id, request.documentId(), request.stance(),
                request.weight(), request.note(), actors.resolve(request.actorId()));
    }

    @GetMapping("/{id}/evidence")
    public List<ClaimEvidence> evidence(@PathVariable UUID id) {
        return claims.evidenceOf(id);
    }

    @PatchMapping("/{id}/status")
    public Claim transition(@PathVariable UUID id, @RequestBody TransitionRequest request) {
        return claims.transition(id, request.status(), actors.resolve(request.actorId()));
    }
}
