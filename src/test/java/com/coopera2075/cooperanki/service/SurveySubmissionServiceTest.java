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
    void persistsIndexedAnswersForRepeatableQuestions() {
        SurveySubmissionRequest.QuestionInput people = question("21.people", "¿Quiénes colaboran?");
        people.addMetadata(
                "repeatText",
                objectMapper.readTree("""
                        {"prefix":"21.tasks","label":"¿En qué tareas colabora?"}
                        """));
        SurveySubmissionRequest.QuestionInput hours = question("22.hours", "Horas semanales");
        hours.setType("repeat-number");
        SurveySubmissionRequest.QuestionInput amount = question("28.amount", "Monto mensual");
        amount.setType("income-amounts");
        SurveySubmissionRequest.QuestionInput detail = question("28.detail", "Detalle del ingreso");
        detail.setType("income-details");

        SurveySubmissionRequest request = new SurveySubmissionRequest();
        request.setQuestions(List.of(people, hours, amount, detail));
        request.setAnswers(Map.of(
                "21.people", objectMapper.readTree("[\"Integrante\"]"),
                "21.tasks.0", objectMapper.readTree("\"Compra y venta\""),
                "22.hours.1", objectMapper.readTree("\"50\""),
                "28.amount.0", objectMapper.readTree("\"2000000\""),
                "28.detail.0", objectMapper.readTree("\"Ingreso mensual\"")));
        when(surveyResponseRepository.save(any(SurveyResponse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SurveyResponse saved = service.submit(request);

        assertIndexedAnswer(saved, "21.tasks.0", "21.tasks", "Compra y venta");
        assertIndexedAnswer(saved, "22.hours.1", "22.hours", "50");
        assertIndexedAnswer(saved, "28.amount.0", "28.amount", "2000000");
        assertIndexedAnswer(saved, "28.detail.0", "28.detail", "Ingreso mensual");
        verify(surveyResponseRepository).save(saved);
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

    private void assertIndexedAnswer(
            SurveyResponse response, String code, String parentCode, String expectedAnswer) {
        SurveyQuestion indexedQuestion = response.getQuestions().stream()
                .filter(question -> code.equals(question.getCode()))
                .findFirst()
                .orElseThrow();
        assertEquals(parentCode, indexedQuestion.getParentQuestion().getCode());
        assertEquals(expectedAnswer, indexedQuestion.getAnswer().getAnswer().asString());
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
