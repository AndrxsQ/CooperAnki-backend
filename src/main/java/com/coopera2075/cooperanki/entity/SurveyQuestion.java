package com.coopera2075.cooperanki.entity;

import tools.jackson.databind.JsonNode;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "survey_questions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_survey_questions_response_code",
                columnNames = { "response_id", "question_code" }))
@Getter
@Setter
@NoArgsConstructor
public class SurveyQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "response_id", nullable = false)
    private SurveyResponse response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_question_id")
    private SurveyQuestion parentQuestion;

    @Column(name = "question_code", nullable = false, length = 120)
    private String code;

    @Column(nullable = false, length = 255)
    private String section;

    @Column(nullable = false, columnDefinition = "text")
    private String prompt;

    @Column(name = "question_type", nullable = false, length = 40)
    private String type;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private JsonNode options;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode metadata;

    @OneToOne(mappedBy = "question", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private SurveyAnswer answer;
}
