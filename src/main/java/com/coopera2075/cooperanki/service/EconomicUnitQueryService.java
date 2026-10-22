package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.cooperanki.dto.EconomicUnitResponse;
import com.coopera2075.cooperanki.repository.EconomicUnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EconomicUnitQueryService {
    private final EconomicUnitRepository economicUnitRepository;

    @Transactional(readOnly = true)
    public List<EconomicUnitResponse> findAll() {
        return economicUnitRepository.findAllWithActivities().stream()
                .map(EconomicUnitResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EconomicUnitResponse findById(Integer id) {
        return economicUnitRepository.findByIdWithActivities(id)
                .map(EconomicUnitResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Unidad económica no encontrada con id: " + id));
    }
}
