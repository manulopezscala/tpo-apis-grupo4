package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.OrderItem;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.OrderRepository;
import com.uade.tpo.ecommerce.repository.OrderItemRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    private final OrderItemRepository repository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemService(OrderItemRepository repository,
                            OrderRepository orderRepository,
                            ProductRepository productRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public OrderItem create(OrderItem item) throws OrderNotFoundException, ProductNotFoundException {
        if (item.getOrder() == null || item.getOrder().getId() == null) {
            throw new IllegalArgumentException("Debe informar un order.id válido para crear el item de orden");
        }
        if (item.getProduct() == null || item.getProduct().getId() == null) {
            throw new IllegalArgumentException("Debe informar un product.id válido para crear el item de orden");
        }

        item.setOrder(orderRepository.findById(item.getOrder().getId()).orElseThrow(OrderNotFoundException::new));
        item.setProduct(productRepository.findById(item.getProduct().getId()).orElseThrow(ProductNotFoundException::new));
        return repository.save(item);
    }
}