package com.coopera2075.modules.projects.dto;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
        @NotNull Long organizationId,
        @NotBlank @Size(max = 50) String identifierCode,
        @NotBlank @Size(max = 255) String title,
        String executiveSummary,
        @DecimalMin(value = "0.0", inclusive = false) BigDecimal requestedBudget,
        Instant startsAt,
        Instant closesAt) {
}
