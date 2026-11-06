package com.coopera2075.modules.matching;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.modules.matching.dto.ProjectMatchPageResponse;
import com.coopera2075.modules.matching.dto.ProjectMatchResponse;
import com.coopera2075.modules.projects.ProjectRepository;

import jakarta.persistence.criteria.Predicate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final ProjectRepository projectRepository;
    private final ProjectMatchRepository projectMatchRepository;

    @Transactional(readOnly = true)
    public ProjectMatchPageResponse getRecommendations(
            long projectId,
            String impactArea,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate closingBefore,
            int page,
            int size) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El proyecto no existe");
        }
        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "minAmount no puede ser mayor que maxAmount");
        }

        Page<ProjectMatchResponse> matches = projectMatchRepository.findAll(
                        buildSpecification(projectId, impactArea, minAmount, maxAmount, closingBefore),
                        PageRequest.of(page, size, Sort.by(
                                Sort.Order.desc("compatibilityScore"),
                                Sort.Order.desc("calculatedAt"))))
                .map(ProjectMatchResponse::from);
        return ProjectMatchPageResponse.from(matches);
    }

    private Specification<ProjectMatch> buildSpecification(
            long projectId,
            String impactArea,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate closingBefore) {
        return (root, query, builder) -> {
            var opportunity = root.join("fundingOpportunity");
            var predicates = new ArrayList<Predicate>();
            predicates.add(builder.equal(root.get("project").get("id"), projectId));

            if (StringUtils.hasText(impactArea)) {
                predicates.add(builder.equal(
                        builder.lower(opportunity.get("impactArea")),
                        impactArea.trim().toLowerCase(java.util.Locale.ROOT)));
            }
            if (minAmount != null) {
                predicates.add(builder.greaterThanOrEqualTo(opportunity.get("maxAmount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(builder.lessThanOrEqualTo(opportunity.get("maxAmount"), maxAmount));
            }
            if (closingBefore != null) {
                predicates.add(builder.lessThanOrEqualTo(opportunity.get("closingDate"), closingBefore));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
