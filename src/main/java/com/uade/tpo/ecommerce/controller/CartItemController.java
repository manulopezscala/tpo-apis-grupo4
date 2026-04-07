package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.exceptions.CartItemNotFoundException;
import com.uade.tpo.ecommerce.exceptions.InsufficientStockException;
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
    public CartItem create(@RequestBody CartItem item) throws InsufficientStockException {
        return service.create(item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws CartItemNotFoundException {
        service.delete(id);
    }
}
