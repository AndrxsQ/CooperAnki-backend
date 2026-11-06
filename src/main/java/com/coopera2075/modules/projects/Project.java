package com.coopera2075.modules.projects;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.organizations.Organization;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "projects",
        schema = "public",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_projects_identifier_code",
                columnNames = "identifier_code"),
        indexes = {
                @Index(name = "idx_projects_organization_id", columnList = "organization_id"),
                @Index(name = "idx_projects_creator_id", columnList = "creator_id"),
                @Index(name = "idx_projects_status", columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @NotBlank
    @Size(max = 50)
    @Column(name = "identifier_code", nullable = false, length = 50)
    private String identifierCode;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "executive_summary", columnDefinition = "text")
    private String executiveSummary;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "requested_budget", precision = 15, scale = 2)
    private BigDecimal requestedBudget;

    @Column(name = "starts_at")
    private Instant startsAt;

    @Column(name = "closes_at")
    private Instant closesAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProjectStatus status = ProjectStatus.DRAFT;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "embedding_vector", columnDefinition = "vector(1536)")
    private float[] embeddingVector;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectAttachment> attachments = new ArrayList<>();

    public void addAttachment(ProjectAttachment attachment) {
        attachments.add(attachment);
        attachment.setProject(this);
    }

    public void removeAttachment(ProjectAttachment attachment) {
        attachments.remove(attachment);
        attachment.setProject(null);
    }
}
