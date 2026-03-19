package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Cart;

public interface CartService {
    Cart createCart(String userId, String currency);
}
