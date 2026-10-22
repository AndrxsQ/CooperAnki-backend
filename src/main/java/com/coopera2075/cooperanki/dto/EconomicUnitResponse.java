package com.coopera2075.cooperanki.dto;

import com.coopera2075.cooperanki.entity.EconomicUnit;

public record EconomicUnitResponse(
        Integer id,
        String docNumber,
        String name,
        Integer age,
        Integer departmentId,
        String departmentName,
        Integer municipalityId,
        String municipalityName,
        Integer nodeId,
        String nodeName,
        String address,
        Integer telNumber,
        String email,
        Integer organizationId,
        String organizationName
) {
    public static EconomicUnitResponse from(EconomicUnit entity) {
        return new EconomicUnitResponse(
                entity.getId(),
                entity.getDocNumber(),
                entity.getName(),
                entity.getAge(),
                entity.getDepartment().getId(),
                entity.getDepartment().getName(),
                entity.getMunicipality().getId(),
                entity.getMunicipality().getName(),
                entity.getNode().getId(),
                entity.getNode().getName(),
                entity.getAddress(),
                entity.getTelNumber(),
                entity.getEmail(),
                entity.getOrganization() == null ? null : entity.getOrganization().getId(),
                entity.getOrganization() == null ? null : entity.getOrganization().getName()
        );
    }
}
