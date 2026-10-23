package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.cooperanki.dto.OrganizationResponse;
import com.coopera2075.cooperanki.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationQueryService {
    private final OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll() {
        return organizationRepository.findAll().stream()
                .map(OrganizationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrganizationResponse findById(Integer id) {
        return organizationRepository.findById(id)
                .map(OrganizationResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Organización no encontrada con id: " + id));
    }
}
