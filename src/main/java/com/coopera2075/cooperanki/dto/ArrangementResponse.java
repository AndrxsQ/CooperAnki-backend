// package com.coopera2075.cooperanki.dto;

// import com.coopera2075.cooperanki.entity.Arrangement;

// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @Builder
// @AllArgsConstructor
// @NoArgsConstructor
// public class ArrangementResponse {
//     private Long id;
//     private Long managerId;
//     private String managerUsername;
//     private Long projectId;
//     private String projectName;

//     public static ArrangementResponse from(Arrangement arrangement) {
//         return ArrangementResponse.builder()
//                 .id(arrangement.getId())
//                 .managerId(arrangement.getManager().getId())
//                 .managerUsername(arrangement.getManager().getUsername())
//                 .projectId(arrangement.getProject().getId())
//                 .projectName(arrangement.getProject().getName())
//                 .build();
//     }
// }
