// package com.coopera2075.cooperanki.dto;

// import com.coopera2075.cooperanki.entity.Investment;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// public class InvestmentResponse {
//     private Long id;
//     private double amount;
//     private Long funderId;
//     private String funderUsername;
//     private Long callPropId;
//     private String callPropTitle;

//     public static InvestmentResponse from(Investment investment) {
//         return InvestmentResponse.builder()
//                 .id(investment.getId())
//                 .amount(investment.getAmount())
//                 .funderId(investment.getFunder().getId())
//                 .funderUsername(investment.getFunder().getUsername())
//                 .callPropId(investment.getCallProp().getId())
//                 .callPropTitle(investment.getCallProp().getTitle())
//                 .build();
//     }
// }
