// package com.coopera2075.cooperanki.dto;

// import java.time.LocalDate;

// import com.coopera2075.cooperanki.entity.CallStatus;

// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Positive;
// import jakarta.validation.constraints.Size;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// public class CallPropRequest {
//     @NotBlank (message = "El título no puede estar vacío")
//     @Size (min = 2, max = 100, message = "El título debe tener entre 2 y 100 caracteres")
//     private String title;

//     private String body;

//     private LocalDate deadline;

//     @NotNull (message = "El monto total es obligatorio")
//     @Positive (message = "El monto total debe ser mayor que 0")
//     private Double totalAmount;

//     // Opcional: al crear se asume OPEN; al actualizar se conserva el estado actual
//     private CallStatus status;
// }
