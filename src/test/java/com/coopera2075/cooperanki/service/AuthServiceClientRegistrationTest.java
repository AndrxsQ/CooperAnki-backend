package com.coopera2075.cooperanki.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.coopera2075.cooperanki.security.CustomUserDetailsService;
import com.coopera2075.cooperanki.security.JwtTokenProvider;
import com.coopera2075.modules.auth.RefreshTokenRepository;
import com.coopera2075.modules.auth.Role;
import com.coopera2075.modules.auth.RoleRepository;
import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;
import com.coopera2075.modules.auth.dto.ClientRegistrationRequest;
import com.coopera2075.modules.auth.dto.CreatedUserResponse;
import com.coopera2075.modules.organizations.CooperaOrganizationRepository;

class AuthServiceClientRegistrationTest {
    @Test
    void registersNewAccountsWithClientRoleAndNormalizedEmail() {
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        CooperaOrganizationRepository organizationRepository = mock(CooperaOrganizationRepository.class);
        RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        Role clientRole = new Role();
        clientRole.setName("CLIENT");

        when(roleRepository.findByName("CLIENT")).thenReturn(java.util.Optional.of(clientRole));
        when(userRepository.existsByEmailIgnoreCase("client@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuthService authService = new AuthService(
                authenticationManager,
                userRepository,
                roleRepository,
                organizationRepository,
                refreshTokenRepository,
                passwordEncoder,
                userDetailsService,
                jwtTokenProvider,
                604_800_000L);

        CreatedUserResponse response = authService.registerClient(
                new ClientRegistrationRequest("Cliente", "Client@Example.com", "password123"));

        assertEquals("CLIENT", response.role());
        assertEquals("client@example.com", response.email());
        verify(roleRepository).findByName("CLIENT");
    }
}
