package com.coopera2075.modules.projects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ProjectControllerAuthorizationTest.TestConfiguration.class)
class ProjectControllerAuthorizationTest {
    @Autowired
    private ProjectController projectController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void preventsUsersWithoutCooperationSubsystemFromReadingProjects() {
        authenticateAs("ROLE_CLIENT", "PERM_SYSTEM_PERSONAL_ACCOUNT_ACCESS");

        assertThrows(AccessDeniedException.class, () -> projectController.findById(1L));
    }

    @Test
    void preventsInformationManagementAdminWithoutCooperationPermissionFromReadingProjects() {
        authenticateAs("ROLE_ADMIN", "PERM_SYSTEM_INFORMATION_MANAGEMENT_ACCESS");

        assertThrows(AccessDeniedException.class, () -> projectController.findById(1L));
    }

    @Test
    void allowsGeneralExecutivesToReadProjects() {
        authenticateAs("ROLE_GENERAL_EXE", "PERM_SYSTEM_COOPERATION_ACCESS");

        org.junit.jupiter.api.Assertions.assertEquals(
                HttpStatus.OK,
                projectController.findById(1L).getStatusCode());
    }

    @Test
    void allowsCooperationRolesToChangeProjects() {
        authenticateAs("ROLE_GENERAL_EXE", "PERM_SYSTEM_COOPERATION_ACCESS");

        org.junit.jupiter.api.Assertions.assertEquals(
                HttpStatus.CREATED,
                projectController.create(
                        null,
                        SecurityContextHolder.getContext().getAuthentication()).getStatusCode());
    }

    @Test
    void rejectsProjectChangesWithoutCooperationSubsystem() {
        authenticateAs("ROLE_LEGAL_EXE", "PERM_SYSTEM_INFORMATION_MANAGEMENT_ACCESS");

        assertThrows(
                AccessDeniedException.class,
                () -> projectController.create(null, null));
    }

    private void authenticateAs(String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test-user",
                        "password",
                        java.util.Arrays.stream(authorities)
                                .map(SimpleGrantedAuthority::new)
                                .toList()));
    }

    @Configuration
    @EnableMethodSecurity
    static class TestConfiguration {
        @Bean
        ProjectService projectService() {
            return mock(ProjectService.class);
        }

        @Bean
        ProjectController projectController(ProjectService projectService) {
            return new ProjectController(projectService, mock(ProjectAttachmentService.class));
        }
    }
}
