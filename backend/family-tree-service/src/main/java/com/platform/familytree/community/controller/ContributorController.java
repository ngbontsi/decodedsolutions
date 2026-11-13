package com.platform.familytree.community.controller;

import com.platform.familytree.community.dto.CreateContributorRequest;
import com.platform.familytree.community.model.Contributor;
import com.platform.familytree.community.service.ContributorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contributors")
public class ContributorController {

    private final ContributorService contributors;

    public ContributorController(ContributorService contributors) {
        this.contributors = contributors;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contributor create(@RequestBody CreateContributorRequest request) {
        return contributors.create(request.displayName(), request.email());
    }

    @GetMapping
    public List<Contributor> list() {
        return contributors.list();
    }
}
