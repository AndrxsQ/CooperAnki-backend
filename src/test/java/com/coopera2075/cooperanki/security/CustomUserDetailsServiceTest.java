package com.coopera2075.cooperanki.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.coopera2075.cooperanki.entity.AppUser;
import com.coopera2075.cooperanki.entity.AuthIdentity;
import com.coopera2075.cooperanki.entity.UserRole;
import com.coopera2075.cooperanki.repository.AuthIdentityRepository;
import com.coopera2075.modules.auth.Permission;
import com.coopera2075.modules.auth.Role;
import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private AuthIdentityRepository authIdentityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadsExistingEconomicUnitsAccountsByUsernameOrEmail() {
        AuthIdentity identity = economicUnitsIdentity();
        when(authIdentityRepository.findAllByLoginIdentifierWithUser("legacy@example.com"))
                .thenReturn(List.of(identity));
        when(userRepository.findByEmailWithRoleAndPermissions("legacy@example.com"))
                .thenReturn(Optional.empty());

        UserDetails user = userDetailsService.loadUserByUsername("legacy@example.com");

        assertEquals("legacy", user.getUsername());
        assertTrue(user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadsCooperaAccountsAndTheirRolePermissions() {
        User account = cooperaUser();
        when(userRepository.findByEmailWithRoleAndPermissions("person@example.com"))
                .thenReturn(Optional.of(account));

        UserDetails user = userDetailsService.loadUserByUsername("coopera:person@example.com");

        assertEquals("coopera:person@example.com", user.getUsername());
        assertTrue(user.isEnabled());
        assertTrue(user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_TERRITORIAL_TRAINER")));
        assertTrue(user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("PERM_SYSTEM_COOPERA_ACCESS")));
        verifyNoInteractions(authIdentityRepository);
    }

    @Test
    void rejectsAnIdentifierThatMatchesAccountsInBothSystems() {
        when(authIdentityRepository.findAllByLoginIdentifierWithUser("person@example.com"))
                .thenReturn(List.of(economicUnitsIdentity()));
        when(userRepository.findByEmailWithRoleAndPermissions("person@example.com"))
                .thenReturn(Optional.of(cooperaUser()));

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("person@example.com"));
    }

    private AuthIdentity economicUnitsIdentity() {
        AppUser appUser = new AppUser();
        appUser.setId(7);
        appUser.setName("Legacy User");
        appUser.setRole(UserRole.ADMIN);

        AuthIdentity identity = new AuthIdentity();
        identity.setUser(appUser);
        identity.setUsername("legacy");
        identity.setEmail("legacy@example.com");
        identity.setPasswordHash("encoded-password");
        return identity;
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
