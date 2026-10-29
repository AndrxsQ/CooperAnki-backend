package com.coopera2075.cooperanki.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import com.coopera2075.cooperanki.dto.SurveySubmissionRequest;
import com.coopera2075.cooperanki.entity.SurveyQuestion;
import com.coopera2075.cooperanki.entity.SurveyResponse;
import com.coopera2075.cooperanki.repository.SurveyResponseRepository;

@ExtendWith(MockitoExtension.class)
class SurveySubmissionServiceTest {
    @Mock
    private SurveyResponseRepository surveyResponseRepository;

    private SurveySubmissionService service;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder().build();
        service = new SurveySubmissionService(surveyResponseRepository, objectMapper);
    }

    @Test
    void persistsQuestionsAnswersAndInlineQuestionsTogether() {
        SurveySubmissionRequest request = new SurveySubmissionRequest();
        SurveySubmissionRequest.QuestionInput question = question("14", "¿Ha liderado un negocio?");
        question.addMetadata(
                "inlineInputs",
                objectMapper.readTree("""
                        [{"id":"14.details","label":"¿Cuáles?","when":"Sí","type":"textarea"}]
                        """));
        request.setQuestions(List.of(question));
        request.setAnswers(new LinkedHashMap<>(Map.of(
                "14", objectMapper.readTree("\"Sí\""),
                "14.details", objectMapper.readTree("\"Asociación local\""))));
        when(surveyResponseRepository.save(any(SurveyResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SurveyResponse saved = service.submit(request);

        assertEquals(2, saved.getQuestions().size());
        SurveyQuestion parent = saved.getQuestions().get(0);
        SurveyQuestion child = saved.getQuestions().get(1);
        assertEquals("14", parent.getCode());
        assertEquals("14.details", child.getCode());
        assertEquals(parent, child.getParentQuestion());
        assertEquals("Sí", parent.getAnswer().getAnswer().asString());
        assertEquals("Asociación local", child.getAnswer().getAnswer().asString());
        verify(surveyResponseRepository).save(saved);
    }

    @Test
    void acceptsQuestionDefinitionsInTheShapeReturnedByTheFrontend() {
        SurveySubmissionRequest request = objectMapper.readValue("""
                {
                  "questions": [{
                    "id": "14",
                    "section": "Perfil",
                    "title": "¿Ha liderado un negocio?",
                    "type": "radio",
                    "options": ["Sí", "No"],
                    "allowUnknown": true,
                    "inlineInputs": [{
                      "id": "14.details",
                      "label": "¿Cuáles?",
                      "when": "Sí",
                      "type": "textarea"
                    }]
                  }],
                  "answers": {"14": "Sí", "14.details": "Asociación local"}
                }
                """, SurveySubmissionRequest.class);
        when(surveyResponseRepository.save(any(SurveyResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SurveyResponse saved = service.submit(request);

        assertEquals(2, saved.getQuestions().size());
        assertTrue(saved.getQuestions().get(0).getMetadata().has("allowUnknown"));
        assertTrue(saved.getQuestions().get(0).getMetadata().has("inlineInputs"));
        assertEquals(2, saved.getQuestions().get(0).getOptions().size());
    }

    @Test
    void rejectsAnswersForQuestionsThatWereNotPresented() {
        SurveySubmissionRequest request = new SurveySubmissionRequest();
        request.setQuestions(List.of(question("1", "Nombre completo")));
        request.setAnswers(Map.of("unknown", objectMapper.readTree("\"respuesta\"")));

        assertThrows(ResponseStatusException.class, () -> service.submit(request));
        verify(surveyResponseRepository, never()).save(any(SurveyResponse.class));
    }

    @Test
    void rejectsDuplicateQuestionCodes() {
        SurveySubmissionRequest request = new SurveySubmissionRequest();
        request.setQuestions(List.of(question("1", "Nombre"), question("1", "Otro")));
        request.setAnswers(Map.of("1", objectMapper.readTree("\"respuesta\"")));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> service.submit(request));

        assertTrue(exception.getReason().contains("está repetido"));
        verify(surveyResponseRepository, never()).save(any(SurveyResponse.class));
    }

    private SurveySubmissionRequest.QuestionInput question(String code, String prompt) {
        SurveySubmissionRequest.QuestionInput question = new SurveySubmissionRequest.QuestionInput();
        question.setCode(code);
        question.setSection("Perfil");
        question.setPrompt(prompt);
        question.setType("text");
        return question;
    }
}
