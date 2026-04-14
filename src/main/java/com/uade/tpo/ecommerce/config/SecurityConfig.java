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

    /**
     * Define la cadena principal de filtros de seguridad para la API.
     *
     * @param http builder de configuración de seguridad HTTP
     * @param jwtAuthFilter filtro JWT que valida el bearer token
     * @return configuración de seguridad construida
     * @throws Exception si ocurre un error durante la construcción del filtro
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            // Deshabilitamos CSRF ya que nuestra API es sin estado y no usamos cookies para autenticación
            .csrf(AbstractHttpConfigurer::disable)
            // Configuramos la política de creación de sesiones para que sea sin estado, ya que usaremos JWT para autenticación
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Definimos las reglas de autorización para los endpoints de la API según los roles de usuario
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
            // Deshabilitamos autenticación básica y formulario de login, ya que usamos JWT para autenticación sin estado
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            // Agregamos el filtro de autenticación JWT antes del filtro de autenticación por nombre de usuario y contraseña
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Finalmente, construimos y retornamos la configuración de seguridad
        return http.build();
    }

    /**
     * Expone el AuthenticationManager para usarlo en el flujo de login.
     *
     * @param configuration configuración de autenticación de Spring Security
     * @return manager de autenticación configurado por Spring
     * @throws Exception si no puede resolverse el manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configura el encoder de contraseñas usado por la aplicación.
     *
     * @return implementación BCrypt para hash de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
