package com.platform.familytree.heritage;

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
@RequestMapping("/api/v1/heritage")
public class HeritageController {

    private final HeritageService heritage;
    private final ActorResolver actors;

    public HeritageController(HeritageService heritage, ActorResolver actors) {
        this.heritage = heritage;
        this.actors = actors;
    }

    public record CreateLocationRequest(String name, Location.Type locationType,
                                        UUID parentLocationId) {
    }

    public record CreateEventRequest(String title,
                                     String description,
                                     LocalDate eventDate,
                                     HistoricalEvent.Precision datePrecision,
                                     UUID locationId,
                                     HistoricalEvent.Type eventType,
                                     UUID actorId) {
    }

    public record CreateOrganisationRequest(String name,
                                            Organisation.Type organisationType,
                                            LocalDate foundedDate,
                                            String description,
                                            UUID actorId) {
    }

    public record AddMembershipRequest(UUID personId,
                                       String role,
                                       LocalDate startDate,
                                       UUID actorId) {
    }

    @PostMapping("/locations")
    @ResponseStatus(HttpStatus.CREATED)
    public Location createLocation(@RequestBody CreateLocationRequest request) {
        return heritage.createLocation(request.name(), request.locationType(),
                request.parentLocationId());
    }

    @GetMapping("/locations")
    public List<Location> locations() {
        return heritage.listLocations();
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public HistoricalEvent createEvent(@RequestBody CreateEventRequest request) {
        return heritage.createEvent(request.title(), request.description(), request.eventDate(),
                request.datePrecision(), request.locationId(), request.eventType(),
                actors.resolve(request.actorId()));
    }

    @GetMapping("/events")
    public List<HistoricalEvent> events() {
        return heritage.listEvents();
    }

    @PostMapping("/organisations")
    @ResponseStatus(HttpStatus.CREATED)
    public Organisation createOrganisation(@RequestBody CreateOrganisationRequest request) {
        return heritage.createOrganisation(request.name(), request.organisationType(),
                request.foundedDate(), request.description(), actors.resolve(request.actorId()));
    }

    @GetMapping("/organisations")
    public List<Organisation> organisations() {
        return heritage.listOrganisations();
    }

    @PostMapping("/organisations/{id}/memberships")
    @ResponseStatus(HttpStatus.CREATED)
    public OrganisationMembership addMembership(@PathVariable UUID id,
                                                @RequestBody AddMembershipRequest request) {
        return heritage.addMembership(request.personId(), id, request.role(),
                request.startDate(), actors.resolve(request.actorId()));
    }

    @GetMapping("/persons/{personId}/memberships")
    public List<OrganisationMembership> membershipsOf(@PathVariable UUID personId) {
        return heritage.membershipsOf(personId);
    }
}
