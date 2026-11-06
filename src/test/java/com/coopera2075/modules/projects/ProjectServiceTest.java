package com.coopera2075.modules.projects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.modules.auth.Role;
import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;
import com.coopera2075.modules.organizations.Organization;
import com.coopera2075.modules.organizations.OrganizationRepository;
import com.coopera2075.modules.projects.dto.ProjectRequest;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createsProjectWithAdminAsCreatorAndDraftStatus() {
        Organization organization = new Organization();
        organization.setId(8L);
        organization.setName("Coopera");
        User admin = userWithRole("ADMIN");
        admin.setId(12L);

        when(projectRepository.existsByIdentifierCode("PRJ-001")).thenReturn(false);
        when(organizationRepository.findById(8L)).thenReturn(Optional.of(organization));
        when(userRepository.findByEmailWithRoleAndPermissions("admin@example.com"))
                .thenReturn(Optional.of(admin));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = projectService.create(
                new ProjectRequest(
                        8L,
                        " PRJ-001 ",
                        "Community project",
                        "Executive summary",
                        null,
                        Instant.parse("2027-01-01T00:00:00Z"),
                        Instant.parse("2027-06-01T00:00:00Z")),
                "coopera:admin@example.com");

        assertEquals(8L, response.organizationId());
        assertEquals(12L, response.creatorId());
        assertEquals("PRJ-001", response.identifierCode());
        assertEquals(ProjectStatus.DRAFT, response.status());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void rejectsCreationFromNonCooperaPrincipal() {
        assertThrows(
                ResponseStatusException.class,
                () -> projectService.create(
                        validRequest(),
                        "legacy-admin"));

        verifyNoInteractions(projectRepository, organizationRepository, userRepository);
    }

    @Test
    void rejectsCreationByCooperaNonAdmin() {
        when(userRepository.findByEmailWithRoleAndPermissions("user@example.com"))
                .thenReturn(Optional.of(userWithRole("COOPERANT")));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.create(validRequest(), "coopera:user@example.com"));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void rejectsInvalidProjectSchedule() {
        ProjectRequest request = new ProjectRequest(
                8L,
                "PRJ-001",
                "Community project",
                null,
                null,
                Instant.parse("2027-06-01T00:00:00Z"),
                Instant.parse("2027-01-01T00:00:00Z"));
        when(userRepository.findByEmailWithRoleAndPermissions("admin@example.com"))
                .thenReturn(Optional.of(userWithRole("ADMIN")));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.create(request, "coopera:admin@example.com"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(projectRepository, organizationRepository);
    }

    @Test
    void rejectsDuplicateProjectCodes() {
        when(userRepository.findByEmailWithRoleAndPermissions("admin@example.com"))
                .thenReturn(Optional.of(userWithRole("ADMIN")));
        when(projectRepository.existsByIdentifierCode("PRJ-001")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> projectService.create(validRequest(), "coopera:admin@example.com"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verifyNoInteractions(organizationRepository);
    }

    private ProjectRequest validRequest() {
        return new ProjectRequest(8L, "PRJ-001", "Community project", null, null, null, null);
    }

    private User userWithRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        User user = new User();
        user.setRole(role);
        user.setEmail("admin@example.com");
        user.setFullName("Project Administrator");
        return user;
    }
}
