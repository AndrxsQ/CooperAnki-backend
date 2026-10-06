package com.coopera2075.cooperanki.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.coopera2075.cooperanki.entity.AppUser;
import com.coopera2075.cooperanki.entity.Funder;
import com.coopera2075.cooperanki.entity.Manager;
import com.coopera2075.cooperanki.repository.AppUserRepository;
import com.coopera2075.cooperanki.repository.ArrangementRepository;

import lombok.RequiredArgsConstructor;

// Punto único para saber quién hace la petición y qué puede hacer sobre un proyecto
@Component
@RequiredArgsConstructor
public class CurrentUserProvider {
    private final AppUserRepository appUserRepository;
    private final ArrangementRepository arrangementRepository;

    // Obtener la entidad del usuario autenticado a partir del SecurityContextHolder
    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("No hay un usuario autenticado");
        }

        return appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new InsufficientAuthenticationException("El usuario autenticado ya no existe"));
    }

    public Manager getCurrentManager() {
        if (getCurrentUser() instanceof Manager manager) {
            return manager;
        }
        throw new AccessDeniedException("Solo un manager puede realizar esta acción");
    }

    public Funder getCurrentFunder() {
        if (getCurrentUser() instanceof Funder funder) {
            return funder;
        }
        throw new AccessDeniedException("Solo un funder puede realizar esta acción");
    }

    // Exigir que el usuario autenticado sea un manager asignado al proyecto (vía arrangement)
    public Manager requireManagerOf(Long projectId) {
        Manager manager = getCurrentManager();
        if (!arrangementRepository.existsByManagerIdAndProjectId(manager.getId(), projectId)) {
            throw new AccessDeniedException("No eres manager de este proyecto");
        }
        return manager;
    }
}
