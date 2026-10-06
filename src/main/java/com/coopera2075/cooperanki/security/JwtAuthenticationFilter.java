package com.coopera2075.cooperanki.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component 
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    // Inyección de dependencias a través del constructor
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, 
        @NonNull HttpServletResponse response, 
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Extraer el token JWT del encabezado de la solicitud
        String token = getJwtFromRequest(request);

        // Validar que sea un token válido y no nulo antes de proceder con la autenticación
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // Extraer el nombre de usuario del token
            String username = jwtTokenProvider.getUsernameFromToken(token);

            try {
                // Cargar los detalles del usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Crear un objeto de autenticación con los detalles del usuario
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );

                // Establecer detalles adicionales de la solicitud en el objeto de autenticación
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Establecer la autenticación en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } catch (UsernameNotFoundException e) {
                // Token válido de un usuario que ya no existe: la petición continúa sin autenticar
                SecurityContextHolder.clearContext();
            }
        }

        // Sin token (o con uno inválido) la petición sigue como anónima: las rutas públicas responden
        // y las protegidas terminan en 401 a través del AuthenticationEntryPoint
        filterChain.doFilter(request, response);
    }

    // Extraer Bearer token del encabezado de autorización
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Verificar si el encabezado contiene el prefijo "Bearer " y extraer el token
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remueve "Bearer " del token
        }
        return null;
    }

}
