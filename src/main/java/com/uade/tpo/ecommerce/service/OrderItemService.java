package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.OrderItem;
import com.uade.tpo.ecommerce.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    private final OrderItemRepository repository;

    public OrderItemService(OrderItemRepository repository) {
        this.repository = repository;
    }

    public OrderItem create(OrderItem item) {
        return repository.save(item);
    }
}