package com.coopera2075.cooperanki.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.OrgExecutiveResponse;
import com.coopera2075.cooperanki.service.OrgExecutiveQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/org-executives")
@RequiredArgsConstructor
public class OrgExecutiveController {
    private final OrgExecutiveQueryService orgExecutiveQueryService;

    @GetMapping
    public ResponseEntity<List<OrgExecutiveResponse>> findAll() {
        return ResponseEntity.ok(orgExecutiveQueryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrgExecutiveResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(orgExecutiveQueryService.findById(id));
    }
}
