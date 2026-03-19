package com.uade.tpo.ecommerce.service;

import java.util.ArrayList;
import java.util.Optional;

import com.uade.tpo.ecommerce.entity.Cart;

public interface CartService {
    ArrayList<Cart> getCarts();

    Optional<Cart> getCartById(int cartId);

    Cart createCart(String userId, String currency);
}
