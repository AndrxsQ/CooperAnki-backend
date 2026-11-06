package com.coopera2075.modules.projects;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectAttachmentRepository extends JpaRepository<ProjectAttachment, Long> {
    List<ProjectAttachment> findAllByProjectIdOrderByIdDesc(Long projectId);

    Optional<ProjectAttachment> findByIdAndProjectId(Long id, Long projectId);
}
