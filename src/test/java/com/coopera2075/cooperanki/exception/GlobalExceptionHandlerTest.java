package com.coopera2075.cooperanki.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import com.coopera2075.cooperanki.dto.ApiErrorResponse;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsNotFoundToTheUnifiedApiErrorResponse() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/projects/42");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleNotFound(new ResourceNotFoundException("Proyecto no encontrado"), request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Not Found", response.getBody().getError());
        assertEquals("Proyecto no encontrado", response.getBody().getMessage());
        assertEquals("/api/v1/projects/42", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void hidesUnexpectedExceptionDetailsFromTheClient() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/projects");

        ResponseEntity<ApiErrorResponse> response =
                handler.handleUnexpected(new IllegalStateException("database password"), request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("Ha ocurrido un error interno en el servidor", response.getBody().getMessage());
        assertEquals("/api/v1/projects", response.getBody().getPath());
    }
}
