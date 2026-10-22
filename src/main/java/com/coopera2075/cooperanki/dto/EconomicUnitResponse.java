package com.coopera2075.cooperanki.dto;

import java.util.List;

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
        List<EconomicActivityResponse> activities,
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
                activitiesFrom(entity),
                entity.getAddress(),
                entity.getTelNumber(),
                entity.getEmail(),
                entity.getOrganization() == null ? null : entity.getOrganization().getId(),
                entity.getOrganization() == null ? null : entity.getOrganization().getName()
        );
    }

    private static List<EconomicActivityResponse> activitiesFrom(EconomicUnit entity) {
        if (entity.getActivities() == null) {
            return List.of();
        }
        return entity.getActivities().stream()
                .map(EconomicActivityResponse::from)
                .toList();
    }
}
