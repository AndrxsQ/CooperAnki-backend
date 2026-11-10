package com.coopera2075.modules.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.coopera2075.modules.matching.dto.EmbeddingWorkerResponse;
import com.coopera2075.modules.projects.Project;
import com.coopera2075.modules.projects.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class MatchingSchedulerServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private FundingOpportunityRepository fundingOpportunityRepository;

    @Mock
    private ProjectMatchRepository projectMatchRepository;

    @Mock
    private MatchingWorkerClient matchingWorkerClient;

    @InjectMocks
    private MatchingSchedulerService matchingSchedulerService;

    @Test
    void generatesMissingEmbeddingsAndRefreshesMatches() {
        Project project = new Project();
        project.setId(8L);
        project.setTitle("Community project");

        FundingOpportunity opportunity = new FundingOpportunity();
        opportunity.setId(12L);
        opportunity.setTitle("Funding opportunity");

        when(projectRepository.findAllByEmbeddingVectorIsNull(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(project)), new PageImpl<>(List.of()));
        when(fundingOpportunityRepository.findAllByEmbeddingVectorIsNull(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(opportunity)), new PageImpl<>(List.of()));
        when(matchingWorkerClient.generateEmbeddings(anyList()))
                .thenReturn(List.of(response("project", 8L)), List.of(response("funding_opportunity", 12L)));
        when(projectMatchRepository.refreshSuggestedMatches(50)).thenReturn(1);

        MatchingSchedulerService.CycleResult result = matchingSchedulerService.processCycle(50);

        assertEquals(1, result.projectsEmbedded());
        assertEquals(1, result.opportunitiesEmbedded());
        assertEquals(1, result.matchesUpdated());
        assertNotNull(project.getEmbeddingVector());
        assertNotNull(opportunity.getEmbeddingVector());
        assertEquals(1536, project.getEmbeddingVector().length);
        assertEquals(1536, opportunity.getEmbeddingVector().length);
        verify(projectRepository).saveAll(anyList());
        verify(fundingOpportunityRepository).saveAll(anyList());
        verify(projectMatchRepository).refreshSuggestedMatches(eq(50));
    }

    @Test
    void rejectsNonPositiveRecommendationLimit() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> matchingSchedulerService.processCycle(0));
    }

    private EmbeddingWorkerResponse response(String entityType, long entityId) {
        float[] embedding = new float[1536];
        embedding[0] = 1.0f;
        return new EmbeddingWorkerResponse(entityType, entityId, "test-model", 768, 1536, embedding);
    }
}
