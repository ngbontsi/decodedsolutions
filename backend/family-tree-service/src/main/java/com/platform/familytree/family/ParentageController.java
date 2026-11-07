package com.platform.familytree.family;

import com.platform.familytree.shared.error.DomainException;
import com.platform.familytree.shared.web.ActorResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parentages")
public class ParentageController {

    private final ParentageService parentages;
    private final ActorResolver actors;

    public ParentageController(ParentageService parentages, ActorResolver actors) {
        this.parentages = parentages;
        this.actors = actors;
    }

    public record RecordParentageRequest(UUID childId,
                                         UUID parentId,
                                         Parentage.Type parentageType,
                                         Parentage.Role parentRole,
                                         Parentage.IdentityStatus identityStatus,
                                         String reportedName,
                                         LocalDate validFrom,
                                         UUID actorId) {
    }

    public record SupersedeRequest(UUID newParentId, LocalDate on, UUID actorId) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Parentage record(@RequestBody RecordParentageRequest request) {
        UUID actor = actors.resolve(request.actorId());
        Parentage.Type type = request.parentageType() == null
                ? Parentage.Type.BIOLOGICAL : request.parentageType();
        Parentage.IdentityStatus identity = request.identityStatus() == null
                ? Parentage.IdentityStatus.IDENTIFIED : request.identityStatus();

        return switch (identity) {
            case IDENTIFIED -> parentages.recordIdentified(request.childId(), request.parentId(),
                    type, request.parentRole(), request.validFrom(), actor);
            case UNIDENTIFIED -> parentages.recordUnidentified(request.childId(), type,
                    request.parentRole(), request.validFrom(), actor);
            case UNRECORDED -> parentages.recordUnrecorded(request.childId(), type,
                    request.parentRole(), request.reportedName(), request.validFrom(), actor);
            case UNKNOWN -> parentages.recordUnknown(request.childId(), type,
                    request.parentRole(), request.validFrom(), actor);
        };
    }

    @GetMapping("/child/{childId}")
    public List<Parentage> forChild(@PathVariable UUID childId) {
        return parentages.forChild(childId);
    }

    @PostMapping("/{id}/supersede")
    public Parentage supersede(@PathVariable UUID id, @RequestBody SupersedeRequest request) {
        if (request.newParentId() == null) {
            throw DomainException.invalid("newParentId is required");
        }
        LocalDate on = request.on() == null ? LocalDate.now() : request.on();
        return parentages.supersede(id, request.newParentId(), on, actors.resolve(request.actorId()));
    }
}
