package com.coopera2075.modules.matching.dto;

public record EmbeddingWorkerRequest(
        String entityType,
        Long id,
        String title,
        String executiveSummary,
        String cooperatingEntity,
        String impactArea,
        String requirementsDescription) {
}
