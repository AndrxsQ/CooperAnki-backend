package com.coopera2075.modules.projects.dto;

import com.coopera2075.modules.projects.ProjectStatus;

import jakarta.validation.constraints.NotNull;

public record ProjectStatusRequest(@NotNull ProjectStatus status) {
}
