package com.coopera2075.modules.projects.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.coopera2075.modules.projects.Project;
import com.coopera2075.modules.projects.ProjectStatus;

public record ProjectResponse(
        Long id,
        Long organizationId,
        String organizationName,
        Long creatorId,
        String creatorName,
        String identifierCode,
        String title,
        String executiveSummary,
        BigDecimal requestedBudget,
        ProjectStatus status,
        Instant startsAt,
        Instant closesAt,
        Instant createdAt,
        Instant updatedAt,
        List<ProjectAttachmentResponse> attachments) {
    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getOrganization().getId(),
                project.getOrganization().getName(),
                project.getCreator().getId(),
                project.getCreator().getFullName(),
                project.getIdentifierCode(),
                project.getTitle(),
                project.getExecutiveSummary(),
                project.getRequestedBudget(),
                project.getStatus(),
                project.getStartsAt(),
                project.getClosesAt(),
                project.getCreatedAt(),
                project.getUpdatedAt(),
                project.getAttachments().stream()
                        .map(attachment -> new ProjectAttachmentResponse(
                                attachment.getId(),
                                attachment.getFileName(),
                                attachment.getS3Url(),
                                attachment.getMimeType(),
                                attachment.getSizeBytes()))
                        .toList());
    }
}
