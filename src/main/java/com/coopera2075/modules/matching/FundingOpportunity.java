package com.coopera2075.modules.matching;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "funding_opportunities", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class FundingOpportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_url", nullable = false, columnDefinition = "text")
    private String sourceUrl;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "cooperating_entity", length = 150)
    private String cooperatingEntity;

    @Column(name = "impact_area", length = 150)
    private String impactArea;

    @Column(name = "requirements_description", columnDefinition = "text")
    private String requirementsDescription;

    @Column(name = "max_amount", precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
