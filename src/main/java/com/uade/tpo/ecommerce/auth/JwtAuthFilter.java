package com.uade.tpo.ecommerce.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * Servicio responsable de parsear y validar JWT.
     */
    private final JwtService jwtService;

    /**
     * Servicio de usuarios usado para cargar authorities en el contexto de seguridad.
     */
    private final UserDetailsService userDetailsService;

    /**
     * Crea el filtro JWT con sus dependencias.
     */
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Intercepta requests para validar Bearer token y poblar SecurityContextHolder.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // Si no hay header o no es Bearer, delegamos al siguiente filtro sin hacer nada.
        // Esto permite que endpoints públicos funcionen sin autenticación.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token JWT del header
        String jwt = authHeader.substring(7);

        // Intentamos extraer el username del token. Si falla, delegamos al siguiente filtro sin autenticación.
        String username;
        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception ex) {
            filterChain.doFilter(request, response);
            return;
        }

        // Si el username es válido y no hay una autenticación ya presente en el contexto, validamos el token.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Cargamos los detalles del usuario para validar el token y poblar authorities.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Si el token es válido, creamos un objeto de autenticación y lo seteamos en el contexto de seguridad.
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Creamos un token de autenticación con los detalles del usuario y sus authorities.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                // Seteamos detalles adicionales de la request (como IP, session ID, etc.) en el token de autenticación.
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Finalmente, seteamos el token de autenticación en el contexto de seguridad para que esté disponible en toda la request.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuamos con la cadena de filtros, ya sea que hayamos autenticado al usuario o no.
        filterChain.doFilter(request, response);
    }
}
