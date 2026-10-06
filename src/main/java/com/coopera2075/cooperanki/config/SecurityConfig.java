package com.coopera2075.cooperanki.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.coopera2075.cooperanki.security.CustomUserDetailsService;

@Configuration 
@EnableWebSecurity 
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            //Deshabilitar CSRF (no required para API REST stateless)
            .csrf(csrf -> csrf.disable())

            // Configurar CORS globalmente
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Deshabilitar manejo de sesiones (stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configuración inicial de rutas
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Permitir acceso a rutas de autenticación
                .anyRequest().authenticated() // Requerir autenticación para cualquier otra ruta
            );

        return http.build();
    }

    @Bean 
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Dominios del cliente React (Vite: 5173 / CRA: 3000)
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); 

        //Metódos HTTP allowed
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); 

        //Cabeceras allowed (incluir autorización si se usa JWT)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); 

        // Permitir credenciales (cookies, headers de autenticación, etc.)
        configuration.setAllowCredentials(true); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplicar configuración a todas las rutas
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean 
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
