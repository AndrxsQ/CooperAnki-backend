package com.coopera2075.cooperanki.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.cooperanki.dto.AuthResponse;
import com.coopera2075.cooperanki.security.CustomUserDetailsService;
import com.coopera2075.cooperanki.security.JwtTokenProvider;
import com.coopera2075.modules.auth.Permission;
import com.coopera2075.modules.auth.RefreshTokenRepository;
import com.coopera2075.modules.auth.Role;
import com.coopera2075.modules.auth.RoleRepository;
import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;
import com.coopera2075.modules.organizations.CooperaOrganizationRepository;

class AuthServiceSubsystemTest {
    @Test
    void returnsAllowedAndDefaultSubsystemsForEverySupportedRole() {
        List<RoleSubsystems> expected = List.of(
                new RoleSubsystems(
                        "ADMIN",
                        List.of("COOPERATION", "INFORMATION_MANAGEMENT", "PERSONAL_ACCOUNT"),
                        "COOPERATION"),
                new RoleSubsystems(
                        "GENERAL_EXE",
                        List.of("COOPERATION", "INFORMATION_MANAGEMENT"),
                        "COOPERATION"),
                new RoleSubsystems(
                        "LEGAL_EXE",
                        List.of("INFORMATION_MANAGEMENT"),
                        "INFORMATION_MANAGEMENT"),
                new RoleSubsystems(
                        "TERRITORIAL_EXE",
                        List.of("INFORMATION_MANAGEMENT"),
                        "INFORMATION_MANAGEMENT"),
                new RoleSubsystems(
                        "CLIENT",
                        List.of("PERSONAL_ACCOUNT"),
                        "PERSONAL_ACCOUNT"));
        UserRepository userRepository = mock(UserRepository.class);
        AuthService authService = new AuthService(
                mock(AuthenticationManager.class),
                userRepository,
                mock(RoleRepository.class),
                mock(CooperaOrganizationRepository.class),
                mock(RefreshTokenRepository.class),
                mock(PasswordEncoder.class),
                mock(CustomUserDetailsService.class),
                mock(JwtTokenProvider.class),
                604_800_000L);

        for (RoleSubsystems roleSubsystems : expected) {
            User account = accountWithRoleAndPermissions(
                    roleSubsystems.role(),
                    permissionsFor(roleSubsystems.allowedSystems()));
            when(userRepository.findByEmailWithRoleAndPermissions("test@example.com"))
                    .thenReturn(java.util.Optional.of(account));

            AuthResponse response = authService.getCurrentUserProfile("coopera:test@example.com");

            assertEquals(roleSubsystems.role(), response.role());
            assertEquals(roleSubsystems.allowedSystems(), response.allowedSystems());
            assertEquals(roleSubsystems.defaultSystem(), response.defaultSystem());
            assertEquals(roleSubsystems.defaultSystem(), response.accountSystem());
        }
    }

    @Test
    void rejectsAccountsWhoseStoredPermissionsDoNotMatchTheRole() {
        UserRepository userRepository = mock(UserRepository.class);
        AuthService authService = new AuthService(
                mock(AuthenticationManager.class),
                userRepository,
                mock(RoleRepository.class),
                mock(CooperaOrganizationRepository.class),
                mock(RefreshTokenRepository.class),
                mock(PasswordEncoder.class),
                mock(CustomUserDetailsService.class),
                mock(JwtTokenProvider.class),
                604_800_000L);
        when(userRepository.findByEmailWithRoleAndPermissions("test@example.com"))
                .thenReturn(java.util.Optional.of(
                        accountWithRoleAndPermissions("CLIENT", permissionsFor(List.of("COOPERATION")))));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.getCurrentUserProfile("coopera:test@example.com"));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    private User accountWithRoleAndPermissions(String roleName, Set<Permission> permissions) {
        Role role = new Role();
        role.setName(roleName);
        role.setPermissions(permissions);
        User account = new User();
        account.setEmail("test@example.com");
        account.setFullName("Test Account");
        account.setRole(role);
        return account;
    }

    private Set<Permission> permissionsFor(List<String> systems) {
        return systems.stream()
                .map(system -> {
                    Permission permission = new Permission();
                    permission.setCode("SYSTEM_" + system + "_ACCESS");
                    return permission;
                })
                .collect(java.util.stream.Collectors.toSet());
    }

    private record RoleSubsystems(String role, List<String> allowedSystems, String defaultSystem) {
    }
}
