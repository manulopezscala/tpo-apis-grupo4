package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.entity.dto.CustomResponse;
import com.uade.tpo.ecommerce.exceptions.CartItemNotFoundException;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.InsufficientStockException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart-items")
public class CartItemController {

    private final CartItemService service;

    public CartItemController(CartItemService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartItem create(@RequestBody CartItem item)
            throws InsufficientStockException, CartNotFoundException, ProductNotFoundException {
        return service.create(item);
    }

    @DeleteMapping("/{id}")
    public CustomResponse delete(@PathVariable Long id) throws CartItemNotFoundException {
        service.delete(id);
        return new CustomResponse(true, "Item del carrito eliminado correctamente");
    }
}
