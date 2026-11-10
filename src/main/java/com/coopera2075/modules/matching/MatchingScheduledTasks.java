package com.coopera2075.modules.matching;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "app.matching.scheduler", name = "enabled", havingValue = "true")
public class MatchingScheduledTasks {
    private final MatchingSchedulerService matchingSchedulerService;

    @Value("${app.matching.max-recommendations-per-project:50}")
    private int maxRecommendationsPerProject;

    @Scheduled(
            fixedDelayString = "${app.matching.scheduler.fixed-delay-ms:3600000}",
            initialDelayString = "${app.matching.scheduler.initial-delay-ms:60000}")
    public void processMatchingCycle() {
        MatchingSchedulerService.CycleResult result =
                matchingSchedulerService.processCycle(maxRecommendationsPerProject);
        log.info(
                "Matching cycle completed: {} project embeddings, {} opportunity embeddings, {} recommendations",
                result.projectsEmbedded(),
                result.opportunitiesEmbedded(),
                result.matchesUpdated());
    }
}
