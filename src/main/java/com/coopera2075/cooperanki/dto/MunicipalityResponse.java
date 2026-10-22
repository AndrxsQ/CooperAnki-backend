package com.coopera2075.cooperanki.dto;

import com.coopera2075.cooperanki.entity.Municipality;

public record MunicipalityResponse(Integer id, String name, Integer departmentId) {
    public static MunicipalityResponse from(Municipality entity) {
        return new MunicipalityResponse(
                entity.getId(),
                entity.getName(),
                entity.getDepartment().getId());
    }
}
