package com.coopera2075.cooperanki.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.EconomicActivityResponse;
import com.coopera2075.cooperanki.dto.EconomicUnitResponse;
import com.coopera2075.cooperanki.service.EconomicActivityQueryService;
import com.coopera2075.cooperanki.service.EconomicUnitQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/economic-units")
@RequiredArgsConstructor
public class EconomicUnitController {
    private final EconomicUnitQueryService economicUnitQueryService;
    private final EconomicActivityQueryService economicActivityQueryService;

    @GetMapping
    public ResponseEntity<List<EconomicUnitResponse>> findAll() {
        return ResponseEntity.ok(economicUnitQueryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EconomicUnitResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(economicUnitQueryService.findById(id));
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<EconomicActivityResponse>> findActivitiesByUnitId(@PathVariable Integer id) {
        return ResponseEntity.ok(economicActivityQueryService.findActivitiesByEconomicUnitId(id));
    }
}
