package com.coopera2075.cooperanki.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coopera2075.cooperanki.entity.AuthIdentity;
import com.coopera2075.cooperanki.repository.AuthIdentityRepository;
import com.coopera2075.modules.auth.User;
import com.coopera2075.modules.auth.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final String COOPERA_SUBJECT_PREFIX = "coopera:";
    private static final String ECONOMIC_UNITS_SUBJECT_PREFIX = "economic-units:";

    private final AuthIdentityRepository authIdentityRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if (identifier.startsWith(COOPERA_SUBJECT_PREFIX)) {
            return loadCooperaUser(identifier.substring(COOPERA_SUBJECT_PREFIX.length()));
        }
        if (identifier.startsWith(ECONOMIC_UNITS_SUBJECT_PREFIX)) {
            return loadEconomicUnitsUser(identifier.substring(ECONOMIC_UNITS_SUBJECT_PREFIX.length()));
        }

        List<AuthIdentity> economicUnitsMatches =
                authIdentityRepository.findAllByLoginIdentifierWithUser(identifier);
        User cooperaMatch = userRepository.findByEmailWithRoleAndPermissions(identifier).orElse(null);

        if (economicUnitsMatches.size() + (cooperaMatch == null ? 0 : 1) != 1) {
            throw new UsernameNotFoundException("Credenciales incorrectas");
        }
        if (cooperaMatch != null) {
            return toCooperaUserDetails(cooperaMatch);
        }
        return toEconomicUnitsUserDetails(economicUnitsMatches.get(0));
    }

    private UserDetails loadCooperaUser(String email) {
        return userRepository.findByEmailWithRoleAndPermissions(email)
                .map(this::toCooperaUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Credenciales incorrectas"));
    }

    private UserDetails loadEconomicUnitsUser(String identifier) {
        List<AuthIdentity> identities =
                authIdentityRepository.findAllByLoginIdentifierWithUser(identifier);
        if (identities.size() != 1) {
            throw new UsernameNotFoundException("Credenciales incorrectas");
        }
        return toEconomicUnitsUserDetails(identities.get(0));
    }

    private UserDetails toEconomicUnitsUserDetails(AuthIdentity identity) {
        return org.springframework.security.core.userdetails.User.withUsername(identity.getUsername())
                .password(identity.getPasswordHash())
                .authorities("ROLE_" + identity.getUser().getRole().name())
                .build();
    }

    private UserDetails toCooperaUserDetails(User account) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole().getName()));
        account.getRole().getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority("PERM_" + permission.getCode()))
                .forEach(authorities::add);

        return org.springframework.security.core.userdetails.User.withUsername(
                        COOPERA_SUBJECT_PREFIX + account.getEmail())
                .password(account.getPasswordHash())
                .authorities(authorities)
                .disabled(!account.isActive())
                .build();
    }
}
