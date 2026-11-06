package com.coopera2075.modules.projects.dto;

public record ProjectAttachmentResponse(
        Long id,
        String fileName,
        String s3Url,
        String mimeType,
        Long sizeBytes) {
}
