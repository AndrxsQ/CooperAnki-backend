package com.coopera2075.cooperanki.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.coopera2075.cooperanki.dto.AuthResponse;
import com.coopera2075.cooperanki.dto.UpdateUserRequest;
import com.coopera2075.cooperanki.dto.UserResponse;
import com.coopera2075.cooperanki.entity.AppUser;
import com.coopera2075.cooperanki.entity.AuthIdentity;
import com.coopera2075.cooperanki.entity.Manager;
import com.coopera2075.cooperanki.entity.UserRole;
import com.coopera2075.cooperanki.exception.BadRequestException;
import com.coopera2075.cooperanki.exception.ConflictException;
import com.coopera2075.cooperanki.exception.ResourceNotFoundException;
import com.coopera2075.cooperanki.repository.AppUserRepository;
import com.coopera2075.cooperanki.repository.ArrangementRepository;
import com.coopera2075.cooperanki.repository.AuthIdentityRepository;
import com.coopera2075.cooperanki.repository.FunderRepository;
import com.coopera2075.cooperanki.repository.ManagerRepository;
import com.coopera2075.cooperanki.security.CurrentUserProvider;

import lombok.RequiredArgsConstructor;

// Gestión de cuentas (Manager y Funder). El alta se hace desde AuthService.register
@Service
@RequiredArgsConstructor
public class UserService {
    private final AppUserRepository appUserRepository;
    private final ManagerRepository managerRepository;
    private final FunderRepository funderRepository;
    private final AuthIdentityRepository authIdentityRepository;
    private final ArrangementRepository arrangementRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll(UserRole role) {
        Long currentUserId = currentUserProvider.getCurrentUser().getId();

        // Sin filtro (role nulo) se devuelven todos los usuarios
        List<? extends AppUser> users = switch (role) {
            case MANAGER -> managerRepository.findAll();
            case FUNDER -> funderRepository.findAll();
            case null -> appUserRepository.findAll();
        };

        return users.stream()
                .map(user -> UserResponse.from(user, user.getId().equals(currentUserId)))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        Long currentUserId = currentUserProvider.getCurrentUser().getId();
        return UserResponse.from(getUser(id), id.equals(currentUserId));
    }

    // Devuelve un AuthResponse con un JWT nuevo: el token anterior deja de servir si cambia el username
    @Transactional
    public AuthResponse update(Long id, UpdateUserRequest request) {
        AppUser appUser = requireOwnAccount(id, "Solo puedes modificar tu propia cuenta");
        AuthIdentity authIdentity = appUser.getAuthIdentity();

        if (!appUser.getUsername().equals(request.getUsername())
                && appUserRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("El nombre de usuario ya está en uso");
        }
        if (!authIdentity.getEmail().equals(request.getEmail())
                && authIdentityRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("El correo electrónico ya está registrado");
        }

        if (StringUtils.hasText(request.getPassword())) {
            // Cambiar la contraseña exige demostrar que se conoce la actual
            if (!StringUtils.hasText(request.getCurrentPassword())
                    || !passwordEncoder.matches(request.getCurrentPassword(), authIdentity.getPassword())) {
                throw new BadRequestException("La contraseña actual no es correcta");
            }
            authIdentity.setPassword(authService.encodePassword(request.getPassword()));
        }

        appUser.setUsername(request.getUsername());
        authIdentity.setEmail(request.getEmail());

        return authService.buildAuthResponse(appUser, authService.issueToken(appUser));
    }

    @Transactional
    public void delete(Long id) {
        AppUser appUser = requireOwnAccount(id, "Solo puedes eliminar tu propia cuenta");

        // Un proyecto sin managers ya no podría ser editado ni eliminado por nadie
        if (appUser instanceof Manager && arrangementRepository.countProjectsManagedOnlyBy(id) > 0) {
            throw new ConflictException(
                    "Eres el único manager de al menos un proyecto: asigna otro manager o elimina el proyecto antes de borrar tu cuenta");
        }

        appUserRepository.delete(appUser);
    }

    private AppUser getUser(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    private AppUser requireOwnAccount(Long id, String deniedMessage) {
        AppUser currentUser = currentUserProvider.getCurrentUser();
        if (!currentUser.getId().equals(id)) {
            // Comprobar primero que existe para responder 404 en lugar de 403 ante un id inexistente
            getUser(id);
            throw new AccessDeniedException(deniedMessage);
        }
        return currentUser;
    }
}
