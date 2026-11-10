package com.coopera2075.modules.matching;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectMatchRepository extends
        JpaRepository<ProjectMatch, Long>, JpaSpecificationExecutor<ProjectMatch> {
    @Override
    @EntityGraph(attributePaths = "fundingOpportunity")
    Page<ProjectMatch> findAll(Specification<ProjectMatch> specification, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO public.project_matches AS current_match (
                project_id,
                funding_opportunity_id,
                compatibility_score,
                match_status,
                calculated_at
            )
            SELECT
                p.id,
                nearest.id,
                GREATEST(
                    0,
                    LEAST(100, (1 - (p.embedding_vector <=> nearest.embedding_vector)) * 100)
                )::numeric(5, 2),
                'SUGGESTED',
                CURRENT_TIMESTAMP
            FROM public.projects p
            CROSS JOIN LATERAL (
                SELECT opportunity.id, opportunity.embedding_vector
                FROM public.funding_opportunities opportunity
                WHERE opportunity.embedding_vector IS NOT NULL
                ORDER BY opportunity.embedding_vector <=> p.embedding_vector
                LIMIT :maxRecommendationsPerProject
            ) nearest
            WHERE p.embedding_vector IS NOT NULL
            ON CONFLICT (project_id, funding_opportunity_id)
            DO UPDATE SET
                compatibility_score = EXCLUDED.compatibility_score,
                calculated_at = EXCLUDED.calculated_at
            WHERE current_match.match_status = 'SUGGESTED'
            """, nativeQuery = true)
    int refreshSuggestedMatches(@Param("maxRecommendationsPerProject") int maxRecommendationsPerProject);
}
