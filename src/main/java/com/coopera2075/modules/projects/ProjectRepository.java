package com.coopera2075.modules.projects;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    boolean existsByIdentifierCode(String identifierCode);

    boolean existsByIdentifierCodeAndIdNot(String identifierCode, Long id);

    Page<Project> findAllByEmbeddingVectorIsNull(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"organization", "creator"})
    Page<Project> findAll(Specification<Project> specification, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"organization", "creator"})
    Page<Project> findAll(Pageable pageable);
}
