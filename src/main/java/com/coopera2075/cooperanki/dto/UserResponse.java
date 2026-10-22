// package com.coopera2075.cooperanki.dto;

// import java.time.LocalDateTime;

// import com.coopera2075.cooperanki.entity.AppUser;
// import com.fasterxml.jackson.annotation.JsonInclude;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// public class UserResponse {
//     private Long id;
//     private String username;

//     // Dato privado: solo se incluye cuando el usuario consulta su propia cuenta
//     @JsonInclude (JsonInclude.Include.NON_NULL)
//     private String email;

//     private String role;
//     private LocalDateTime createdAt;

//     public static UserResponse from(AppUser user, boolean includeEmail) {
//         return UserResponse.builder()
//                 .id(user.getId())
//                 .username(user.getUsername())
//                 .email(includeEmail && user.getAuthIdentity() != null ? user.getAuthIdentity().getEmail() : null)
//                 .role(user.getRole())
//                 .createdAt(user.getCreatedAt())
//                 .build();
//     }
// }
