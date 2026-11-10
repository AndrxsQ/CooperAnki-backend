package com.coopera2075.modules.matching;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "funding_opportunities",
        schema = "public",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_funding_opportunities_source_url",
                columnNames = "source_url"))
@Getter
@Setter
@NoArgsConstructor
public class FundingOpportunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "source_url", nullable = false, columnDefinition = "text")
    private String sourceUrl;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "cooperating_entity", length = 150)
    private String cooperatingEntity;

    @Column(name = "impact_area", length = 150)
    private String impactArea;

    @Column(name = "requirements_description", columnDefinition = "text")
    private String requirementsDescription;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "max_amount", precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding_vector", columnDefinition = "vector(1536)")
    private float[] embeddingVector;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
