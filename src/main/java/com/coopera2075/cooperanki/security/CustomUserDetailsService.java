package com.coopera2075.cooperanki.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coopera2075.cooperanki.entity.AppUser;
import com.coopera2075.cooperanki.entity.AuthIdentity;
import com.coopera2075.cooperanki.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Consultar el AppUser por su username
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // Extraer la entidad de autenticación asociada
        AuthIdentity authIdentity = appUser.getAuthIdentity();

        // Mapear los roles a GrantedAuthority
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(appUser.getRole()));

        // Retornar el objeto UserDetails de Spring Security
        return new org.springframework.security.core.userdetails.User(
                appUser.getUsername(),
                authIdentity.getPassword(), // Se obtiene de la relación 1:1
                authorities
        );
    }

}
