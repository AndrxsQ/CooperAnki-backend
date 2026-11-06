package com.coopera2075.modules.projects;

import java.time.Instant;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;
import com.coopera2075.modules.organizations.Organization;
import com.coopera2075.modules.organizations.OrganizationRepository;
import com.coopera2075.modules.projects.dto.ProjectPageResponse;
import com.coopera2075.modules.projects.dto.ProjectRequest;
import com.coopera2075.modules.projects.dto.ProjectResponse;
import com.coopera2075.modules.projects.dto.ProjectStatusRequest;

import jakarta.persistence.criteria.Predicate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private static final String COOPERA_SUBJECT_PREFIX = "coopera:";

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProjectPageResponse findAll(
            String query,
            ProjectStatus status,
            Long organizationId,
            Long creatorId,
            Pageable pageable) {
        Specification<Project> specification = buildSpecification(query, status, organizationId, creatorId);
        Page<ProjectResponse> page = projectRepository.findAll(specification, pageable)
                .map(ProjectResponse::from);
        return ProjectPageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(long id) {
        return projectRepository.findById(id)
                .map(ProjectResponse::from)
                .orElseThrow(() -> projectNotFound(id));
    }

    @Transactional
    public ProjectResponse create(ProjectRequest request, String principal) {
        User creator = findCurrentAdmin(principal);
        validateSchedule(request.startsAt(), request.closesAt());
        String identifierCode = normalizeIdentifierCode(request.identifierCode());
        if (projectRepository.existsByIdentifierCode(identifierCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código del proyecto ya está registrado");
        }

        Organization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La organización indicada no existe"));

        Project project = new Project();
        project.setOrganization(organization);
        project.setCreator(creator);
        applyRequest(project, request, identifierCode);
        project.setStatus(ProjectStatus.DRAFT);
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(long id, ProjectRequest request) {
        validateSchedule(request.startsAt(), request.closesAt());
        Project project = findProject(id);
        String identifierCode = normalizeIdentifierCode(request.identifierCode());
        if (projectRepository.existsByIdentifierCodeAndIdNot(identifierCode, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código del proyecto ya está registrado");
        }

        Organization organization = organizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "La organización indicada no existe"));
        project.setOrganization(organization);
        applyRequest(project, request, identifierCode);
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse updateStatus(long id, ProjectStatusRequest request) {
        Project project = findProject(id);
        project.setStatus(request.status());
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public void delete(long id) {
        Project project = findProject(id);
        projectRepository.delete(project);
    }

    private Specification<Project> buildSpecification(
            String query,
            ProjectStatus status,
            Long organizationId,
            Long creatorId) {
        return (root, criteriaQuery, builder) -> {
            java.util.List<Predicate> predicates = new java.util.ArrayList<>();
            if (StringUtils.hasText(query)) {
                String pattern = "%" + escapeLike(query.trim().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(root.get("title")), pattern, '!'),
                        builder.like(builder.lower(root.get("identifierCode")), pattern, '!'),
                        builder.like(
                                builder.lower(builder.coalesce(root.get("executiveSummary"), "")),
                                pattern,
                                '!')));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (organizationId != null) {
                predicates.add(builder.equal(root.get("organization").get("id"), organizationId));
            }
            if (creatorId != null) {
                predicates.add(builder.equal(root.get("creator").get("id"), creatorId));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void applyRequest(Project project, ProjectRequest request, String identifierCode) {
        project.setIdentifierCode(identifierCode);
        project.setTitle(request.title().trim());
        project.setExecutiveSummary(request.executiveSummary());
        project.setRequestedBudget(request.requestedBudget());
        project.setStartsAt(request.startsAt());
        project.setClosesAt(request.closesAt());
    }

    private User findCurrentAdmin(String principal) {
        if (principal == null || !principal.startsWith(COOPERA_SUBJECT_PREFIX)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Se requiere una cuenta administrativa del sistema Coopera");
        }
        String email = principal.substring(COOPERA_SUBJECT_PREFIX.length());
        User user = userRepository.findByEmailWithRoleAndPermissions(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "La cuenta no pertenece al sistema Coopera"));
        if (!"ADMIN".equals(user.getRole().getName())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Solo un administrador puede gestionar proyectos");
        }
        return user;
    }

    private Project findProject(long id) {
        return projectRepository.findById(id).orElseThrow(() -> projectNotFound(id));
    }

    private void validateSchedule(Instant startsAt, Instant closesAt) {
        if (startsAt != null && closesAt != null && !closesAt.isAfter(startsAt)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "La fecha de cierre debe ser posterior a la fecha de inicio");
        }
    }

    private String normalizeIdentifierCode(String identifierCode) {
        return identifierCode.trim();
    }

    private String escapeLike(String value) {
        return value.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }

    private ResponseStatusException projectNotFound(long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no encontrado: " + id);
    }
}
