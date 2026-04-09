package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.enums.RoleName;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Servicio de autenticación que resuelve usuarios desde la base de datos.
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    /**
     * Carga un usuario por username y lo adapta al modelo de Spring Security.
     *
     * @param username nombre de usuario recibido en el login
     * @return usuario con credenciales y authorities para el contexto de seguridad
     * @throws UsernameNotFoundException si el usuario no existe en la base de datos
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        RoleName roleName = user.getRole() != null && user.getRole().getName() != null
            ? user.getRole().getName()
            : RoleName.USER;

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + roleName.name()))
        );
    }
}
