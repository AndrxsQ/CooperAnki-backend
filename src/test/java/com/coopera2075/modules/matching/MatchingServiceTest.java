package com.coopera2075.modules.matching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.modules.projects.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMatchRepository projectMatchRepository;

    @InjectMocks
    private MatchingService matchingService;

    @Test
    void returnsProjectRecommendationsSortedByScoreAndCalculationTime() {
        FundingOpportunity opportunity = new FundingOpportunity();
        opportunity.setId(12L);
        opportunity.setTitle("Community program");
        opportunity.setSourceUrl("https://funding.example/opportunity");
        opportunity.setCooperatingEntity("Funding body");

        ProjectMatch match = new ProjectMatch();
        match.setId(7L);
        match.setFundingOpportunity(opportunity);
        match.setCompatibilityScore(new BigDecimal("92.50"));
        match.setStatus(MatchStatus.SUGGESTED);
        match.setCalculatedAt(Instant.parse("2026-01-01T00:00:00Z"));

        when(projectRepository.existsById(3L)).thenReturn(true);
        when(projectMatchRepository.findByProject_Id(eq(3L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(match), PageRequest.of(0, 20), 1));

        var response = matchingService.getRecommendations(3L, 0, 20);

        assertEquals(1, response.totalElements());
        assertEquals(7L, response.content().get(0).matchId());
        assertEquals(12L, response.content().get(0).fundingOpportunityId());
        assertEquals(new BigDecimal("92.50"), response.content().get(0).compatibilityScore());
        assertEquals(MatchStatus.SUGGESTED, response.content().get(0).status());
        verify(projectMatchRepository).findByProject_Id(
                eq(3L),
                eq(PageRequest.of(0, 20, Sort.by(
                        Sort.Order.desc("compatibilityScore"),
                        Sort.Order.desc("calculatedAt")))));
    }

    @Test
    void rejectsRecommendationsForMissingProject() {
        when(projectRepository.existsById(404L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> matchingService.getRecommendations(404L, 0, 20));

        assertEquals(404, exception.getStatusCode().value());
    }
}
