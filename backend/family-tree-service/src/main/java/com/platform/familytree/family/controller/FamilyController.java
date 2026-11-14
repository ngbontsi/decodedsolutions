package com.platform.familytree.family.controller;

import com.platform.familytree.family.dto.AddMemberRequest;
import com.platform.familytree.family.dto.CreateGroupRequest;
import com.platform.familytree.family.model.FamilyGroup;
import com.platform.familytree.family.model.FamilyMembership;
import com.platform.familytree.family.service.FamilyService;
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
@RequestMapping("/api/v1/families")
public class FamilyController {

    private final FamilyService families;
    private final ActorResolver actors;

    public FamilyController(FamilyService families, ActorResolver actors) {
        this.families = families;
        this.actors = actors;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyGroup create(@RequestBody CreateGroupRequest request) {
        return families.createGroup(request.name(), request.description(),
                request.lineageType(), actors.resolve(request.actorId()));
    }

    @GetMapping
    public List<FamilyGroup> list() {
        return families.listGroups();
    }

    @GetMapping("/{id}/members")
    public List<FamilyMembership> members(@PathVariable UUID id) {
        return families.membersOfGroup(id);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyMembership addMember(@PathVariable UUID id, @RequestBody AddMemberRequest request) {
        return families.addMembership(request.personId(), id, request.connectionType(),
                request.basis(), request.validFrom(), actors.resolve(request.actorId()));
    }

    @GetMapping("/by-person/{personId}")
    public List<FamilyMembership> membershipsOfPerson(@PathVariable UUID personId) {
        return families.membershipsOfPerson(personId);
    }
}
