package com.coopera2075.cooperanki.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.DepartmentResponse;
import com.coopera2075.cooperanki.service.DepartmentQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentQueryService departmentQueryService;

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> findAll() {
        return ResponseEntity.ok(departmentQueryService.findAll());
    }
}
