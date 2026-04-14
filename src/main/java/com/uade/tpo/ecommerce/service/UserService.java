package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Role;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.entity.dto.UserRequest;
import com.uade.tpo.ecommerce.enums.RoleName;
import com.uade.tpo.ecommerce.exceptions.UserDuplicateException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.RoleRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public User getById(Long id) throws UserNotFoundException {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User create(UserRequest request) throws UserDuplicateException {
        if (repository.existsByUsername(request.username()) || repository.existsByEmail(request.email()))
            throw new UserDuplicateException();

        Role role = roleRepository.findByName(RoleName.valueOf(request.role().toUpperCase()))
            .orElseThrow(() -> new IllegalArgumentException("Role no encontrado: " + request.role()));

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(role);
        return repository.save(user);
    }

    public User update(Long id, UserRequest request) throws UserNotFoundException {
        User existing = repository.findById(id).orElseThrow(UserNotFoundException::new);

        Role role = roleRepository.findByName(RoleName.valueOf(request.role().toUpperCase()))
            .orElseThrow(() -> new IllegalArgumentException("Role no encontrado: " + request.role()));

        existing.setUsername(request.username());
        existing.setEmail(request.email());
        if (request.password() == null || request.password().isBlank()) {
            // conservar contraseña actual
        } else {
            existing.setPassword(passwordEncoder.encode(request.password()));
        }
        existing.setFirstName(request.firstName());
        existing.setLastName(request.lastName());
        existing.setRole(role);
        return repository.save(existing);
    }

    public void delete(Long id) throws UserNotFoundException {
        if (!repository.existsById(id)) throw new UserNotFoundException();
        repository.deleteById(id);
    }
}