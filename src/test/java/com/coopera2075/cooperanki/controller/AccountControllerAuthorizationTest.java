package com.coopera2075.cooperanki.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.coopera2075.cooperanki.service.AccountService;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AccountControllerAuthorizationTest.TestConfiguration.class)
class AccountControllerAuthorizationTest {
    @Autowired
    private AccountController accountController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void permitsClientWithPersonalAccountSubsystem() {
        authenticateAs("ROLE_CLIENT", "PERM_SYSTEM_PERSONAL_ACCOUNT_ACCESS");

        assertEquals(
                HttpStatus.OK,
                accountController.getAccount(SecurityContextHolder.getContext().getAuthentication()).getStatusCode());
    }

    @Test
    void deniesCooperationOnlyRoleFromPersonalAccountSubsystem() {
        authenticateAs("ROLE_GENERAL_EXE", "PERM_SYSTEM_COOPERATION_ACCESS");

        assertThrows(AccessDeniedException.class, () -> accountController.getAccount(null));
    }

    private void authenticateAs(String... authorities) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test-user",
                        "password",
                        java.util.Arrays.stream(authorities)
                                .map(SimpleGrantedAuthority::new)
                                .toList()));
    }

    @Configuration
    @EnableMethodSecurity
    static class TestConfiguration {
        @Bean
        AccountService accountService() {
            return mock(AccountService.class);
        }

        @Bean
        AccountController accountController(AccountService accountService) {
            return new AccountController(accountService);
        }
    }
}
