package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cart> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Cart getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Cart create(@RequestBody Cart cart) {
        return service.create(cart);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
