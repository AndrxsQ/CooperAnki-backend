// package com.coopera2075.cooperanki.dto;

// import java.time.LocalDateTime;
// import java.util.Map;

// import com.fasterxml.jackson.annotation.JsonInclude;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// public class ApiErrorResponse {
//     private LocalDateTime timestamp;
//     private int status;
//     private String error;

//     // Mensaje legible para el cliente (el frontend lo lee como data.message)
//     private String message;
//     private String path;

//     // Detalle campo -> mensaje, solo en errores de validación
//     @JsonInclude (JsonInclude.Include.NON_NULL)
//     private Map<String, String> errors;
// }
