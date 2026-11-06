package com.coopera2075.modules.projects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "project_attachments",
        schema = "public",
        indexes = @Index(name = "idx_project_attachments_project_id", columnList = "project_id"))
@Getter
@Setter
@NoArgsConstructor
public class ProjectAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @NotBlank
    @Size(max = 255)
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @NotBlank
    @Column(name = "s3_url", nullable = false, columnDefinition = "text")
    private String s3Url;

    @Size(max = 50)
    @Column(name = "mime_type", length = 50)
    private String mimeType;

    @Positive
    @Column(name = "size_bytes")
    private Long sizeBytes;
}
