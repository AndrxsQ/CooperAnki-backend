package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.cooperanki.dto.NodeResponse;
import com.coopera2075.cooperanki.repository.NodeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NodeQueryService {
    private final NodeRepository nodeRepository;

    @Transactional(readOnly = true)
    public List<NodeResponse> findAll() {
        return nodeRepository.findAll().stream()
                .map(NodeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public NodeResponse findById(Integer id) {
        return nodeRepository.findById(id)
                .map(NodeResponse::from)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Nodo no encontrado con id: " + id));
    }
}
