package com.uade.tpo.ecommerce.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.repository.CartRepository;

@Service
public class CartServiceImpl implements CartService {
    private CartRepository cartRepository;

    public CartServiceImpl() {
        this.cartRepository = new CartRepository();
    }

    public ArrayList<Cart> getCarts() {
        return cartRepository.getCarts();
    }

    public Optional<Cart> getCartById(int cartId) {
        return cartRepository.getCartById(cartId);
    }

    public Cart createCart(String userId, String currency) {
        String normalizedCurrency = currency;
        if (normalizedCurrency == null || normalizedCurrency.isBlank())
            normalizedCurrency = "ARS";

        return cartRepository.createCart(userId, normalizedCurrency);
    }
}
