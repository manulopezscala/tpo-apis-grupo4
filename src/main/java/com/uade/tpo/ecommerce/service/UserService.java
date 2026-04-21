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


/**
 * Servicio para operaciones de negocio sobre usuarios.
 * Permite crear, consultar, actualizar y eliminar usuarios.
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de usuarios
     * @param roleRepository repositorio de roles
     * @param passwordEncoder codificador de contraseñas
     */
    public UserService(UserRepository repository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene todos los usuarios.
     * @return lista de usuarios
     */
    public List<User> getAll() {
        return repository.findAll();
    }

    /**
     * Busca un usuario por su ID.
     * @param id identificador del usuario
     * @return el usuario encontrado
     * @throws UserNotFoundException si no existe el usuario
     */
    public User getById(Long id) throws UserNotFoundException {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    /**
     * Crea un nuevo usuario, validando unicidad y rol.
     * @param user datos del usuario
     * @return el usuario creado
     * @throws UserDuplicateException si ya existe usuario con ese username/email
     * @throws RoleNotFoundException si el rol no existe
     */
    public User create(User user) throws UserDuplicateException, RoleNotFoundException {
        if (repository.existsByUsername(user.getUsername()) || repository.existsByEmail(user.getEmail())) throw new UserDuplicateException();
        if (user.getRole() == null || user.getRole().getId() == null) throw new RoleNotFoundException();
        Role role = roleRepository.findById(user.getRole().getId()).orElseThrow(RoleNotFoundException::new);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }

    /**
     * Actualiza un usuario existente.
     * @param id identificador del usuario
     * @param user datos actualizados
     * @return el usuario actualizado
     * @throws UserNotFoundException si el usuario no existe
     * @throws RoleNotFoundException si el rol no existe
     */
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

    /**
     * Elimina un usuario por su ID.
     * @param id identificador del usuario
     * @throws UserNotFoundException si el usuario no existe
     */
    public void delete(Long id) throws UserNotFoundException {
        if (!repository.existsById(id)) throw new UserNotFoundException();
        repository.deleteById(id);
    }
}