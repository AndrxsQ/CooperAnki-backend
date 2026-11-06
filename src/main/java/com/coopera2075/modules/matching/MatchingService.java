package com.coopera2075.modules.matching;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.modules.matching.dto.ProjectMatchPageResponse;
import com.coopera2075.modules.matching.dto.ProjectMatchResponse;
import com.coopera2075.modules.projects.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchingService {
    private final ProjectRepository projectRepository;
    private final ProjectMatchRepository projectMatchRepository;

    @Transactional(readOnly = true)
    public ProjectMatchPageResponse getRecommendations(long projectId, int page, int size) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El proyecto no existe");
        }

        Page<ProjectMatchResponse> matches = projectMatchRepository.findByProject_Id(
                        projectId,
                        PageRequest.of(page, size, Sort.by(
                                Sort.Order.desc("compatibilityScore"),
                                Sort.Order.desc("calculatedAt"))))
                .map(ProjectMatchResponse::from);
        return ProjectMatchPageResponse.from(matches);
    }
}
