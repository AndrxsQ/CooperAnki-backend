package com.coopera2075.cooperanki.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SurveySubmissionRequest {
    @NotEmpty
    @Size(max = 1000)
    private List<@Valid QuestionInput> questions;

    @NotEmpty
    @Size(max = 1000)
    private Map<
            @NotBlank @Size(max = 120) @Pattern(regexp = "[A-Za-z0-9._-]+") String,
            JsonNode> answers;

    @Getter
    @Setter
    public static class QuestionInput {
        @NotBlank
        @Size(max = 120)
        @Pattern(regexp = "[A-Za-z0-9._-]+")
        @JsonProperty("id")
        private String code;

        @NotBlank
        @Size(max = 255)
        private String section;

        @NotBlank
        @Size(max = 10000)
        @JsonProperty("title")
        private String prompt;

        @NotBlank
        @Size(max = 40)
        private String type;

        private JsonNode options;

        private final Map<String, JsonNode> metadata = new LinkedHashMap<>();

        @JsonAnySetter
        public void addMetadata(String name, JsonNode value) {
            metadata.put(name, value);
        }
    }

    public void setQuestions(List<QuestionInput> questions) {
        this.questions = questions == null ? null : new ArrayList<>(questions);
    }
}
