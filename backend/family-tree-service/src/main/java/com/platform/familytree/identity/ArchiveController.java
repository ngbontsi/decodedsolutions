package com.platform.familytree.identity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/archive")
public class ArchiveController {

    private final ArchiveService archives;

    public ArchiveController(ArchiveService archives) {
        this.archives = archives;
    }

    @GetMapping
    public Archive current() {
        return archives.get(archives.defaultArchiveId());
    }
}
