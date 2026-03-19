package com.uade.tpo.ecommerce.repository;

import java.util.ArrayList;

import com.uade.tpo.ecommerce.entity.Cart;

public class CartRepository {
    private ArrayList<Cart> carts;
    private int currentId;

    public CartRepository() {
        this.carts = new ArrayList<Cart>();
        this.currentId = 1;
    }

    public Cart createCart(String userId, String currency) {
        Cart cart = Cart.builder()
                .id(currentId++)
                .userId(userId)
                .currency(currency)
                .status("active")
                .build();

        carts.add(cart);
        return cart;
    }
}
