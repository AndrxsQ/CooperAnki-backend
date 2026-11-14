package com.coopera2075.modules.matching;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FundingOpportunityRepository
        extends JpaRepository<FundingOpportunity, Long>,
                JpaSpecificationExecutor<FundingOpportunity> {
    Optional<FundingOpportunity> findBySourceUrl(String sourceUrl);

    boolean existsBySourceUrl(String sourceUrl);

    Page<FundingOpportunity> findAllByEmbeddingVectorIsNull(Pageable pageable);
}
