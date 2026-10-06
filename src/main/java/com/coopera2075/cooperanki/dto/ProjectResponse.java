package com.coopera2075.cooperanki.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.coopera2075.cooperanki.entity.Project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    // Managers asignados al proyecto a través de sus arrangements
    private List<ManagerSummary> managers;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ManagerSummary {
        private Long id;
        private String username;
    }

    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .managers(project.getArrangements().stream()
                        .map(arrangement -> new ManagerSummary(
                                arrangement.getManager().getId(),
                                arrangement.getManager().getUsername()))
                        .toList())
                .build();
    }
}
