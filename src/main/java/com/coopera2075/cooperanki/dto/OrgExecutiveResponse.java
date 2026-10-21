package com.coopera2075.cooperanki.dto;

import com.coopera2075.cooperanki.entity.OrgExecutive;

public record OrgExecutiveResponse(
        Integer id,
        String name,
        String docNumber,
        String role,
        Integer organizationId,
        String organizationName
) {
    public static OrgExecutiveResponse from(OrgExecutive entity) {
        return new OrgExecutiveResponse(
                entity.getId(),
                entity.getName(),
                entity.getDocNumber(),
                entity.getRole(),
                entity.getOrganization() == null ? null : entity.getOrganization().getId(),
                entity.getOrganization() == null ? null : entity.getOrganization().getName()
        );
    }
}
