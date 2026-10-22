package com.coopera2075.cooperanki.dto;

import com.coopera2075.cooperanki.entity.Organization;

public record OrganizationResponse(
        Integer id,
        String name,
        Integer departmentId,
        String departmentName,
        Integer municipalityId,
        String municipalityName
) {
    public static OrganizationResponse from(Organization entity) {
        return new OrganizationResponse(
                entity.getId(),
                entity.getName(),
                entity.getDepartment().getId(),
                entity.getDepartment().getName(),
                entity.getMunicipality().getId(),
                entity.getMunicipality().getName()
        );
    }
}
