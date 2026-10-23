// package com.coopera2075.cooperanki.service;

// import java.nio.charset.StandardCharsets;
// import java.util.List;

// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.coopera2075.cooperanki.dto.AuthResponse;
// import com.coopera2075.cooperanki.dto.LoginRequest;
// import com.coopera2075.cooperanki.dto.RegisterRequest;
// import com.coopera2075.cooperanki.entity.AppUser;
// import com.coopera2075.cooperanki.entity.AuthIdentity;
// import com.coopera2075.cooperanki.entity.Funder;
// import com.coopera2075.cooperanki.entity.Manager;
// import com.coopera2075.cooperanki.entity.UserRole;
// import com.coopera2075.cooperanki.exception.BadRequestException;
// import com.coopera2075.cooperanki.exception.ConflictException;
// import com.coopera2075.cooperanki.exception.ResourceNotFoundException;
// import com.coopera2075.cooperanki.repository.AppUserRepository;
// import com.coopera2075.cooperanki.repository.AuthIdentityRepository;
// import com.coopera2075.cooperanki.security.CurrentUserProvider;
// import com.coopera2075.cooperanki.security.JwtTokenProvider;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class AuthService {
//     private static final int BCRYPT_MAX_BYTES = 72;

//     private final AppUserRepository appUserRepository;
//     private final AuthIdentityRepository authIdentityRepository;
//     private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder
//     private final JwtTokenProvider jwtTokenProvider;
//     private final AuthenticationManager authenticationManager;
//     private final CurrentUserProvider currentUserProvider;

//     @Transactional
//     public AuthResponse register(RegisterRequest request) {
//         if (appUserRepository.existsByUsername(request.getUsername())) {
//             throw new ConflictException("El nombre de usuario ya está en uso");
//         }
//         if (authIdentityRepository.existsByEmail(request.getEmail())) {
//             throw new ConflictException("El correo electrónico ya está registrado");
//         }

//         // Si no se indica rol, el usuario se registra como FUNDER
//         AppUser appUser = request.getRole() == UserRole.MANAGER ? new Manager() : new Funder();
//         appUser.setUsername(request.getUsername());

//         AuthIdentity authIdentity = new AuthIdentity();
//         authIdentity.setUser(appUser);
//         authIdentity.setEmail(request.getEmail());
//         authIdentity.setPassword(encodePassword(request.getPassword()));
//         appUser.setAuthIdentity(authIdentity);
//         appUserRepository.save(appUser);

//         // Las credenciales se acaban de crear, así que el JWT se emite sin volver a autenticar
//         return buildAuthResponse(appUser, issueToken(appUser));
//     }

//     @Transactional(readOnly = true)
//     public AuthResponse login(LoginRequest request) {
//         Authentication authentication = authenticationManager.authenticate(
//                 new UsernamePasswordAuthenticationToken(
//                         request.getUsername(),
//                         request.getPassword()
//                 )
//         );

//         //Buscar usuario en BD
//         AppUser appUser = appUserRepository.findByUsername(request.getUsername())
//                 .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

//         // Genera JWT
//         String jwtToken = jwtTokenProvider.generateToken(authentication);

//         return buildAuthResponse(appUser, jwtToken);
//     }

//     @Transactional(readOnly = true)
//     public AuthResponse getCurrentUserProfile() {
//         // Obtener usuario autenticado del SecurityContextHolder
//         return buildAuthResponse(currentUserProvider.getCurrentUser(), null);
//     }

//     // BCrypt solo admite 72 bytes y @Size cuenta caracteres: con tildes, ñ o emojis una contraseña
//     // válida en longitud puede superar el límite y hacer fallar al encoder
//     public String encodePassword(String rawPassword) {
//         if (rawPassword.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
//             throw new BadRequestException("La contraseña es demasiado larga");
//         }
//         return passwordEncoder.encode(rawPassword);
//     }

//     // Emitir un JWT nuevo para el usuario (p. ej. tras registrarse o cambiar su username)
//     public String issueToken(AppUser appUser) {
//         return jwtTokenProvider.generateToken(appUser.getUsername(), List.of(appUser.getRole()));
//     }

//     public AuthResponse buildAuthResponse(AppUser appUser, String jwtToken) {
//         return AuthResponse.builder()
//                 .accessToken(jwtToken)
//                 .id(appUser.getId())
//                 .nombre(appUser.getUsername())
//                 .email(appUser.getAuthIdentity().getEmail())
//                 .role(appUser.getRole())
//                 .build();
//     }
// }
