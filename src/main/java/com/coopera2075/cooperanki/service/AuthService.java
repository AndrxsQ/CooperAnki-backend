package com.coopera2075.cooperanki.service;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.cooperanki.dto.AuthResponse;
import com.coopera2075.cooperanki.dto.LoginRequest;
import com.coopera2075.cooperanki.entity.AuthIdentity;
import com.coopera2075.cooperanki.repository.AuthIdentityRepository;
import com.coopera2075.cooperanki.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final int BCRYPT_MAX_BYTES = 72;

    private final AuthenticationManager authenticationManager;
    private final AuthIdentityRepository authIdentityRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es demasiado larga");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.username(), request.password()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }
        AuthIdentity identity = findIdentity(authentication.getName());
        return toResponse(identity, jwtTokenProvider.generateToken(authentication));
    }

    @Transactional(readOnly = true)
    public AuthResponse getCurrentUserProfile(String username) {
        return toResponse(findIdentity(username), null);
    }

    private AuthIdentity findIdentity(String username) {
        return authIdentityRepository.findByUsernameWithUser(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
    }

    private AuthResponse toResponse(AuthIdentity identity, String token) {
        return new AuthResponse(
                token,
                "Bearer",
                identity.getUser().getId(),
                identity.getUser().getName(),
                identity.getUsername(),
                identity.getEmail(),
                identity.getUser().getRole().name());
    }
}
