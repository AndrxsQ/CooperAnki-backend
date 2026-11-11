package com.coopera2075.modules.auth;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public enum ApplicationRole {
    ADMIN(
            ApplicationSubsystem.COOPERATION,
            EnumSet.of(
                    ApplicationSubsystem.COOPERATION,
                    ApplicationSubsystem.INFORMATION_MANAGEMENT,
                    ApplicationSubsystem.PERSONAL_ACCOUNT)),
    GENERAL_EXE(
            ApplicationSubsystem.COOPERATION,
            EnumSet.of(
                    ApplicationSubsystem.COOPERATION,
                    ApplicationSubsystem.INFORMATION_MANAGEMENT)),
    LEGAL_EXE(
            ApplicationSubsystem.INFORMATION_MANAGEMENT,
            EnumSet.of(ApplicationSubsystem.INFORMATION_MANAGEMENT)),
    TERRITORIAL_EXE(
            ApplicationSubsystem.INFORMATION_MANAGEMENT,
            EnumSet.of(ApplicationSubsystem.INFORMATION_MANAGEMENT)),
    CLIENT(
            ApplicationSubsystem.PERSONAL_ACCOUNT,
            EnumSet.of(ApplicationSubsystem.PERSONAL_ACCOUNT));

    private final ApplicationSubsystem defaultSubsystem;
    private final Set<ApplicationSubsystem> allowedSubsystems;

    ApplicationRole(ApplicationSubsystem defaultSubsystem, Set<ApplicationSubsystem> allowedSubsystems) {
        if (!allowedSubsystems.contains(defaultSubsystem)) {
            throw new IllegalArgumentException("El subsistema predeterminado debe estar permitido para el rol");
        }
        this.defaultSubsystem = defaultSubsystem;
        this.allowedSubsystems = Set.copyOf(allowedSubsystems);
    }

    public ApplicationSubsystem defaultSubsystem() {
        return defaultSubsystem;
    }

    public Set<ApplicationSubsystem> allowedSubsystems() {
        return allowedSubsystems;
    }

    public boolean allows(ApplicationSubsystem subsystem) {
        return allowedSubsystems.contains(subsystem);
    }

    public Set<String> permissionCodes() {
        return allowedSubsystems.stream()
                .map(ApplicationSubsystem::permissionCode)
                .collect(Collectors.toUnmodifiableSet());
    }

    public List<String> allowedSystemNames() {
        return Arrays.stream(ApplicationSubsystem.values())
                .filter(allowedSubsystems::contains)
                .map(Enum::name)
                .toList();
    }

    public static Optional<ApplicationRole> fromName(String name) {
        return Arrays.stream(values())
                .filter(role -> role.name().equals(name))
                .findFirst();
    }
}
