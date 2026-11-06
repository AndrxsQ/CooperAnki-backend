package com.coopera2075.cooperanki.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.cooperanki.dto.AuthResponse;
import com.coopera2075.cooperanki.dto.LoginRequest;
import com.coopera2075.cooperanki.entity.AuthIdentity;
import com.coopera2075.cooperanki.repository.AuthIdentityRepository;
import com.coopera2075.cooperanki.security.CustomUserDetailsService;
import com.coopera2075.cooperanki.security.JwtTokenProvider;
import com.coopera2075.modules.auth.LoginSystem;
import com.coopera2075.modules.auth.RefreshToken;
import com.coopera2075.modules.auth.RefreshTokenRepository;
import com.coopera2075.modules.auth.Role;
import com.coopera2075.modules.auth.RoleRepository;
import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;
import com.coopera2075.modules.auth.dto.CreateUserRequest;
import com.coopera2075.modules.auth.dto.CreatedUserResponse;
import com.coopera2075.modules.auth.dto.RefreshTokenRequest;
import com.coopera2075.modules.auth.dto.RoleOptionResponse;
import com.coopera2075.modules.organizations.Organization;
import com.coopera2075.modules.organizations.CooperaOrganizationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {
    private static final int BCRYPT_MAX_BYTES = 72;
    private static final long DEFAULT_REFRESH_EXPIRATION_MS = 604_800_000L;
    private static final String COOPERA_SUBJECT_PREFIX = "coopera:";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AuthenticationManager authenticationManager;
    private final AuthIdentityRepository authIdentityRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CooperaOrganizationRepository organizationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final long refreshTokenExpirationMs;

    public AuthService(
            AuthenticationManager authenticationManager,
            AuthIdentityRepository authIdentityRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            CooperaOrganizationRepository organizationRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            CustomUserDetailsService userDetailsService,
            JwtTokenProvider jwtTokenProvider,
            @Value("${jwt.refresh-expiration-ms:" + DEFAULT_REFRESH_EXPIRATION_MS + "}")
                    long refreshTokenExpirationMs) {
        if (refreshTokenExpirationMs <= 0) {
            throw new IllegalArgumentException("jwt.refresh-expiration-ms debe ser positivo");
        }
        this.authenticationManager = authenticationManager;
        this.authIdentityRepository = authIdentityRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        ensurePasswordWithinBcryptLimit(request.password());
        String identifier = request.username().trim();
        String principal = identifier;
        if (request.system() == LoginSystem.ECONOMIC_UNITS) {
            principal = "economic-units:" + identifier;
        } else if (request.system() == LoginSystem.COOPERA) {
            principal = COOPERA_SUBJECT_PREFIX + identifier;
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(principal, request.password()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos");
        }

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = issueRefreshToken(authentication.getName());
        return toResponse(authentication.getName(), accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        String tokenHash = hashRefreshToken(request.refreshToken());
        RefreshToken storedToken = refreshTokenRepository.findByTokenHashForUpdate(tokenHash)
                .filter(token -> token.getRevokedAt() == null && token.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "El refresh token no es válido o ha expirado"));

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(storedToken.getSubject());
        } catch (UsernameNotFoundException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "El refresh token no es válido o ha expirado");
        }
        if (!userDetails.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "El refresh token no es válido o ha expirado");
        }

        Instant now = Instant.now();
        storedToken.setRevokedAt(now);
        refreshTokenRepository.save(storedToken);

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                userDetails, null, userDetails.getAuthorities());
        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = issueRefreshToken(userDetails.getUsername());
        return toResponse(userDetails.getUsername(), accessToken, refreshToken);
    }

    @Transactional
    public CreatedUserResponse register(CreateUserRequest request) {
        ensurePasswordWithinBcryptLimit(request.password());
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)
                || authIdentityRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol indicado no existe"));
        Organization organization = request.organizationId() == null
                ? null
                : organizationRepository.findById(request.organizationId())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "La organización indicada no existe"));

        User account = new User();
        account.setFullName(request.fullName().trim());
        account.setEmail(normalizedEmail);
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setRole(role);
        account.setOrganization(organization);
        account.setActive(true);

        User savedAccount = userRepository.save(account);
        return new CreatedUserResponse(
                savedAccount.getId(),
                savedAccount.getFullName(),
                savedAccount.getEmail(),
                savedAccount.getRole().getName(),
                savedAccount.isActive());
    }

    @Transactional(readOnly = true)
    public List<RoleOptionResponse> getAvailableRoles() {
        return roleRepository.findAllByOrderByNameAsc().stream()
                .map(role -> new RoleOptionResponse(role.getId(), role.getName(), role.getDescription()))
                .toList();
    }

    @Transactional(readOnly = true)
    public AuthResponse getCurrentUserProfile(String subject) {
        return toResponse(subject, null, null);
    }

    private AuthResponse toResponse(String subject, String accessToken, String refreshToken) {
        if (subject.startsWith(COOPERA_SUBJECT_PREFIX)) {
            User account = userRepository.findByEmailWithRoleAndPermissions(
                            subject.substring(COOPERA_SUBJECT_PREFIX.length()))
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
            List<String> systems = account.getRole().getPermissions().stream()
                    .map(permission -> permission.getCode())
                    .filter(code -> code.startsWith("SYSTEM_") && code.endsWith("_ACCESS"))
                    .map(code -> code.substring("SYSTEM_".length(), code.length() - "_ACCESS".length()))
                    .distinct()
                    .sorted()
                    .toList();
            return new AuthResponse(
                    accessToken,
                    "Bearer",
                    account.getId(),
                    account.getFullName(),
                    account.getEmail(),
                    account.getEmail(),
                    account.getRole().getName(),
                    LoginSystem.COOPERA.name(),
                    systems,
                    refreshToken);
        }

        String username = subject.startsWith("economic-units:")
                ? subject.substring("economic-units:".length())
                : subject;
        AuthIdentity identity = authIdentityRepository.findByUsernameWithUser(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
        return new AuthResponse(
                accessToken,
                "Bearer",
                identity.getUser().getId().longValue(),
                identity.getUser().getName(),
                identity.getUsername(),
                identity.getEmail(),
                identity.getUser().getRole().name(),
                LoginSystem.ECONOMIC_UNITS.name(),
                List.of(LoginSystem.ECONOMIC_UNITS.name()),
                refreshToken);
    }

    private String issueRefreshToken(String subject) {
        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        RefreshToken token = new RefreshToken();
        token.setTokenHash(hashRefreshToken(rawToken));
        token.setSubject(subject);
        token.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshTokenRepository.save(token);
        return rawToken;
    }

    private String hashRefreshToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            log.error("SHA-256 is unavailable for refresh token hashing", exception);
            throw new IllegalStateException("No se pudo procesar el refresh token", exception);
        }
    }

    private void ensurePasswordWithinBcryptLimit(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es demasiado larga");
        }
    }
}
