package com.coopera2075.modules.matching;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MatchingControllerAuthorizationTest.TestConfiguration.class)
class MatchingControllerAuthorizationTest {
    @Autowired
    private MatchingController matchingController;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void preventsUsersWithoutCooperationSubsystemFromReadingRecommendations() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test-user",
                        "password",
                        java.util.List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN"),
                                new SimpleGrantedAuthority("PERM_SYSTEM_INFORMATION_MANAGEMENT_ACCESS"))));

        assertThrows(
                AccessDeniedException.class,
                () -> matchingController.getRecommendations(1L, null, null, null, null, 0, 20));
    }

    @Test
    void allowsGeneralExecutivesWithCooperationAccessToReadRecommendations() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "test-user",
                        "password",
                        java.util.List.of(
                                new SimpleGrantedAuthority("ROLE_GENERAL_EXE"),
                                new SimpleGrantedAuthority("PERM_SYSTEM_COOPERATION_ACCESS"))));

        org.junit.jupiter.api.Assertions.assertEquals(
                org.springframework.http.HttpStatus.OK,
                matchingController.getRecommendations(1L, null, null, null, null, 0, 20).getStatusCode());
    }

    @Configuration
    @EnableMethodSecurity
    static class TestConfiguration {
        @Bean
        MatchingService matchingService() {
            return mock(MatchingService.class);
        }

        @Bean
        MatchingController matchingController(MatchingService matchingService) {
            return new MatchingController(matchingService);
        }
    }
}
