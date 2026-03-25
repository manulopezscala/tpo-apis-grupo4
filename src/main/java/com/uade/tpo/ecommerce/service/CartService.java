package com.uade.tpo.ecommerce.service;

import java.util.List;
import java.util.Optional;

import com.uade.tpo.ecommerce.entity.Cart;

public interface CartService {
    List<Cart> getCarts();

    Optional<Cart> getCartById(Long cartId);

    Cart createCart(String userId, String currency);
}
