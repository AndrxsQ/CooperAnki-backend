package com.coopera2075.modules.auth.dto;

import jakarta.validation.constraints.NotNull;

public record AccountPreferencesRequest(@NotNull Boolean emailNotificationsEnabled) {
}
