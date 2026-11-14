package com.coopera2075.modules.matching.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record FundingOpportunityPageResponse(
        List<FundingOpportunityResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {
    public static FundingOpportunityPageResponse from(Page<FundingOpportunityResponse> page) {
        return new FundingOpportunityPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
