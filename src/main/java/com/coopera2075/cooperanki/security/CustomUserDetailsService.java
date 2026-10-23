package com.coopera2075.cooperanki.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coopera2075.cooperanki.entity.AuthIdentity;
import com.coopera2075.cooperanki.repository.AuthIdentityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AuthIdentityRepository authIdentityRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthIdentity identity = authIdentityRepository.findByUsernameWithUser(username)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));

        return User.withUsername(identity.getUsername())
                .password(identity.getPasswordHash())
                .authorities("ROLE_" + identity.getUser().getRole().name())
                .build();
    }
}
