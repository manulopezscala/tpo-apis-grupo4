package com.uade.tpo.ecommerce.controllers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.dto.CartRequest;
import com.uade.tpo.ecommerce.service.CartService;

@RestController
@RequestMapping("carts")
public class CartsController {
    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<ArrayList<Cart>> getCarts() {
        return ResponseEntity.ok(cartService.getCarts());
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable int cartId) {
        Optional<Cart> result = cartService.getCartById(cartId);

        if (result.isPresent())
            return ResponseEntity.ok(result.get());

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Cart> createCart(@RequestBody CartRequest cartRequest) {
        Cart cart = cartService.createCart(cartRequest.getUserId(), cartRequest.getCurrency());
        return ResponseEntity.created(URI.create("/carts/" + cart.getId())).body(cart);
    }
}
