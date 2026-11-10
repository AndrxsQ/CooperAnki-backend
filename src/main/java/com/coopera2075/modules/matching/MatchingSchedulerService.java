package com.coopera2075.modules.matching;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.coopera2075.modules.matching.dto.EmbeddingWorkerRequest;
import com.coopera2075.modules.matching.dto.EmbeddingWorkerResponse;
import com.coopera2075.modules.projects.Project;
import com.coopera2075.modules.projects.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchingSchedulerService {
    private static final int EMBEDDING_BATCH_SIZE = 16;
    private static final int STORED_EMBEDDING_DIMENSIONS = 1536;

    private final ProjectRepository projectRepository;
    private final FundingOpportunityRepository fundingOpportunityRepository;
    private final ProjectMatchRepository projectMatchRepository;
    private final MatchingWorkerClient matchingWorkerClient;

    public CycleResult processCycle(int maxRecommendationsPerProject) {
        if (maxRecommendationsPerProject < 1) {
            throw new IllegalArgumentException("maxRecommendationsPerProject debe ser mayor que cero");
        }
        int projectsEmbedded = generateProjectEmbeddings();
        int opportunitiesEmbedded = generateOpportunityEmbeddings();
        int matchesUpdated = projectMatchRepository.refreshSuggestedMatches(maxRecommendationsPerProject);
        return new CycleResult(projectsEmbedded, opportunitiesEmbedded, matchesUpdated);
    }

    private int generateProjectEmbeddings() {
        int generated = 0;
        Page<Project> page;
        do {
            page = projectRepository.findAllByEmbeddingVectorIsNull(PageRequest.of(0, EMBEDDING_BATCH_SIZE));
            if (!page.isEmpty()) {
                List<EmbeddingWorkerRequest> requests = page.getContent().stream()
                        .map(project -> new EmbeddingWorkerRequest(
                                "project",
                                project.getId(),
                                project.getTitle(),
                                project.getExecutiveSummary(),
                                null,
                                null,
                                null))
                        .toList();
                List<EmbeddingWorkerResponse> responses = matchingWorkerClient.generateEmbeddings(requests);
                Map<Long, Project> projectsById = new HashMap<>();
                page.forEach(project -> projectsById.put(project.getId(), project));
                Set<Long> processedIds = new HashSet<>();
                for (EmbeddingWorkerResponse response : responses) {
                    validateResponse(response, "project", projectsById, processedIds);
                    projectsById.get(response.entityId()).setEmbeddingVector(response.embedding());
                }
                if (processedIds.size() != projectsById.size()) {
                    throw new IllegalStateException("El worker devolvió un lote incompleto de proyectos");
                }
                projectRepository.saveAll(page.getContent());
                generated += page.getNumberOfElements();
            }
        } while (!page.isEmpty());
        return generated;
    }

    private int generateOpportunityEmbeddings() {
        int generated = 0;
        Page<FundingOpportunity> page;
        do {
            page = fundingOpportunityRepository.findAllByEmbeddingVectorIsNull(
                    PageRequest.of(0, EMBEDDING_BATCH_SIZE));
            if (!page.isEmpty()) {
                List<EmbeddingWorkerRequest> requests = page.getContent().stream()
                        .map(opportunity -> new EmbeddingWorkerRequest(
                                "funding_opportunity",
                                opportunity.getId(),
                                opportunity.getTitle(),
                                null,
                                opportunity.getCooperatingEntity(),
                                opportunity.getImpactArea(),
                                opportunity.getRequirementsDescription()))
                        .toList();
                List<EmbeddingWorkerResponse> responses = matchingWorkerClient.generateEmbeddings(requests);
                Map<Long, FundingOpportunity> opportunitiesById = new HashMap<>();
                page.forEach(opportunity -> opportunitiesById.put(opportunity.getId(), opportunity));
                Set<Long> processedIds = new HashSet<>();
                for (EmbeddingWorkerResponse response : responses) {
                    validateResponse(response, "funding_opportunity", opportunitiesById, processedIds);
                    opportunitiesById.get(response.entityId()).setEmbeddingVector(response.embedding());
                }
                if (processedIds.size() != opportunitiesById.size()) {
                    throw new IllegalStateException("El worker devolvió un lote incompleto de convocatorias");
                }
                fundingOpportunityRepository.saveAll(page.getContent());
                generated += page.getNumberOfElements();
            }
        } while (!page.isEmpty());
        return generated;
    }

    private <T> void validateResponse(
            EmbeddingWorkerResponse response,
            String expectedType,
            Map<Long, T> entitiesById,
            Set<Long> processedIds) {
        if (!expectedType.equals(response.entityType()) || !entitiesById.containsKey(response.entityId())) {
            throw new IllegalStateException("El worker devolvió un embedding para un registro inesperado");
        }
        if (!processedIds.add(response.entityId())) {
            throw new IllegalStateException("El worker devolvió embeddings duplicados para un registro");
        }
        float[] embedding = response.embedding();
        if (response.dimensions() != STORED_EMBEDDING_DIMENSIONS
                || embedding == null
                || embedding.length != STORED_EMBEDDING_DIMENSIONS) {
            throw new IllegalStateException("El worker devolvió un embedding con dimensiones inválidas");
        }
        for (float value : embedding) {
            if (!Float.isFinite(value)) {
                throw new IllegalStateException("El worker devolvió un embedding con valores no finitos");
            }
        }
    }

    public record CycleResult(int projectsEmbedded, int opportunitiesEmbedded, int matchesUpdated) {
    }
}
