package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.enums.CartStatus;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.EmptyCartException;
import com.uade.tpo.ecommerce.exceptions.InvalidCartStatusException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository repository;
    private final UserRepository userRepository;

    public CartService(CartRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Cart> getAll() {
        return repository.findAll();
    }

    public Cart getById(Long id) throws CartNotFoundException {
        return repository.findById(id).orElseThrow(CartNotFoundException::new);
    }

    public Cart create(Cart cart) throws UserNotFoundException {
        if (cart.getUser() == null || cart.getUser().getId() == null) {
            throw new IllegalArgumentException("Debe informar un user.id válido para crear el carrito");
        }

        User user = userRepository.findById(cart.getUser().getId()).orElseThrow(UserNotFoundException::new);
        cart.setUser(user);
        cart.setStatus(CartStatus.ACTIVE);
        return repository.save(cart);
    }

    public Cart checkout(Long id) throws CartNotFoundException, InvalidCartStatusException, EmptyCartException {
        Cart cart = getById(id);
        if (cart.getStatus() != CartStatus.ACTIVE) throw new InvalidCartStatusException();
        if (cart.getItems() == null || cart.getItems().isEmpty()) throw new EmptyCartException();
        cart.setStatus(CartStatus.COMPLETED);
        return repository.save(cart);
    }

    public void delete(Long id) throws CartNotFoundException {
        if (!repository.existsById(id)) throw new CartNotFoundException();
        repository.deleteById(id);
    }
}