package com.coopera2075.cooperanki.dto;

import com.coopera2075.cooperanki.entity.Department;

public record DepartmentResponse(Integer id, String name) {
    public static DepartmentResponse from(Department entity) {
        return new DepartmentResponse(entity.getId(), entity.getName());
    }
}
