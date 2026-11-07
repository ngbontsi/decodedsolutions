package com.platform.familytree.identity;

import com.platform.familytree.shared.web.ActorResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    private final PersonService persons;
    private final ActorResolver actors;

    public PersonController(PersonService persons, ActorResolver actors) {
        this.persons = persons;
        this.actors = actors;
    }

    public record CreatePersonRequest(Person.Gender gender,
                                      LocalDate birthDate,
                                      Person.DatePrecision birthDatePrecision,
                                      String biography,
                                      Person.Visibility visibility,
                                      UUID actorId) {
    }

    public record AddNameRequest(PersonName.Type nameType,
                                 String fullName,
                                 String givenNames,
                                 String familyName,
                                 LocalDate validFrom,
                                 Boolean preferred,
                                 UUID actorId) {
    }

    public record DeathRequest(LocalDate deathDate,
                               Person.DatePrecision precision,
                               UUID actorId) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person create(@RequestBody CreatePersonRequest request) {
        return persons.create(request.gender(), request.birthDate(), request.birthDatePrecision(),
                request.biography(), request.visibility(), actors.resolve(request.actorId()));
    }

    @GetMapping
    public List<Person> list() {
        return persons.list();
    }

    @GetMapping("/{id}")
    public Person get(@PathVariable UUID id) {
        return persons.get(id);
    }

    @GetMapping("/{id}/display-name")
    public String displayName(@PathVariable UUID id) {
        return persons.displayName(id);
    }

    @PostMapping("/{id}/names")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonName addName(@PathVariable UUID id, @RequestBody AddNameRequest request) {
        boolean first = persons.names(id).isEmpty();
        boolean preferred = request.preferred() == null ? first : request.preferred();
        return persons.addName(id, request.nameType(), request.fullName(), request.givenNames(),
                request.familyName(), request.validFrom(), preferred, actors.resolve(request.actorId()));
    }

    @GetMapping("/{id}/names")
    public List<PersonName> names(@PathVariable UUID id) {
        return persons.names(id);
    }

    @PostMapping("/{id}/death")
    public Person recordDeath(@PathVariable UUID id, @RequestBody DeathRequest request) {
        return persons.recordDeath(id, request.deathDate(), request.precision(),
                actors.resolve(request.actorId()));
    }

    @GetMapping("/{id}/ancestry")
    public List<PersonService.AncestryEdge> ancestry(@PathVariable UUID id,
                                                     @RequestParam(defaultValue = "8") int generations) {
        return persons.ancestry(id, generations);
    }
}
