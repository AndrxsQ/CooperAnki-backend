package com.coopera2075.cooperanki.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final String COOPERA_SUBJECT_PREFIX = "coopera:";

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        String email = identifier.startsWith(COOPERA_SUBJECT_PREFIX)
                ? identifier.substring(COOPERA_SUBJECT_PREFIX.length())
                : identifier;
        return userRepository.findByEmailWithRoleAndPermissions(email)
                .map(this::toCooperaUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));
    }

    private UserDetails toCooperaUserDetails(User account) {
        List<String> authorities = new ArrayList<>();
        authorities.add("ROLE_" + account.getRole().getName());
        account.getRole().getPermissions().stream()
                .map(permission -> "PERM_" + permission.getCode())
                .forEach(authorities::add);

        return org.springframework.security.core.userdetails.User.withUsername(
                        COOPERA_SUBJECT_PREFIX + account.getEmail())
                .password(account.getPasswordHash())
                .authorities(authorities.toArray(String[]::new))
                .disabled(!account.isActive())
                .build();
    }
}
