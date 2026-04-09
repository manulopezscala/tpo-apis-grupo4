package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.enums.OrderStatus;
import com.uade.tpo.ecommerce.exceptions.InvalidOrderStatusException;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public List<Order> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Order getById(@PathVariable Long id) throws OrderNotFoundException {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody Order order) {
        return service.create(order);
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestParam OrderStatus status)
            throws OrderNotFoundException, InvalidOrderStatusException {
        return service.updateStatus(id, status);
    }
}
