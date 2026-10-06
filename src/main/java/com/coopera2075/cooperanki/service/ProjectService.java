package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coopera2075.cooperanki.dto.ProjectRequest;
import com.coopera2075.cooperanki.dto.ProjectResponse;
import com.coopera2075.cooperanki.entity.Arrangement;
import com.coopera2075.cooperanki.entity.Manager;
import com.coopera2075.cooperanki.entity.Project;
import com.coopera2075.cooperanki.exception.ResourceNotFoundException;
import com.coopera2075.cooperanki.repository.ProjectRepository;
import com.coopera2075.cooperanki.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final CurrentUserProvider currentUserProvider;

    // q es opcional: filtra por texto en el nombre o la descripción
    @Transactional(readOnly = true)
    public List<ProjectResponse> findAll(String q) {
        return projectRepository.search(SearchPatterns.contains(q)).stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(Long id) {
        return ProjectResponse.from(getProject(id));
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Manager manager = currentUserProvider.getCurrentManager();

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());

        // El manager que crea el proyecto queda asignado como su primer gestor
        Arrangement arrangement = new Arrangement();
        arrangement.setManager(manager);
        arrangement.setProject(project);
        project.getArrangements().add(arrangement);

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getProject(id);
        currentUserProvider.requireManagerOf(id);

        project.setName(request.getName());
        project.setDescription(request.getDescription());

        return ProjectResponse.from(project);
    }

    // Elimina en cascada los arrangements, convocatorias e inversiones del proyecto
    @Transactional
    public void delete(Long id) {
        Project project = getProject(id);
        currentUserProvider.requireManagerOf(id);

        projectRepository.delete(project);
    }

    private Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + id));
    }
}
