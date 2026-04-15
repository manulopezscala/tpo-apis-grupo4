package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.enums.OrderStatus;
import com.uade.tpo.ecommerce.exceptions.CartAlreadyOrderedException;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.InvalidOrderStatusException;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.OrderRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository repository, CartRepository cartRepository, UserRepository userRepository) {
        this.repository = repository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    public List<Order> getAll() {
        return repository.findAll();
    }

    public Order getById(Long id) throws OrderNotFoundException {
        return repository.findById(id).orElseThrow(OrderNotFoundException::new);
    }

    public Order create(Order order)
            throws CartNotFoundException, CartAlreadyOrderedException, UserNotFoundException {
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
        if (order.getUser() != null && order.getUser().getId() != null) {
            User user = userRepository.findById(order.getUser().getId()).orElseThrow(UserNotFoundException::new);
            order.setUser(user);
        }
        order.setDate(new Date());
        order.setStatus(OrderStatus.CREATED);
        return repository.save(order);
    }

    public Order updateStatus(Long id, OrderStatus newStatus) throws OrderNotFoundException, InvalidOrderStatusException {
        Order order = getById(id);
        if (!isValidTransition(order.getStatus(), newStatus)) {
            String nextAllowed = nextAllowedStatusMessage(order.getStatus());
            throw new InvalidOrderStatusException("La transición de estado de la orden no es válida. El siguiente estado permitido es: " + nextAllowed);
        }
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

    private String nextAllowedStatusMessage(OrderStatus current) {
        return switch (current) {
            case CREATED -> "PENDING o CANCELLED";
            case PENDING -> "CONFIRMED o CANCELLED";
            case CONFIRMED -> "SHIPPED";
            case SHIPPED -> "DELIVERED";
            default -> "-";
        };
    }
}