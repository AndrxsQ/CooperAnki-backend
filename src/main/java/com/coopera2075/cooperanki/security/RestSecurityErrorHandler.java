package com.coopera2075.cooperanki.security;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        writeError(response, HttpServletResponse.SC_UNAUTHORIZED,
                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Debes iniciar sesión para acceder a este recurso\"}");
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exception) throws IOException {
        writeError(response, HttpServletResponse.SC_FORBIDDEN,
                "{\"status\":403,\"error\":\"Forbidden\",\"message\":\"No tienes permisos para acceder a este recurso\"}");
    }

    private void writeError(
            HttpServletResponse response,
            int status,
            String body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(body);
    }
}
