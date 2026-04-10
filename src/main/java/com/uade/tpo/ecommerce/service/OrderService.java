package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.enums.OrderStatus;
import com.uade.tpo.ecommerce.exceptions.CartAlreadyOrderedException;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.InvalidOrderStatusException;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository repository, CartRepository cartRepository) {
        this.repository = repository;
        this.cartRepository = cartRepository;
    }

    public List<Order> getAll() {
        return repository.findAll();
    }

    public Order getById(Long id) throws OrderNotFoundException {
        return repository.findById(id).orElseThrow(OrderNotFoundException::new);
    }

    public Order create(Order order) throws CartNotFoundException, CartAlreadyOrderedException {
        if (order.getCart() == null || order.getCart().getId() == null) {
            throw new CartNotFoundException();
        }

        Long cartId = order.getCart().getId();
        if (!cartRepository.existsById(cartId)) {
            throw new CartNotFoundException();
        }
        if (repository.existsByCartId(cartId)) {
            throw new CartAlreadyOrderedException();
        }

        order.setCart(cartRepository.getReferenceById(cartId));
        order.setDate(new Date());
        order.setStatus(OrderStatus.CREATED);
        return repository.save(order);
    }

    public Order updateStatus(Long id, OrderStatus newStatus) throws OrderNotFoundException, InvalidOrderStatusException {
        Order order = getById(id);
        if (!isValidTransition(order.getStatus(), newStatus)) throw new InvalidOrderStatusException();
        order.setStatus(newStatus);
        return repository.save(order);
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case CREATED -> next == OrderStatus.PENDING || next == OrderStatus.CANCELLED;
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            default -> false;
        };
    }
}