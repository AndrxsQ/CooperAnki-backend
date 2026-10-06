package com.coopera2075.cooperanki.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.coopera2075.cooperanki.dto.CallPropRequest;
import com.coopera2075.cooperanki.dto.CallPropResponse;
import com.coopera2075.cooperanki.entity.CallProp;
import com.coopera2075.cooperanki.entity.CallStatus;
import com.coopera2075.cooperanki.entity.Project;
import com.coopera2075.cooperanki.exception.ResourceNotFoundException;
import com.coopera2075.cooperanki.repository.CallPropRepository;
import com.coopera2075.cooperanki.repository.ProjectRepository;
import com.coopera2075.cooperanki.security.CurrentUserProvider;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CallPropService {
    private final CallPropRepository callPropRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserProvider currentUserProvider;

    // Todos los filtros son opcionales y combinables; q busca texto en el título o el cuerpo
    @Transactional(readOnly = true)
    public List<CallPropResponse> findAll(Long projectId, CallStatus status, String q) {
        Specification<CallProp> filters = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (projectId != null) {
                predicates.add(builder.equal(root.get("project").get("id"), projectId));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (StringUtils.hasText(q)) {
                String pattern = SearchPatterns.contains(q);
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("title")), pattern, SearchPatterns.ESCAPE),
                        builder.like(builder.lower(root.get("body")), pattern, SearchPatterns.ESCAPE)));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };

        return callPropRepository.findAll(filters, Sort.by(Sort.Direction.DESC, "createdAt", "id")).stream()
                .map(CallPropResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CallPropResponse findById(Long id) {
        return CallPropResponse.from(getCallProp(id));
    }

    // La convocatoria siempre nace dentro de un proyecto y solo la crean los managers de ese proyecto
    @Transactional
    public CallPropResponse create(Long projectId, CallPropRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + projectId));
        currentUserProvider.requireManagerOf(projectId);

        CallProp callProp = new CallProp();
        callProp.setProject(project);
        callProp.setStatus(CallStatus.OPEN);
        applyRequest(callProp, request);

        return CallPropResponse.from(callPropRepository.save(callProp));
    }

    @Transactional
    public CallPropResponse update(Long id, CallPropRequest request) {
        CallProp callProp = getCallProp(id);
        currentUserProvider.requireManagerOf(callProp.getProject().getId());

        applyRequest(callProp, request);

        return CallPropResponse.from(callProp);
    }

    // Elimina en cascada las inversiones de la convocatoria
    @Transactional
    public void delete(Long id) {
        CallProp callProp = getCallProp(id);
        currentUserProvider.requireManagerOf(callProp.getProject().getId());

        callPropRepository.delete(callProp);
    }

    private void applyRequest(CallProp callProp, CallPropRequest request) {
        callProp.setTitle(request.getTitle());
        callProp.setBody(request.getBody());
        callProp.setDeadline(request.getDeadline());
        callProp.setTotalAmount(request.getTotalAmount());
        // Si no se envía estado se conserva el que ya tenía la convocatoria
        if (request.getStatus() != null) {
            callProp.setStatus(request.getStatus());
        }
    }

    private CallProp getCallProp(Long id) {
        return callPropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Convocatoria no encontrada con id: " + id));
    }
}
