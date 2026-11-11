package com.coopera2075.cooperanki.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.coopera2075.modules.auth.RefreshTokenRepository;
import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;
import com.coopera2075.modules.auth.dto.AccountPreferencesRequest;
import com.coopera2075.modules.auth.dto.AccountResponse;
import com.coopera2075.modules.auth.dto.ChangePasswordRequest;
import com.coopera2075.modules.auth.dto.UpdateAccountRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private static final String COOPERA_SUBJECT_PREFIX = "coopera:";
    private static final int BCRYPT_MAX_BYTES = 72;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public AccountResponse getAccount(String subject) {
        return toResponse(findAccount(subject));
    }

    @Transactional
    public AccountResponse updateAccount(String subject, UpdateAccountRequest request) {
        User account = findAccount(subject);
        if (!passwordEncoder.matches(request.currentPassword(), account.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
        }

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        if (!account.getEmail().equalsIgnoreCase(normalizedEmail)
                && userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }

        account.setFullName(request.fullName().trim());
        account.setEmail(normalizedEmail);
        User savedAccount = userRepository.save(account);
        if (!subject.equals(COOPERA_SUBJECT_PREFIX + normalizedEmail)) {
            refreshTokenRepository.revokeActiveTokensBySubject(subject);
        }
        return toResponse(savedAccount);
    }

    @Transactional
    public AccountResponse updatePreferences(String subject, AccountPreferencesRequest request) {
        User account = findAccount(subject);
        account.setEmailNotificationsEnabled(request.emailNotificationsEnabled());
        return toResponse(userRepository.save(account));
    }

    @Transactional
    public void changePassword(String subject, ChangePasswordRequest request) {
        User account = findAccount(subject);
        if (!passwordEncoder.matches(request.currentPassword(), account.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
        }
        if (request.newPassword().getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es demasiado larga");
        }

        account.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(account);
        refreshTokenRepository.revokeActiveTokensBySubject(subject);
    }

    private User findAccount(String subject) {
        String email = subject.startsWith(COOPERA_SUBJECT_PREFIX)
                ? subject.substring(COOPERA_SUBJECT_PREFIX.length())
                : subject;
        return userRepository.findByEmailWithRoleAndPermissions(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La cuenta no existe"));
    }

    private AccountResponse toResponse(User account) {
        return new AccountResponse(
                account.getId(),
                account.getFullName(),
                account.getEmail(),
                account.getRole().getName(),
                account.isActive(),
                account.isEmailNotificationsEnabled());
    }
}
