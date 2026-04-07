package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.exceptions.UserDuplicateException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAll() {
        return repository.findAll();
    }

    public User getById(Long id) throws UserNotFoundException {
        return repository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User create(User user) throws UserDuplicateException {
        if (repository.existsByUsername(user.getUsername()) || repository.existsByEmail(user.getEmail()))
            throw new UserDuplicateException();
        return repository.save(user);
    }

    public User update(Long id, User user) throws UserNotFoundException {
        if (!repository.existsById(id)) throw new UserNotFoundException();
        user.setId(id);
        return repository.save(user);
    }

    public void delete(Long id) throws UserNotFoundException {
        if (!repository.existsById(id)) throw new UserNotFoundException();
        repository.deleteById(id);
    }
}