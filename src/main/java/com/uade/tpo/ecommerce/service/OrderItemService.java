package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.entity.OrderItem;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.exceptions.OrderItemNotFoundException;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.OrderRepository;
import com.uade.tpo.ecommerce.repository.OrderItemRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import org.springframework.lang.NonNull;
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
    private final AuthenticatedUserService authenticatedUserService;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de ítems de orden
     * @param orderRepository repositorio de órdenes
     * @param productRepository repositorio de productos
     */
    public OrderItemService(OrderItemRepository repository,
                            OrderRepository orderRepository,
                            ProductRepository productRepository,
                            AuthenticatedUserService authenticatedUserService) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.authenticatedUserService = authenticatedUserService;
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

        Long orderId = item.getOrder().getId();
        Long productId = item.getProduct().getId();

        if (authenticatedUserService.isCurrentUserAdmin()) {
            item.setOrder(orderRepository.findById(orderId).orElseThrow(OrderNotFoundException::new));
        } else {
            Long currentUserId = authenticatedUserService.getCurrentUserId();
            item.setOrder(orderRepository.findByIdAndUserId(orderId, currentUserId)
                .orElseThrow(OrderNotFoundException::new));
        }
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        item.setProduct(product);
        item.setUnitPrice(calculateUnitPrice(product));
        return repository.save(item);
    }

    private Double calculateUnitPrice(Product product) {
        Double basePrice = product.getPrice();
        if (basePrice == null) {
            throw new IllegalStateException("El producto no tiene precio configurado");
        }

        Discount discount = product.getDiscount();
        if (discount == null || !Boolean.TRUE.equals(discount.getActive())) {
            return basePrice;
        }

        Double percentage = discount.getPercentage();
        if (percentage == null || percentage <= 0) {
            return basePrice;
        }

        Double normalizedPercentage = Math.min(percentage, 100.0);
        return basePrice * (1 - (normalizedPercentage / 100.0));
    }

    /**
     * Elimina un ítem de orden por su ID.
     * @param id identificador del ítem
     * @throws OrderItemNotFoundException si el ítem no existe
     */
    public void delete(@NonNull Long id) throws OrderItemNotFoundException {
        if (authenticatedUserService.isCurrentUserAdmin()) {
            if (!repository.existsById(id)) throw new OrderItemNotFoundException();
            repository.deleteById(id);
            return;
        }

        Long currentUserId = authenticatedUserService.getCurrentUserId();
        if (!repository.existsByIdAndOrderUserId(id, currentUserId)) {
            throw new OrderItemNotFoundException();
        }
        repository.deleteById(id);
    }
}