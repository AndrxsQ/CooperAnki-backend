package com.coopera2075.cooperanki.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coopera2075.cooperanki.dto.AuthResponse;
import com.coopera2075.cooperanki.dto.LoginRequest;
import com.coopera2075.cooperanki.service.AuthService;
import com.coopera2075.modules.auth.dto.CreateUserRequest;
import com.coopera2075.modules.auth.dto.ClientRegistrationRequest;
import com.coopera2075.modules.auth.dto.CreatedUserResponse;
import com.coopera2075.modules.auth.dto.LogoutRequest;
import com.coopera2075.modules.auth.dto.RefreshTokenRequest;
import com.coopera2075.modules.auth.dto.RoleOptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    @Operation(security = {})
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @PreAuthorize("permitAll()")
    @Operation(security = {})
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> logout(
            Authentication authentication,
            @Valid @RequestBody LogoutRequest request) {
        authService.logout(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleOptionResponse>> getAvailableRoles() {
        return ResponseEntity.ok(authService.getAvailableRoles());
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreatedUserResponse> register(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/register/client")
    @PreAuthorize("permitAll()")
    @Operation(security = {})
    public ResponseEntity<CreatedUserResponse> registerClient(
            @Valid @RequestBody ClientRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerClient(request));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthResponse> getCurrentUserProfile(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUserProfile(authentication.getName()));
    }
}
