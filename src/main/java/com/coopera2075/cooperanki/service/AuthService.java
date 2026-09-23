package com.coopera2075.cooperanki.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.coopera2075.cooperanki.dto.AuthResponse;
import com.coopera2075.cooperanki.dto.LoginRequest;
import com.coopera2075.cooperanki.dto.RegisterRequest;
import com.coopera2075.cooperanki.entity.AppUser;
import com.coopera2075.cooperanki.entity.AuthIdentity;
import com.coopera2075.cooperanki.entity.Funder;
import com.coopera2075.cooperanki.repository.AppUserRepository;
import com.coopera2075.cooperanki.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        Funder appUser = new Funder();
        appUser.setUsername(request.getUsername());

        AuthIdentity authIdentity = new AuthIdentity();
        authIdentity.setUser(appUser);
        authIdentity.setEmail(request.getEmail());
        authIdentity.setPassword(passwordEncoder.encode(request.getPassword()));
        appUser.setAuthIdentity(authIdentity);
        appUserRepository.save(appUser);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        String jwtToken = jwtTokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .id(appUser.getId())
                .nombre(appUser.getUsername())
                .email(authIdentity.getEmail())
                .role(appUser.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        //Buscar usuario en BD
        AppUser appUser = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Genera JWT
        String jwtToken = jwtTokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .id(appUser.getId())
                .nombre(appUser.getUsername())
                .email(appUser.getAuthIdentity().getEmail())
                .role(appUser.getRole())
                .build();
    }

    public AuthResponse getCurrentUserProfile() {
        // Obtener usuario autenticado del SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser appUser = appUserRepository.findByUsername(authentication.getName())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return AuthResponse.builder()
                        .id(appUser.getId())
                        .nombre(appUser.getUsername())
                        .email(appUser.getAuthIdentity().getEmail())
                        .role(appUser.getRole())
                        .build();
    }
}
