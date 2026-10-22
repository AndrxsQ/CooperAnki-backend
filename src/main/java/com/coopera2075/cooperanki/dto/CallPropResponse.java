// package com.coopera2075.cooperanki.dto;

// import java.time.LocalDate;
// import java.time.LocalDateTime;

// import com.coopera2075.cooperanki.entity.CallProp;
// import com.coopera2075.cooperanki.entity.CallStatus;
// import com.coopera2075.cooperanki.entity.Investment;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// public class CallPropResponse {
//     private Long id;
//     private String title;
//     private String body;
//     private LocalDate deadline;
//     private double totalAmount;

//     // Suma de las inversiones recibidas hasta el momento
//     private double raisedAmount;

//     private CallStatus status;
//     private LocalDateTime createdAt;
//     private Long projectId;
//     private String projectName;

//     public static CallPropResponse from(CallProp callProp) {
//         return CallPropResponse.builder()
//                 .id(callProp.getId())
//                 .title(callProp.getTitle())
//                 .body(callProp.getBody())
//                 .deadline(callProp.getDeadline())
//                 .totalAmount(callProp.getTotalAmount())
//                 .raisedAmount(callProp.getInvestments().stream().mapToDouble(Investment::getAmount).sum())
//                 .status(callProp.getStatus())
//                 .createdAt(callProp.getCreatedAt())
//                 .projectId(callProp.getProject().getId())
//                 .projectName(callProp.getProject().getName())
//                 .build();
//     }
// }
