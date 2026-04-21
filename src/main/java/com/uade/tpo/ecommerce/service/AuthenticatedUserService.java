package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio utilitario para resolver el usuario autenticado desde el contexto de seguridad.
 */
@Service
public class AuthenticatedUserService {

    private final UserRepository userRepository;

    /**
     * Constructor con inyección de dependencias.
     * @param userRepository repositorio de usuarios
     */
    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Obtiene la entidad de usuario actualmente autenticada.
     * @return usuario autenticado persistido en base de datos
     * @throws IllegalStateException si no existe el usuario autenticado en base de datos
     */
    public User getCurrentUser() {
        String username = getCurrentUsername();
        return userRepository.findByUsername(username).orElseThrow(() -> new IllegalStateException("El usuario autenticado no existe en la base de datos"));
    }

    /**
     * Obtiene el identificador del usuario autenticado.
     * @return id del usuario autenticado
     */
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     * Indica si el usuario autenticado posee rol administrador.
     * @return true si tiene el authority ROLE_ADMIN, false en caso contrario
     */
    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) return false;
        return authentication.getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    /**
     * Obtiene el username del principal autenticado en el contexto de seguridad.
     * @return username autenticado
     * @throws IllegalStateException si no hay autenticación válida en el contexto
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalStateException("No hay un usuario autenticado en el contexto de seguridad");
        }
        return authentication.getName();
    }
}