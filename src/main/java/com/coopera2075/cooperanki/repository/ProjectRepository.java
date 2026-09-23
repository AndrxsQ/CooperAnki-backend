package com.coopera2075.cooperanki.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coopera2075.cooperanki.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
}
