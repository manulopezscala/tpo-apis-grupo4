package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Role;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.exceptions.RoleNotFoundException;
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

    public User create(User user) throws UserDuplicateException, RoleNotFoundException {
        if (repository.existsByUsername(user.getUsername()) || repository.existsByEmail(user.getEmail()))
            throw new UserDuplicateException();
        if (user.getRole() == null || user.getRole().getId() == null) throw new RoleNotFoundException();
        Role role = roleRepository.findById(user.getRole().getId()).orElseThrow(RoleNotFoundException::new);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    public User update(Long id, User user) throws UserNotFoundException, RoleNotFoundException {
        User existing = repository.findById(id).orElseThrow(UserNotFoundException::new);
        if (user.getRole() == null || user.getRole().getId() == null) throw new RoleNotFoundException();
        Role role = roleRepository.findById(user.getRole().getId()).orElseThrow(RoleNotFoundException::new);
        user.setRole(role);
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user.setId(id);
        return repository.save(user);
    }

    public void delete(Long id) throws UserNotFoundException {
        if (!repository.existsById(id)) throw new UserNotFoundException();
        repository.deleteById(id);
    }
}