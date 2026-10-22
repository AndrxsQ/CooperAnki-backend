package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coopera2075.cooperanki.dto.DepartmentResponse;
import com.coopera2075.cooperanki.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentQueryService {
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAllOrderByName().stream()
                .map(DepartmentResponse::from)
                .toList();
    }
}
