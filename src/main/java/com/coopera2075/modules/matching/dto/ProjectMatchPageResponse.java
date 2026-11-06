package com.coopera2075.modules.matching.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record ProjectMatchPageResponse(
        List<ProjectMatchResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {
    public static ProjectMatchPageResponse from(Page<ProjectMatchResponse> page) {
        return new ProjectMatchPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
