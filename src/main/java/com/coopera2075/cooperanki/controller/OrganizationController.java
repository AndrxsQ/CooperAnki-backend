package com.coopera2075.cooperanki.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.OrganizationResponse;
import com.coopera2075.cooperanki.service.OrganizationQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    private final OrganizationQueryService organizationQueryService;

    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> findAll() {
        return ResponseEntity.ok(organizationQueryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(organizationQueryService.findById(id));
    }
}
