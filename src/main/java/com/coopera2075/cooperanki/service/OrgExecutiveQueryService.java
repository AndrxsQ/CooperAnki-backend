package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.cooperanki.dto.OrgExecutiveResponse;
import com.coopera2075.cooperanki.repository.OrgExecutiveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrgExecutiveQueryService {
    private final OrgExecutiveRepository orgExecutiveRepository;

    @Transactional(readOnly = true)
    public List<OrgExecutiveResponse> findAll() {
        return orgExecutiveRepository.findAll().stream()
                .map(OrgExecutiveResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrgExecutiveResponse findById(Integer id) {
        return orgExecutiveRepository.findById(id)
                .map(OrgExecutiveResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Directivo no encontrado con id: " + id));
    }
}
