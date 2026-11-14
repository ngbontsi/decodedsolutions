package com.platform.familytree.shared.web;

import com.platform.familytree.community.service.ContributorService;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Every write needs an actor for the audit trail. Until authentication is wired in,
 * callers either pass actorId explicitly or fall back to the seeded system contributor.
 */
@Component
public class ActorResolver {

    private final ContributorService contributors;

    public ActorResolver(ContributorService contributors) {
        this.contributors = contributors;
    }

    public UUID resolve(UUID requested) {
        if (requested == null) {
            return contributors.systemContributorId();
        }
        contributors.require(requested);
        return requested;
    }
}
