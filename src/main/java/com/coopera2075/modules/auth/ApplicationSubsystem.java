package com.coopera2075.modules.auth;

public enum ApplicationSubsystem {
    COOPERATION("SYSTEM_COOPERATION_ACCESS"),
    INFORMATION_MANAGEMENT("SYSTEM_INFORMATION_MANAGEMENT_ACCESS"),
    PERSONAL_ACCOUNT("SYSTEM_PERSONAL_ACCOUNT_ACCESS");

    private final String permissionCode;

    ApplicationSubsystem(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String permissionCode() {
        return permissionCode;
    }
}
