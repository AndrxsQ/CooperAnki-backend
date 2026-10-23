// package com.coopera2075.cooperanki.security;

// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.web.AuthenticationEntryPoint;
// import org.springframework.security.web.access.AccessDeniedHandler;
// import org.springframework.stereotype.Component;
// import org.springframework.web.servlet.HandlerExceptionResolver;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// // Los rechazos de la cadena de filtros ocurren antes de llegar a los controladores, así que
// // se delegan al GlobalExceptionHandler para responder 401/403 con el mismo JSON que el resto de la API.
// @Component
// public class RestSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
//     private final HandlerExceptionResolver handlerExceptionResolver;

//     public RestSecurityErrorHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {
//         this.handlerExceptionResolver = handlerExceptionResolver;
//     }

//     // Petición sin autenticar a una ruta protegida -> 401
//     @Override
//     public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
//         handlerExceptionResolver.resolveException(request, response, null, authException);
//     }

//     // Usuario autenticado sin permisos suficientes -> 403
//     @Override
//     public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
//         handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
//     }
// }
