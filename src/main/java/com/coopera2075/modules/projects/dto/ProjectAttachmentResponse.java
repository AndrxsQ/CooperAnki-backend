package com.coopera2075.modules.projects.dto;

public record ProjectAttachmentResponse(
        Long id,
        String fileName,
        String mimeType,
        Long sizeBytes,
        String downloadUrl) {
}
