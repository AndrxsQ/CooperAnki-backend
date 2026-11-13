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

    @Column(name = "donor_country", length = 150)
    private String donorCountry;

    @Column(name = "cooperation_type", length = 100)
    private String cooperationType;

    @Column(name = "organization_type", columnDefinition = "text")
    private String organizationType;

    @Column(name = "project_type", length = 100)
    private String projectType;

    @Column(name = "target_populations", columnDefinition = "text")
    private String targetPopulations;

    @Column(name = "application_method", columnDefinition = "text")
    private String applicationMethod;

    @Column(name = "language", length = 150)
    private String language;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "amount_in_cop", precision = 15, scale = 2)
    private BigDecimal amountInCop;

    @Column(name = "source_page", length = 100)
    private String sourcePage;

    @Column(name = "source_document", columnDefinition = "text")
    private String sourceDocument;

    @Column(name = "opportunity_status", length = 50)
    private String opportunityStatus;

    @Column(name = "opportunity_status_detail", columnDefinition = "text")
    private String opportunityStatusDetail;

    @Column(name = "contact_person", length = 150)
    private String contactPerson;

    @Column(name = "contact_phone", length = 100)
    private String contactPhone;

    @Column(name = "contact_fax", length = 100)
    private String contactFax;

    @Column(name = "contact_address", columnDefinition = "text")
    private String contactAddress;

    @Column(name = "contact_city", length = 150)
    private String contactCity;

    @Column(name = "contact_email", length = 150)
    private String contactEmail;

    @Column(name = "local_contact_person", length = 150)
    private String localContactPerson;

    @Column(name = "local_contact_phone", length = 100)
    private String localContactPhone;

    @Column(name = "local_contact_address", columnDefinition = "text")
    private String localContactAddress;

    @Column(name = "local_contact_city", length = 150)
    private String localContactCity;

    @Column(name = "local_contact_email", length = 150)
    private String localContactEmail;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding_vector", columnDefinition = "vector(1536)")
    private float[] embeddingVector;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
