package com.coopera2075.cooperanki.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.coopera2075.cooperanki.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException exception,
            HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, exception.getMessage(), request, null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            BadRequestException exception,
            HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        String message = errors.values().stream()
                .findFirst()
                .orElse("Los datos enviados no son válidos");
        return build(HttpStatus.BAD_REQUEST, message, request, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getConstraintViolations()
                .forEach(violation -> errors.putIfAbsent(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()));

        String message = errors.values().stream()
                .findFirst()
                .orElse("Los datos enviados no son válidos");
        return build(HttpStatus.BAD_REQUEST, message, request, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadable(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        return build(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es válido o tiene un formato incorrecto",
                request,
                null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request) {
        return build(
                HttpStatus.BAD_REQUEST,
                "El valor del parámetro '" + exception.getName() + "' no es válido",
                request,
                null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            AuthenticationException exception,
            HttpServletRequest request) {
        return build(
                HttpStatus.UNAUTHORIZED,
                "Debes iniciar sesión para acceder a este recurso",
                request,
                null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request) {
        return build(
                HttpStatus.FORBIDDEN,
                "No tienes permisos para realizar esta acción",
                request,
                null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request) {
        log.warn("Violación de integridad en {} {}", request.getMethod(), request.getRequestURI());
        return build(
                HttpStatus.CONFLICT,
                "La operación entra en conflicto con los datos existentes",
                request,
                null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request) {
        if (exception instanceof ErrorResponse errorResponse) {
            HttpStatusCode status = errorResponse.getStatusCode();
            String message = errorResponse.getBody().getDetail();
            if (message == null || message.isBlank()) {
                message = status.toString();
            }
            return build(status, message, request, null);
        }

        log.error("Error no controlado en {} {}", request.getMethod(), request.getRequestURI(), exception);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ha ocurrido un error interno en el servidor",
                request,
                null);
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatusCode status,
            String message,
            HttpServletRequest request,
            Map<String, String> errors) {
        HttpStatus resolvedStatus = HttpStatus.resolve(status.value());
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(resolvedStatus != null ? resolvedStatus.getReasonPhrase() : "Error")
                .message(message)
                .path(request.getRequestURI())
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
