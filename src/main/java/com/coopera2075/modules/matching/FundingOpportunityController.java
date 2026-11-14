package com.coopera2075.modules.matching;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.modules.matching.dto.FundingOpportunityPageResponse;
import com.coopera2075.modules.matching.dto.FundingOpportunityResponse;

import lombok.RequiredArgsConstructor;

// Catálogo de convocatorias. Filtros opcionales y combinables:
//   q (texto en title/cooperatingEntity), impactArea, donorCountry, currency,
//   minAmount, maxAmount, closingBefore, closingAfter, page, size.
@RestController
@RequestMapping("/api/v1/funding-opportunities")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PERM_SYSTEM_COOPERATION_ACCESS')")
public class FundingOpportunityController {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final FundingOpportunityCatalogService catalogService;

    @GetMapping
    public ResponseEntity<FundingOpportunityPageResponse> getCatalog(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String impactArea,
            @RequestParam(required = false) String donorCountry,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) LocalDate closingBefore,
            @RequestParam(required = false) LocalDate closingAfter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La página debe ser no negativa y el tamaño debe estar entre 1 y " + MAX_PAGE_SIZE);
        }
        if (minAmount != null && minAmount.signum() < 0
                || maxAmount != null && maxAmount.signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Los montos no pueden ser negativos");
        }
        return ResponseEntity.ok(catalogService.getCatalog(
                q, impactArea, donorCountry, currency, minAmount, maxAmount,
                closingBefore, closingAfter, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FundingOpportunityResponse> getById(@PathVariable long id) {
        return ResponseEntity.ok(catalogService.getById(id));
    }
}
