package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.repository.CartItemRepository;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    private final CartItemRepository repository;

    public CartItemService(CartItemRepository repository) {
        this.repository = repository;
    }

    public CartItem create(CartItem item) {
        return repository.save(item);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}