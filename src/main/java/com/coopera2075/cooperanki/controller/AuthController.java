// package com.coopera2075.cooperanki.controller;

// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.coopera2075.cooperanki.dto.AuthResponse;
// import com.coopera2075.cooperanki.dto.LoginRequest;
// import com.coopera2075.cooperanki.dto.RegisterRequest;

// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;

// import com.coopera2075.cooperanki.service.AuthService;

// @RestController
// @RequestMapping ("/api/v1/auth")
// @RequiredArgsConstructor 
// public class AuthController {
//     private final AuthService authService;

//     // Endpoint público para registrar nuevo usuario
//     @PostMapping("/register")
//     public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
//         AuthResponse response = authService.register(request);
//         return ResponseEntity.status(HttpStatus.CREATED).body(response);
//     }

//     // Endpoint público para autenticar credenciales y obtener JWT
//     @PostMapping("/login")
//     public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
//         AuthResponse response = authService.login(request);
//         return ResponseEntity.ok(response);
//     }

//     // Endpoint protegido para obtener los datos del usuario autenticado
//     @GetMapping("/me")
//     public ResponseEntity<AuthResponse> getProfile() {
//         AuthResponse response = authService.getCurrentUserProfile();
//         return ResponseEntity.ok(response);
//     }
    
// }
