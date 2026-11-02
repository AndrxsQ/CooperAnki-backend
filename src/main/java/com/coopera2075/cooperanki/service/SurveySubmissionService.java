package com.coopera2075.cooperanki.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.coopera2075.cooperanki.dto.SurveySubmissionRequest;
import com.coopera2075.cooperanki.entity.SurveyAnswer;
import com.coopera2075.cooperanki.entity.SurveyQuestion;
import com.coopera2075.cooperanki.entity.SurveyResponse;
import com.coopera2075.cooperanki.repository.SurveyResponseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveySubmissionService {
    private final SurveyResponseRepository surveyResponseRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public SurveyResponse submit(SurveySubmissionRequest request) {
        SurveyResponse response = new SurveyResponse();
        List<SurveyQuestion> questions = new ArrayList<>();
        Map<String, SurveyQuestion> questionsByCode = new HashMap<>();
        Set<String> questionCodes = new HashSet<>();

        for (int index = 0; index < request.getQuestions().size(); index++) {
            SurveySubmissionRequest.QuestionInput input = request.getQuestions().get(index);
            SurveyQuestion question = createQuestion(response, input, index, null);
            addQuestion(question, questions, questionsByCode, questionCodes);
            createInlineQuestions(response, question, input, index, questions, questionsByCode, questionCodes);
            createRepeatTextQuestion(response, question, input, index, questions, questionsByCode, questionCodes);
        }

        for (Map.Entry<String, JsonNode> answerEntry : request.getAnswers().entrySet()) {
            SurveyQuestion question = questionsByCode.get(answerEntry.getKey());
            if (question == null) {
                question = createIndexedQuestion(
                        answerEntry.getKey(), response, questions, questionsByCode, questionCodes);
                if (question == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "La respuesta '" + answerEntry.getKey()
                                    + "' no corresponde a una pregunta enviada");
                }
            }

            SurveyAnswer answer = new SurveyAnswer();
            answer.setQuestion(question);
            answer.setAnswer(answerEntry.getValue());
            question.setAnswer(answer);
        }

        response.setQuestions(questions);
        return surveyResponseRepository.save(response);
    }

    private SurveyQuestion createIndexedQuestion(
            String code,
            SurveyResponse response,
            List<SurveyQuestion> questions,
            Map<String, SurveyQuestion> questionsByCode,
            Set<String> questionCodes) {
        int indexSeparator = code.lastIndexOf('.');
        if (indexSeparator <= 0 || indexSeparator == code.length() - 1
                || !code.substring(indexSeparator + 1).matches("[0-9]+")) {
            return null;
        }

        SurveyQuestion parent = questionsByCode.get(code.substring(0, indexSeparator));
        if (parent == null || !isRepeatable(parent.getType())) {
            return null;
        }

        SurveyQuestion question = new SurveyQuestion();
        question.setResponse(response);
        question.setParentQuestion(parent);
        question.setCode(code);
        question.setSection(parent.getSection());
        question.setPrompt(parent.getPrompt());
        question.setType(parent.getType());
        question.setDisplayOrder(parent.getDisplayOrder());
        question.setOptions(parent.getOptions());
        question.setMetadata(parent.getMetadata());
        addQuestion(question, questions, questionsByCode, questionCodes);
        return question;
    }

    private boolean isRepeatable(String type) {
        return type != null && (type.startsWith("repeat-")
                || "income-details".equals(type)
                || "income-amounts".equals(type));
    }

    private SurveyQuestion createQuestion(
            SurveyResponse response,
            SurveySubmissionRequest.QuestionInput input,
            int order,
            SurveyQuestion parent) {
        SurveyQuestion question = new SurveyQuestion();
        question.setResponse(response);
        question.setParentQuestion(parent);
        question.setCode(input.getCode());
        question.setSection(input.getSection());
        question.setPrompt(input.getPrompt());
        question.setType(input.getType());
        question.setDisplayOrder(order);
        question.setOptions(input.getOptions());
        question.setMetadata(objectMapper.valueToTree(input.getMetadata()));
        return question;
    }

    private void createInlineQuestions(
            SurveyResponse response,
            SurveyQuestion parent,
            SurveySubmissionRequest.QuestionInput input,
            int order,
            List<SurveyQuestion> questions,
            Map<String, SurveyQuestion> questionsByCode,
            Set<String> questionCodes) {
        JsonNode inlineInputs = input.getMetadata().get("inlineInputs");
        if (inlineInputs == null) {
            return;
        }
        if (!inlineInputs.isArray()) {
            throw invalidQuestion(input.getCode(), "inlineInputs debe ser una lista");
        }

        for (JsonNode child : inlineInputs) {
            String code = textValue(child, "id");
            String prompt = textValue(child, "label");
            String type = textValue(child, "type");
            if (code == null || prompt == null || !code.matches("[A-Za-z0-9._-]{1,120}")) {
                throw invalidQuestion(input.getCode(), "cada pregunta adicional requiere id y label válidos");
            }
            if (prompt.length() > 10000 || (type != null && type.length() > 40)) {
                throw invalidQuestion(input.getCode(), "el texto o tipo de una pregunta adicional es demasiado largo");
            }

            SurveyQuestion subquestion = new SurveyQuestion();
            subquestion.setResponse(response);
            subquestion.setParentQuestion(parent);
            subquestion.setCode(code);
            subquestion.setSection(input.getSection());
            subquestion.setPrompt(prompt);
            subquestion.setType(type == null ? "text" : type);
            subquestion.setDisplayOrder(order);
            subquestion.setMetadata(child);
            addQuestion(subquestion, questions, questionsByCode, questionCodes);
        }
    }

    private void createRepeatTextQuestion(
            SurveyResponse response,
            SurveyQuestion parent,
            SurveySubmissionRequest.QuestionInput input,
            int order,
            List<SurveyQuestion> questions,
            Map<String, SurveyQuestion> questionsByCode,
            Set<String> questionCodes) {
        JsonNode repeatText = input.getMetadata().get("repeatText");
        if (repeatText == null || !repeatText.isObject()) {
            return;
        }

        String code = textValue(repeatText, "prefix");
        String prompt = textValue(repeatText, "label");
        if (code == null || prompt == null || !code.matches("[A-Za-z0-9._-]{1,120}")) {
            throw invalidQuestion(input.getCode(), "repeatText requiere prefix y label válidos");
        }
        if (prompt.length() > 10000) {
            throw invalidQuestion(input.getCode(), "el texto de repeatText es demasiado largo");
        }

        SurveyQuestion repeatedQuestion = new SurveyQuestion();
        repeatedQuestion.setResponse(response);
        repeatedQuestion.setParentQuestion(parent);
        repeatedQuestion.setCode(code);
        repeatedQuestion.setSection(input.getSection());
        repeatedQuestion.setPrompt(prompt);
        repeatedQuestion.setType("repeat-text");
        repeatedQuestion.setDisplayOrder(order);
        repeatedQuestion.setMetadata(repeatText);
        addQuestion(repeatedQuestion, questions, questionsByCode, questionCodes);
    }

    private void addQuestion(
            SurveyQuestion question,
            List<SurveyQuestion> questions,
            Map<String, SurveyQuestion> questionsByCode,
            Set<String> questionCodes) {
        if (!questionCodes.add(question.getCode())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador de pregunta '" + question.getCode() + "' está repetido");
        }
        questions.add(question);
        questionsByCode.put(question.getCode(), question);
    }

    private ResponseStatusException invalidQuestion(String code, String detail) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La definición de la pregunta '" + code + "' no es válida: " + detail);
    }

    private String textValue(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && value.isTextual() && !value.asText().isBlank()
                ? value.asText()
                : null;
    }
}
