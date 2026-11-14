package com.coopera2075.modules.matching;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.modules.matching.dto.FundingOpportunityPageResponse;
import com.coopera2075.modules.matching.dto.FundingOpportunityResponse;

import jakarta.persistence.criteria.Predicate;

import lombok.RequiredArgsConstructor;

// Catálogo público de convocatorias: lista/consulta funding_opportunities sin depender de un proyecto.
@Service
@RequiredArgsConstructor
public class FundingOpportunityCatalogService {
    private final FundingOpportunityRepository fundingOpportunityRepository;

    @Transactional(readOnly = true)
    public FundingOpportunityPageResponse getCatalog(
            String q,
            String impactArea,
            String donorCountry,
            String currency,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate closingBefore,
            LocalDate closingAfter,
            int page,
            int size) {
        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "minAmount no puede ser mayor que maxAmount");
        }

        Page<FundingOpportunityResponse> catalog = fundingOpportunityRepository.findAll(
                        buildSpecification(q, impactArea, donorCountry, currency, minAmount, maxAmount,
                                closingBefore, closingAfter),
                        PageRequest.of(page, size, Sort.by(
                                Sort.Order.asc("closingDate").nullsLast(),
                                Sort.Order.asc("title"))))
                .map(FundingOpportunityResponse::from);
        return FundingOpportunityPageResponse.from(catalog);
    }

    @Transactional(readOnly = true)
    public FundingOpportunityResponse getById(long id) {
        return fundingOpportunityRepository.findById(id)
                .map(FundingOpportunityResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "La convocatoria no existe"));
    }

    // Filtros opcionales y combinables. Solo se agrega el predicado si el parámetro llega con valor.
    private Specification<FundingOpportunity> buildSpecification(
            String q,
            String impactArea,
            String donorCountry,
            String currency,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate closingBefore,
            LocalDate closingAfter) {
        return (root, query, builder) -> {
            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(q)) {
                String pattern = "%" + q.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("title")), pattern),
                        builder.like(builder.lower(root.get("cooperatingEntity")), pattern)));
            }
            if (StringUtils.hasText(impactArea)) {
                predicates.add(builder.equal(
                        builder.lower(root.get("impactArea")),
                        impactArea.trim().toLowerCase(Locale.ROOT)));
            }
            if (StringUtils.hasText(donorCountry)) {
                predicates.add(builder.equal(
                        builder.lower(root.get("donorCountry")),
                        donorCountry.trim().toLowerCase(Locale.ROOT)));
            }
            if (StringUtils.hasText(currency)) {
                predicates.add(builder.equal(root.get("currency"), currency.trim()));
            }
            if (minAmount != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("maxAmount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("maxAmount"), maxAmount));
            }
            if (closingBefore != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("closingDate"), closingBefore));
            }
            if (closingAfter != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("closingDate"), closingAfter));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
