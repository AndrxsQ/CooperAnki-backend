package com.coopera2075.modules.projects.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record ProjectPageResponse(
        List<ProjectResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {
    public static ProjectPageResponse from(Page<ProjectResponse> page) {
        return new ProjectPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
