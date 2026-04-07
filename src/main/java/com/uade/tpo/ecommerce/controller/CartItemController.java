package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.service.CartItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart-items")
public class CartItemController {

    private final CartItemService service;

    public CartItemController(CartItemService service) {
        this.service = service;
    }

    @PostMapping
    public CartItem create(@RequestBody CartItem item) {
        return service.create(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
