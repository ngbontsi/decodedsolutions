package com.platform.familytree.community;

import com.platform.familytree.identity.ArchiveService;
import com.platform.familytree.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ContributorService {

    private final ContributorRepository contributors;
    private final ArchiveService archives;

    public ContributorService(ContributorRepository contributors, ArchiveService archives) {
        this.contributors = contributors;
        this.archives = archives;
    }

    @Transactional
    public Contributor create(String displayName, String email) {
        if (contributors.existsByEmailIgnoreCase(email)) {
            throw DomainException.conflict("A contributor with that email already exists");
        }
        return contributors.save(Contributor.create(archives.defaultArchiveId(), displayName, email));
    }

    @Transactional(readOnly = true)
    public List<Contributor> list() {
        return contributors.findAll();
    }

    @Transactional(readOnly = true)
    public UUID systemContributorId() {
        return contributors.findByEmailIgnoreCase("system@decoded.local")
                .map(Contributor::getId)
                .orElseThrow(() -> new DomainException(
                        com.platform.familytree.shared.error.ErrorCode.NOT_FOUND,
                        "System contributor missing. Run the V1 migration."));
    }

    @Transactional(readOnly = true)
    public void require(UUID id) {
        if (id != null && !contributors.existsById(id)) {
            throw DomainException.notFound("Contributor", id);
        }
    }
}
