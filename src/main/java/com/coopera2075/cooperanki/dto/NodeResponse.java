package com.coopera2075.cooperanki.dto;

import com.coopera2075.cooperanki.entity.Node;

public record NodeResponse(
        Integer id,
        String name,
        Integer organizationId,
        String organizationName,
        Integer departmentId,
        String departmentName,
        Integer municipalityId,
        String municipalityName
) {
    public static NodeResponse from(Node entity) {
        return new NodeResponse(
                entity.getId(),
                entity.getName(),
                entity.getOrganization().getId(),
                entity.getOrganization().getName(),
                entity.getDepartment().getId(),
                entity.getDepartment().getName(),
                entity.getMunicipality().getId(),
                entity.getMunicipality().getName()
        );
    }
}
