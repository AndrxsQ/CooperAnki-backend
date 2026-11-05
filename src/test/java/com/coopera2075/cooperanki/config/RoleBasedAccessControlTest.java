package com.coopera2075.cooperanki.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.coopera2075.cooperanki.controller.AuthController;
import com.coopera2075.cooperanki.controller.EconomicUnitController;
import com.coopera2075.cooperanki.service.AuthService;
import com.coopera2075.cooperanki.service.EconomicActivityQueryService;
import com.coopera2075.cooperanki.service.EconomicUnitQueryService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RoleBasedAccessControlTest.TestConfiguration.class)
class RoleBasedAccessControlTest {
    @Autowired
    private AuthController authController;

    @Autowired
    private AuthService authService;

    @Autowired
    private EconomicUnitController economicUnitController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void rejectsCooperaRolesFromEconomicUnitsEndpoints() {
        authenticateAs("ROLE_COOPERANT");

        assertThrows(AccessDeniedException.class, () -> economicUnitController.findAll());
    }

    @Test
    void allowsExecutiveRoleOnEconomicUnitsEndpoints() {
        authenticateAs("ROLE_EXECUTIVE");

        assertEquals(HttpStatus.OK, economicUnitController.findAll().getStatusCode());
    }

    @Test
    void restrictsRoleManagementToAdministrators() {
        authenticateAs("ROLE_EXECUTIVE");

        assertThrows(AccessDeniedException.class, () -> authController.getAvailableRoles());

        SecurityContextHolder.clearContext();
        authenticateAs("ROLE_ADMIN");
        when(authService.getAvailableRoles()).thenReturn(List.of());

        assertEquals(List.of(), authController.getAvailableRoles().getBody());
    }

    private void authenticateAs(String... authorities) {
        List<SimpleGrantedAuthority> grantedAuthorities = java.util.Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test-user", "password", grantedAuthorities));
    }

    @Configuration
    @EnableMethodSecurity
    static class TestConfiguration {
        @Bean
        AuthService authService() {
            return mock(AuthService.class);
        }

        @Bean
        AuthController authController(AuthService authService) {
            return new AuthController(authService);
        }

        @Bean
        EconomicUnitController economicUnitController() {
            return new EconomicUnitController(
                    mock(EconomicUnitQueryService.class),
                    mock(EconomicActivityQueryService.class));
        }
    }
}
