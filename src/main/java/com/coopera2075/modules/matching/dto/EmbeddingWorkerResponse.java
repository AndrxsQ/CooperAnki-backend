package com.coopera2075.modules.matching.dto;

public record EmbeddingWorkerResponse(
        String entityType,
        long entityId,
        String model,
        int modelDimensions,
        int dimensions,
        float[] embedding) {
}
