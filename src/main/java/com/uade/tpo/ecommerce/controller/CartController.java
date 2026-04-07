package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.EmptyCartException;
import com.uade.tpo.ecommerce.exceptions.InvalidCartStatusException;
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
    public Cart getById(@PathVariable Long id) throws CartNotFoundException {
        return service.getById(id);
    }

    @PostMapping
    public Cart create(@RequestBody Cart cart) {
        return service.create(cart);
    }

    @PostMapping("/{id}/checkout")
    public Cart checkout(@PathVariable Long id) throws CartNotFoundException, InvalidCartStatusException, EmptyCartException {
        return service.checkout(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws CartNotFoundException {
        service.delete(id);
    }
}
