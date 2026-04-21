package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.entity.dto.CustomResponse;
import com.uade.tpo.ecommerce.exceptions.ActiveCartAlreadyExistsException;
import com.uade.tpo.ecommerce.exceptions.CartAlreadyOrderedException;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.EmptyCartException;
import com.uade.tpo.ecommerce.exceptions.InvalidCartStatusException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.service.CartService;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public Cart create(@RequestBody Cart cart) throws UserNotFoundException, ActiveCartAlreadyExistsException {
        return service.create(cart);
    }

    @PostMapping("/{id}/checkout")
    public Cart checkout(@PathVariable Long id) throws CartNotFoundException, InvalidCartStatusException, EmptyCartException, CartAlreadyOrderedException {
        return service.checkout(id);
    }

    @GetMapping("/{id}/items")
    public List<CartItem> getCartItems(@PathVariable Long id) throws CartNotFoundException {
        return service.getCartItems(id);
    }

    @DeleteMapping("/{id}")
    public CustomResponse delete(@PathVariable Long id) throws CartNotFoundException, InvalidCartStatusException {
        service.delete(id);
        return new CustomResponse(true, "Carrito eliminado correctamente");
    }
}
