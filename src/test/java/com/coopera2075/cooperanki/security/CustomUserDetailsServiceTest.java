package com.coopera2075.cooperanki.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import com.coopera2075.modules.auth.Permission;
import com.coopera2075.modules.auth.Role;
import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadsUnifiedAccountByEmailAndIncludesItsRolePermissions() {
        User account = cooperaUser();
        when(userRepository.findByEmailWithRoleAndPermissions("person@example.com"))
                .thenReturn(Optional.of(account));

        UserDetails user = userDetailsService.loadUserByUsername("person@example.com");

        assertEquals("coopera:person@example.com", user.getUsername());
        assertTrue(user.isEnabled());
        assertTrue(user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_TERRITORIAL_TRAINER")));
        assertTrue(user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("PERM_SYSTEM_COOPERA_ACCESS")));
    }

    @Test
    void loadsUnifiedAccountFromTokenSubject() {
        when(userRepository.findByEmailWithRoleAndPermissions("person@example.com"))
                .thenReturn(Optional.of(cooperaUser()));

        UserDetails user = userDetailsService.loadUserByUsername("coopera:person@example.com");

        assertEquals("coopera:person@example.com", user.getUsername());
    }

    private User cooperaUser() {
        Permission permission = new Permission();
        permission.setCode("SYSTEM_COOPERA_ACCESS");

        Role role = new Role();
        role.setName("TERRITORIAL_TRAINER");
        role.setPermissions(java.util.Set.of(permission));

        User account = new User();
        account.setEmail("person@example.com");
        account.setPasswordHash("encoded-password");
        account.setRole(role);
        account.setActive(true);
        return account;
    }
}
