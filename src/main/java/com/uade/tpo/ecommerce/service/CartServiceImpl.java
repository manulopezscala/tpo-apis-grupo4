package com.uade.tpo.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.repository.CartRepository;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;

    public List<Cart> getCarts() {
        return cartRepository.findAll();
    }

    public Optional<Cart> getCartById(Long cartId) {
        return cartRepository.findById(cartId);
    }

    public Cart createCart(String userId, String currency) {
        String normalizedCurrency = currency;
        if (normalizedCurrency == null || normalizedCurrency.isBlank())
            normalizedCurrency = "ARS";

        Cart cart = new Cart(userId, normalizedCurrency, "active");
        return cartRepository.save(cart);
    }
}
