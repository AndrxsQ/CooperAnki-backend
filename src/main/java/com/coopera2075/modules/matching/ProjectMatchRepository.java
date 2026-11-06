package com.coopera2075.modules.matching;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectMatchRepository extends JpaRepository<ProjectMatch, Long> {
    @EntityGraph(attributePaths = "fundingOpportunity")
    Page<ProjectMatch> findByProject_Id(Long projectId, Pageable pageable);
}
