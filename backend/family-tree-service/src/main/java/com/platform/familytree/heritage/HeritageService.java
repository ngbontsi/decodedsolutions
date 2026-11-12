package com.platform.familytree.heritage;

import com.platform.familytree.heritage.model.HistoricalEvent;
import com.platform.familytree.heritage.model.Location;
import com.platform.familytree.heritage.model.Organisation;
import com.platform.familytree.heritage.model.OrganisationMembership;
import com.platform.familytree.heritage.repository.HistoricalEventRepository;
import com.platform.familytree.heritage.repository.LocationRepository;
import com.platform.familytree.heritage.repository.OrganisationMembershipRepository;
import com.platform.familytree.heritage.repository.OrganisationRepository;
import com.platform.familytree.identity.service.ArchiveService;
import com.platform.familytree.identity.repository.PersonRepository;
import com.platform.familytree.shared.audit.AuditLog;
import com.platform.familytree.shared.audit.AuditWriter;
import com.platform.familytree.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class HeritageService {

    private final HistoricalEventRepository events;
    private final OrganisationRepository organisations;
    private final OrganisationMembershipRepository organisationMemberships;
    private final LocationRepository locations;
    private final PersonRepository persons;
    private final ArchiveService archives;
    private final AuditWriter audit;

    public HeritageService(HistoricalEventRepository events,
                           OrganisationRepository organisations,
                           OrganisationMembershipRepository organisationMemberships,
                           LocationRepository locations,
                           PersonRepository persons,
                           ArchiveService archives,
                           AuditWriter audit) {
        this.events = events;
        this.organisations = organisations;
        this.organisationMemberships = organisationMemberships;
        this.locations = locations;
        this.persons = persons;
        this.archives = archives;
        this.audit = audit;
    }

    @Transactional
    public Location createLocation(String name, Location.Type type, UUID parentLocationId) {
        return locations.save(Location.create(archives.defaultArchiveId(), name, type,
                parentLocationId));
    }

    @Transactional(readOnly = true)
    public List<Location> listLocations() {
        return locations.findByArchiveIdOrderByNameAsc(archives.defaultArchiveId());
    }

    @Transactional
    public HistoricalEvent createEvent(String title, String description, LocalDate eventDate,
                                       HistoricalEvent.Precision precision, UUID locationId,
                                       HistoricalEvent.Type type, UUID actorId) {
        if (locationId != null && !locations.existsById(locationId)) {
            throw DomainException.notFound("Location", locationId);
        }
        HistoricalEvent event = HistoricalEvent.create(archives.defaultArchiveId(), title,
                description, eventDate, precision, locationId, type, actorId);
        events.save(event);
        audit.record(event.getArchiveId(), actorId, "HistoricalEvent", event.getId(),
                AuditLog.Action.CREATE, null, "{\"title\":\"" + title + "\"}",
                "Historical event recorded");
        return event;
    }

    @Transactional(readOnly = true)
    public List<HistoricalEvent> listEvents() {
        return events.findByArchiveIdOrderByEventDateAsc(archives.defaultArchiveId());
    }

    @Transactional
    public Organisation createOrganisation(String name, Organisation.Type type,
                                           LocalDate foundedDate, String description,
                                           UUID actorId) {
        Organisation organisation = Organisation.create(archives.defaultArchiveId(), name,
                type, foundedDate, description);
        organisations.save(organisation);
        audit.record(organisation.getArchiveId(), actorId, "Organisation", organisation.getId(),
                AuditLog.Action.CREATE, null, "{\"name\":\"" + name + "\"}",
                "Organisation recorded");
        return organisation;
    }

    @Transactional(readOnly = true)
    public List<Organisation> listOrganisations() {
        return organisations.findByArchiveIdOrderByNameAsc(archives.defaultArchiveId());
    }

    @Transactional
    public OrganisationMembership addMembership(UUID personId, UUID organisationId,
                                                String role, LocalDate startDate, UUID actorId) {
        persons.findById(personId)
                .orElseThrow(() -> DomainException.notFound("Person", personId));
        if (!organisations.existsById(organisationId)) {
            throw DomainException.notFound("Organisation", organisationId);
        }
        OrganisationMembership membership = OrganisationMembership.of(personId, organisationId,
                role, startDate, actorId);
        organisationMemberships.save(membership);
        return membership;
    }

    @Transactional(readOnly = true)
    public List<OrganisationMembership> membershipsOf(UUID personId) {
        return organisationMemberships.findByPersonIdOrderByStartDateAsc(personId);
    }
}
