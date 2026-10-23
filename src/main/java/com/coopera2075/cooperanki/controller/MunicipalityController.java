package com.coopera2075.cooperanki.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.MunicipalityResponse;
import com.coopera2075.cooperanki.service.MunicipalityQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/municipalities")
@RequiredArgsConstructor
public class MunicipalityController {
    private final MunicipalityQueryService municipalityQueryService;

    @GetMapping
    public ResponseEntity<List<MunicipalityResponse>> findAll() {
        return ResponseEntity.ok(municipalityQueryService.findAll());
    }
}
