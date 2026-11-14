package com.platform.familytree.family.controller;

import com.platform.familytree.family.model.CareRelationship;
import com.platform.familytree.family.service.CareService;
import com.platform.familytree.family.dto.RecordCareRequest;
import com.platform.familytree.shared.web.ActorResolver;
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
@RequestMapping("/api/v1/care-relationships")
public class CareController {

    private final CareService care;
    private final ActorResolver actors;

    public CareController(CareService care, ActorResolver actors) {
        this.care = care;
        this.actors = actors;
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CareRelationship record(@RequestBody RecordCareRequest request) {
        return care.record(request.caregiverId(), request.recipientId(), request.careType(),
                request.startDate(), actors.resolve(request.actorId()));
    }

    @GetMapping("/recipient/{id}")
    public List<CareRelationship> forRecipient(@PathVariable UUID id) {
        return care.forRecipient(id);
    }

    @GetMapping("/caregiver/{id}")
    public List<CareRelationship> byCaregiver(@PathVariable UUID id) {
        return care.byCaregiver(id);
    }
}
