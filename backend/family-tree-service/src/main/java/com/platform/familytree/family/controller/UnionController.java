package com.platform.familytree.family.controller;

import com.platform.familytree.family.model.FamilyUnion;
import com.platform.familytree.family.model.UnionParticipant;
import com.platform.familytree.family.service.UnionService;
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
@RequestMapping("/api/v1/unions")
public class UnionController {

    private final UnionService unions;
    private final ActorResolver actors;

    public UnionController(UnionService unions, ActorResolver actors) {
        this.unions = unions;
        this.actors = actors;
    }

    public record Participant(UUID personId, UnionParticipant.Role role) {
    }

    public record CreateUnionRequest(FamilyUnion.Type unionType,
                                     LocalDate startDate,
                                     List<Participant> participants,
                                     UUID actorId) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyUnion create(@RequestBody CreateUnionRequest request) {
        List<UnionService.ParticipantInput> inputs = request.participants() == null
                ? List.of()
                : request.participants().stream()
                        .map(p -> new UnionService.ParticipantInput(p.personId(), p.role()))
                        .toList();
        return unions.create(request.unionType(), request.startDate(), inputs,
                actors.resolve(request.actorId()));
    }

    @GetMapping
    public List<FamilyUnion> list() {
        return unions.list();
    }

    @GetMapping("/{id}/participants")
    public List<UnionParticipant> participants(@PathVariable UUID id) {
        return unions.participantsOf(id);
    }
}
