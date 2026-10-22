package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coopera2075.cooperanki.dto.MunicipalityResponse;
import com.coopera2075.cooperanki.repository.MunicipalityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MunicipalityQueryService {
    private final MunicipalityRepository municipalityRepository;

    @Transactional(readOnly = true)
    public List<MunicipalityResponse> findAll() {
        return municipalityRepository.findAllWithDepartmentOrderByName().stream()
                .map(MunicipalityResponse::from)
                .toList();
    }
}
