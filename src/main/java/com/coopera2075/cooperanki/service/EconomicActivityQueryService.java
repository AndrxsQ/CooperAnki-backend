package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.cooperanki.dto.EconomicActivityResponse;
import com.coopera2075.cooperanki.dto.EconomicUnitResponse;
import com.coopera2075.cooperanki.repository.EconomicActivityRepository;
import com.coopera2075.cooperanki.repository.EconomicUnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EconomicActivityQueryService {
    private final EconomicActivityRepository economicActivityRepository;
    private final EconomicUnitRepository economicUnitRepository;

    @Transactional(readOnly = true)
    public List<EconomicActivityResponse> findActivitiesByEconomicUnitId(Integer unitId) {
        if (!economicUnitRepository.existsById(unitId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Unidad económica no encontrada con id: " + unitId);
        }

        return economicActivityRepository.findAllByEconomicUnitId(unitId).stream()
                .map(EconomicActivityResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EconomicUnitResponse> findUnitsByActivityCiiuId(String ciiuId) {
        if (!economicActivityRepository.existsById(ciiuId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Actividad económica no encontrada con código CIIU: " + ciiuId);
        }

        return economicActivityRepository.findAllUnitsByActivityCiiuId(ciiuId).stream()
                .map(EconomicUnitResponse::from)
                .toList();
    }
}
