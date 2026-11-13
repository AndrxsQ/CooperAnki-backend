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
        Instant calculatedAt,
        String donorCountry,
        String cooperationType,
        String organizationType,
        String projectType,
        String targetPopulations,
        String applicationMethod,
        String language,
        String currency,
        BigDecimal amountInCop,
        String sourcePage,
        String sourceDocument,
        String opportunityStatus,
        String opportunityStatusDetail,
        String contactPerson,
        String contactPhone,
        String contactFax,
        String contactAddress,
        String contactCity,
        String contactEmail,
        String localContactPerson,
        String localContactPhone,
        String localContactAddress,
        String localContactCity,
        String localContactEmail,
        String notes) {
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
                match.getCalculatedAt(),
                opportunity.getDonorCountry(),
                opportunity.getCooperationType(),
                opportunity.getOrganizationType(),
                opportunity.getProjectType(),
                opportunity.getTargetPopulations(),
                opportunity.getApplicationMethod(),
                opportunity.getLanguage(),
                opportunity.getCurrency(),
                opportunity.getAmountInCop(),
                opportunity.getSourcePage(),
                opportunity.getSourceDocument(),
                opportunity.getOpportunityStatus(),
                opportunity.getOpportunityStatusDetail(),
                opportunity.getContactPerson(),
                opportunity.getContactPhone(),
                opportunity.getContactFax(),
                opportunity.getContactAddress(),
                opportunity.getContactCity(),
                opportunity.getContactEmail(),
                opportunity.getLocalContactPerson(),
                opportunity.getLocalContactPhone(),
                opportunity.getLocalContactAddress(),
                opportunity.getLocalContactCity(),
                opportunity.getLocalContactEmail(),
                opportunity.getNotes());
    }
}
