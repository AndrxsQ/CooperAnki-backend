package com.coopera2075.modules.matching;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.coopera2075.modules.projects.Project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "project_matches",
        schema = "public",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_project_matches_project_opportunity",
                columnNames = {"project_id", "funding_opportunity_id"}),
        indexes = {
                @Index(name = "idx_project_matches_score", columnList = "compatibility_score"),
                @Index(name = "idx_project_matches_opportunity_id", columnList = "funding_opportunity_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class ProjectMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funding_opportunity_id", nullable = false)
    private FundingOpportunity fundingOpportunity;

    @Column(name = "compatibility_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal compatibilityScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_status", nullable = false, length = 20)
    private MatchStatus status = MatchStatus.SUGGESTED;

    @CreationTimestamp
    @Column(name = "calculated_at", nullable = false, updatable = false)
    private Instant calculatedAt;
}
