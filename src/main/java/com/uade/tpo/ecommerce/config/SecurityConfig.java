package com.uade.tpo.ecommerce.config;

import com.uade.tpo.ecommerce.auth.JwtAuthFilter;
import com.uade.tpo.ecommerce.enums.RoleName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    /**
     * Define la cadena principal de filtros de seguridad para la API.
     *
     * @param http builder de configuración de seguridad HTTP
     * @param jwtAuthFilter filtro JWT que valida el bearer token
     * @return configuración de seguridad construida
     * @throws Exception si ocurre un error durante la construcción del filtro
     */
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**").hasAnyRole(RoleName.USER.name(), RoleName.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/products/**", "/categories/**", "/discounts/**", "/product-images/**").hasRole(RoleName.ADMIN.name())
                .requestMatchers(HttpMethod.PUT, "/products/**", "/users/**").hasRole(RoleName.ADMIN.name())
                .requestMatchers(HttpMethod.PATCH, "/orders/**").hasRole(RoleName.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/products/**", "/categories/**", "/users/**").hasRole(RoleName.ADMIN.name())
                .requestMatchers("/users/**").hasRole(RoleName.ADMIN.name())
                .requestMatchers("/carts/**", "/cart-items/**", "/orders/**", "/order-items/**").hasAnyRole(RoleName.USER.name(), RoleName.ADMIN.name())
                .anyRequest().authenticated()
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    /**
     * Expone el AuthenticationManager para usarlo en el flujo de login.
     *
     * @param configuration configuración de autenticación de Spring Security
     * @return manager de autenticación configurado por Spring
     * @throws Exception si no puede resolverse el manager
     */
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    /**
     * Configura el encoder de contraseñas usado por la aplicación.
     *
     * @return implementación BCrypt para hash de contraseñas
     */
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
