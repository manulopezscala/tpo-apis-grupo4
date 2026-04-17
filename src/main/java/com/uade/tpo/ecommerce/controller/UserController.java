package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Role;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.entity.dto.CustomResponse;
import com.uade.tpo.ecommerce.exceptions.RoleNotFoundException;
import com.uade.tpo.ecommerce.exceptions.UserDuplicateException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.RoleRepository;
import com.uade.tpo.ecommerce.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final RoleRepository roleRepository;

    public UserController(UserService service, RoleRepository roleRepository) {
        this.service = service;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/roles")
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Long id) throws UserNotFoundException {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) throws UserDuplicateException, RoleNotFoundException {
        return service.create(user);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User user) throws UserNotFoundException, RoleNotFoundException {
        return service.update(id, user);
    }

    @DeleteMapping("/{id}")
    public CustomResponse delete(@PathVariable Long id) throws UserNotFoundException {
        service.delete(id);
        return new CustomResponse(true, "Usuario eliminado correctamente");
    }
}