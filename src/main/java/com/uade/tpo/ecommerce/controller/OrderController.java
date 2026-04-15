package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.enums.OrderStatus;
import com.uade.tpo.ecommerce.entity.dto.OrderStatusUpdateRequest;
import com.uade.tpo.ecommerce.exceptions.CartAlreadyOrderedException;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.InvalidOrderStatusException;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static java.util.Arrays.stream;

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
    public Order create(@RequestBody Order order)
            throws CartNotFoundException, CartAlreadyOrderedException, UserNotFoundException {
        return service.create(order);
    }

    @PatchMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id, @RequestBody OrderStatusUpdateRequest request)
            throws OrderNotFoundException, InvalidOrderStatusException {
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(request.getStatus());
        } catch (Exception e) {
            String allowed = String.join(", ", stream(OrderStatus.values()).map(Enum::name).toList());
            throw new InvalidOrderStatusException("El status enviado no es válido. Los valores permitidos son: " + allowed);
        }
        return service.updateStatus(id, newStatus);
    }
}
