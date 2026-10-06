package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coopera2075.cooperanki.dto.ArrangementRequest;
import com.coopera2075.cooperanki.dto.ArrangementResponse;
import com.coopera2075.cooperanki.entity.Arrangement;
import com.coopera2075.cooperanki.entity.Manager;
import com.coopera2075.cooperanki.entity.Project;
import com.coopera2075.cooperanki.exception.ConflictException;
import com.coopera2075.cooperanki.exception.ResourceNotFoundException;
import com.coopera2075.cooperanki.repository.ArrangementRepository;
import com.coopera2075.cooperanki.repository.ManagerRepository;
import com.coopera2075.cooperanki.repository.ProjectRepository;
import com.coopera2075.cooperanki.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

// Un arrangement es la asignación de un manager a un proyecto. No tiene más datos que esa pareja,
// así que no hay "actualizar": para cambiar una asignación se elimina y se crea otra.
@Service
@RequiredArgsConstructor
public class ArrangementService {
    private final ArrangementRepository arrangementRepository;
    private final ManagerRepository managerRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserProvider currentUserProvider;

    // Ambos filtros son opcionales y combinables
    @Transactional(readOnly = true)
    public List<ArrangementResponse> findAll(Long projectId, Long managerId) {
        List<Arrangement> arrangements;
        if (projectId != null && managerId != null) {
            arrangements = arrangementRepository.findByManagerIdAndProjectId(managerId, projectId);
        } else if (projectId != null) {
            arrangements = arrangementRepository.findByProjectId(projectId);
        } else if (managerId != null) {
            arrangements = arrangementRepository.findByManagerId(managerId);
        } else {
            arrangements = arrangementRepository.findAll();
        }

        return arrangements.stream()
                .map(ArrangementResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ArrangementResponse findById(Long id) {
        return ArrangementResponse.from(getArrangement(id));
    }

    // Solo un manager que ya gestiona el proyecto puede sumar a otro manager
    @Transactional
    public ArrangementResponse create(ArrangementRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + request.getProjectId()));
        currentUserProvider.requireManagerOf(project.getId());

        // ManagerRepository solo devuelve usuarios con rol MANAGER: un funder no puede gestionar proyectos
        Manager manager = managerRepository.findById(request.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("Manager no encontrado con id: " + request.getManagerId()));

        if (arrangementRepository.existsByManagerIdAndProjectId(manager.getId(), project.getId())) {
            throw new ConflictException("El manager ya está asignado a este proyecto");
        }

        Arrangement arrangement = new Arrangement();
        arrangement.setManager(manager);
        arrangement.setProject(project);

        return ArrangementResponse.from(arrangementRepository.save(arrangement));
    }

    @Transactional
    public void delete(Long id) {
        Arrangement arrangement = getArrangement(id);
        Long projectId = arrangement.getProject().getId();
        currentUserProvider.requireManagerOf(projectId);

        // Un proyecto sin managers ya no podría ser editado ni eliminado por nadie
        if (arrangementRepository.countByProjectId(projectId) <= 1) {
            throw new ConflictException("No se puede quitar al único manager del proyecto");
        }

        arrangementRepository.delete(arrangement);
    }

    private Arrangement getArrangement(Long id) {
        return arrangementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arrangement no encontrado con id: " + id));
    }
}
