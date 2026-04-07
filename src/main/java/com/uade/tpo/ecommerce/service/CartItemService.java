package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.exceptions.CartItemNotFoundException;
import com.uade.tpo.ecommerce.exceptions.InsufficientStockException;
import com.uade.tpo.ecommerce.repository.CartItemRepository;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    private final CartItemRepository repository;

    public CartItemService(CartItemRepository repository) {
        this.repository = repository;
    }

    public CartItem create(CartItem item) throws InsufficientStockException {
        int available = item.getProduct().getStock();
        if (item.getQuantity() > available) throw new InsufficientStockException();
        return repository.save(item);
    }

    public void delete(Long id) throws CartItemNotFoundException {
        if (!repository.existsById(id)) throw new CartItemNotFoundException();
        repository.deleteById(id);
    }
}