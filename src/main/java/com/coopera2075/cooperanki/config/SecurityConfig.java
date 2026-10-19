// package com.coopera2075.cooperanki.config;

// import java.util.List;

// import org.springframework.boot.web.servlet.FilterRegistrationBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// import com.coopera2075.cooperanki.security.CustomUserDetailsService;
// import com.coopera2075.cooperanki.security.JwtAuthenticationFilter;
// import com.coopera2075.cooperanki.security.RestSecurityErrorHandler;

// import jakarta.servlet.DispatcherType;

// @Configuration 
// @EnableWebSecurity 
// @EnableMethodSecurity // Habilita @PreAuthorize en los controladores para las reglas por rol
// public class SecurityConfig {
//     private final CustomUserDetailsService userDetailsService;
//     private final PasswordEncoder passwordEncoder;
    
//     public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//         this.userDetailsService = userDetailsService;
//         this.passwordEncoder = passwordEncoder;
//     }

//     @Bean 
//     public SecurityFilterChain securityFilterChain(
//         HttpSecurity http,
//         JwtAuthenticationFilter jwtAuthenticationFilter,
//         RestSecurityErrorHandler securityErrorHandler
//     ) throws Exception {
//         http
//             //Deshabilitar CSRF (no required para API REST stateless)
//             .csrf(csrf -> csrf.disable())

//             // Configurar CORS globalmente
//             .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
//             // Deshabilitar manejo de sesiones (stateless)
//             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
//             // Responder 401/403 en JSON en lugar del 403 vacío por defecto
//             .exceptionHandling(exceptions -> exceptions
//                 .authenticationEntryPoint(securityErrorHandler)
//                 .accessDeniedHandler(securityErrorHandler)
//             )

//             // Configuración inicial de rutas
//             .authorizeHttpRequests(auth -> auth
//                 // El reenvío interno a /error no debe exigir autenticación, o cualquier fallo se convierte en 403
//                 .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
//                 // Permitir acceso a registro y login (/api/v1/auth/me sigue protegido)
//                 .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()
//                 // Consulta pública de proyectos y convocatorias (páginas públicas del cliente)
//                 .requestMatchers(HttpMethod.GET,
//                     "/api/v1/projects", "/api/v1/projects/*",
//                     "/api/v1/calls", "/api/v1/calls/*").permitAll()
//                 .anyRequest().authenticated() // Requerir autenticación para cualquier otra ruta
//             )

//             // Validar el JWT de cada petición antes de que se evalúen las reglas de autorización
//             .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }

//     // El filtro JWT es un @Component, así que Spring Boot también lo registraría como filtro del servlet
//     // fuera de la cadena de seguridad. Se desactiva ese registro para que solo se ejecute dentro de ella.
//     @Bean
//     public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(JwtAuthenticationFilter jwtAuthenticationFilter) {
//         FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(jwtAuthenticationFilter);
//         registration.setEnabled(false);
//         return registration;
//     }

//     @Bean 
//     public CorsConfigurationSource corsConfigurationSource() {
//         CorsConfiguration configuration = new CorsConfiguration();
//         // Dominios del cliente React (Vite: 5173 / CRA: 3000)
//         configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); 

//         //Metódos HTTP allowed
//         configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); 

//         //Cabeceras allowed (incluir autorización si se usa JWT)
//         configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); 

//         // Permitir credenciales (cookies, headers de autenticación, etc.)
//         configuration.setAllowCredentials(true); 

//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", configuration); // Aplicar configuración a todas las rutas
//         return source;
//     }

//     @Bean
//     public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//         return authenticationConfiguration.getAuthenticationManager();
//     }

//     @Bean 
//     public DaoAuthenticationProvider authenticationProvider() {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
//         authProvider.setPasswordEncoder(passwordEncoder);
//         return authProvider;
//     }
// }
