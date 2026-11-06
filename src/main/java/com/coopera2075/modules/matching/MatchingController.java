package com.coopera2075.modules.matching;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.modules.matching.dto.ProjectMatchPageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/recommendations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') and hasAuthority('PERM_SYSTEM_COOPERA_ACCESS')")
public class MatchingController {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final MatchingService matchingService;

    @GetMapping
    public ResponseEntity<ProjectMatchPageResponse> getRecommendations(
            @PathVariable long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La página debe ser no negativa y el tamaño debe estar entre 1 y " + MAX_PAGE_SIZE);
        }
        return ResponseEntity.ok(matchingService.getRecommendations(projectId, page, size));
    }
}
