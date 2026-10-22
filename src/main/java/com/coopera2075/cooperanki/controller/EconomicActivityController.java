package com.coopera2075.cooperanki.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.EconomicUnitResponse;
import com.coopera2075.cooperanki.dto.EconomicActivityResponse;
import com.coopera2075.cooperanki.service.EconomicActivityQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/economic-activities")
@RequiredArgsConstructor
public class EconomicActivityController {
    private final EconomicActivityQueryService economicActivityQueryService;

    @GetMapping
    public ResponseEntity<List<EconomicActivityResponse>> findAll() {
        return ResponseEntity.ok(economicActivityQueryService.findAll());
    }

    @GetMapping("/{ciiuId}/units")
    public ResponseEntity<List<EconomicUnitResponse>> findUnitsByActivity(
            @PathVariable String ciiuId) {
        return ResponseEntity.ok(economicActivityQueryService.findUnitsByActivityCiiuId(ciiuId));
    }
}
