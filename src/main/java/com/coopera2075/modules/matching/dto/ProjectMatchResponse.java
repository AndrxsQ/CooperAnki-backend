package com.coopera2075.modules.matching.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.coopera2075.modules.matching.FundingOpportunity;
import com.coopera2075.modules.matching.ProjectMatch;
import com.coopera2075.modules.matching.MatchStatus;

public record ProjectMatchResponse(
        Long matchId,
        Long fundingOpportunityId,
        String title,
        String sourceUrl,
        String cooperatingEntity,
        String impactArea,
        String requirementsDescription,
        BigDecimal maxAmount,
        LocalDate closingDate,
        BigDecimal compatibilityScore,
        MatchStatus status,
        Instant calculatedAt) {
    public static ProjectMatchResponse from(ProjectMatch match) {
        FundingOpportunity opportunity = match.getFundingOpportunity();
        return new ProjectMatchResponse(
                match.getId(),
                opportunity.getId(),
                opportunity.getTitle(),
                opportunity.getSourceUrl(),
                opportunity.getCooperatingEntity(),
                opportunity.getImpactArea(),
                opportunity.getRequirementsDescription(),
                opportunity.getMaxAmount(),
                opportunity.getClosingDate(),
                match.getCompatibilityScore(),
                match.getStatus(),
                match.getCalculatedAt());
    }
}
