package com.coopera2075.cooperanki.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.SurveySubmissionRequest;
import com.coopera2075.cooperanki.dto.SurveySubmissionResponse;
import com.coopera2075.cooperanki.entity.SurveyResponse;
import com.coopera2075.cooperanki.service.SurveySubmissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/surveys")
@RequiredArgsConstructor
public class SurveySubmissionController {
    private final SurveySubmissionService surveySubmissionService;

    @PostMapping("/responses")
    public ResponseEntity<SurveySubmissionResponse> submit(
            @Valid @RequestBody SurveySubmissionRequest request) {
        SurveyResponse response = surveySubmissionService.submit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SurveySubmissionResponse(response.getId(), response.getSubmittedAt()));
    }
}
