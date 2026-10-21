package com.coopera2075.cooperanki.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.NodeResponse;
import com.coopera2075.cooperanki.service.NodeQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/nodes")
@RequiredArgsConstructor
public class NodeController {
    private final NodeQueryService nodeQueryService;

    @GetMapping
    public ResponseEntity<List<NodeResponse>> findAll() {
        return ResponseEntity.ok(nodeQueryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NodeResponse> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(nodeQueryService.findById(id));
    }
}
