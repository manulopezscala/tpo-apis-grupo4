package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.OrderItem;
import com.uade.tpo.ecommerce.entity.dto.CustomResponse;
import com.uade.tpo.ecommerce.exceptions.InsufficientStockException;
import com.uade.tpo.ecommerce.exceptions.InvalidOrderStatusException;
import com.uade.tpo.ecommerce.exceptions.OrderItemNotFoundException;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService service;

    public OrderItemController(OrderItemService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItem create(@RequestBody OrderItem item)
            throws OrderNotFoundException, ProductNotFoundException, InvalidOrderStatusException, InsufficientStockException {
        return service.create(item);
    }

    @DeleteMapping("/{id}")
    public CustomResponse delete(@PathVariable Long id) throws OrderItemNotFoundException {
        service.delete(id);
        return new CustomResponse(true, "Item de la orden eliminado correctamente");
    }
}
