package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.OrderItem;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.OrderRepository;
import com.uade.tpo.ecommerce.repository.OrderItemRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;


/**
 * Servicio para operaciones sobre ítems de orden.
 * Permite crear ítems asociados a órdenes y productos existentes.
 */
@Service
public class OrderItemService {

    private final OrderItemRepository repository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de ítems de orden
     * @param orderRepository repositorio de órdenes
     * @param productRepository repositorio de productos
     */
    public OrderItemService(OrderItemRepository repository,
                            OrderRepository orderRepository,
                            ProductRepository productRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Crea un nuevo ítem de orden, validando relaciones.
     * @param item datos del ítem
     * @return el ítem creado
     * @throws OrderNotFoundException si la orden no existe
     * @throws ProductNotFoundException si el producto no existe
     */
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