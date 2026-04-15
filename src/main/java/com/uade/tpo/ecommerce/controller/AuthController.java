package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.dto.AuthRequest;
import com.uade.tpo.ecommerce.entity.dto.AuthResponse;
import com.uade.tpo.ecommerce.auth.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Manager central de autenticación de Spring Security.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Servicio de emisión y validación de JWT.
     */
    private final JwtService jwtService;

    /**
     * Controlador de autenticación para el endpoint de login.
     */
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * Autentica usuario y contraseña, y retorna un JWT si las credenciales son válidas.
     *
     * @param request credenciales del usuario
     * @return token JWT o 401 si falla la autenticación
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
