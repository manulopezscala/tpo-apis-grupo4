package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.enums.CartStatus;
import com.uade.tpo.ecommerce.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartRepository repository;

    public CartService(CartRepository repository) {
        this.repository = repository;
    }

    public List<Cart> getAll() {
        return repository.findAll();
    }

    public Cart getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Cart create(Cart cart) {
        cart.setStatus(CartStatus.ACTIVE);
        return repository.save(cart);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}