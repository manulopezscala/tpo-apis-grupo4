package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.exceptions.CartItemNotFoundException;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.InsufficientStockException;
import com.uade.tpo.ecommerce.exceptions.InvalidCartStatusException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.enums.CartStatus;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.CartItemRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;


/**
 * Servicio para operaciones sobre ítems de carrito.
 * Permite crear y eliminar ítems asociados a carritos y productos existentes.
 */
@Service
public class CartItemService {

    private final CartItemRepository repository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final AuthenticatedUserService authenticatedUserService;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de ítems de carrito
     * @param cartRepository repositorio de carritos
     * @param productRepository repositorio de productos
     */
    public CartItemService(CartItemRepository repository,
                           CartRepository cartRepository,
                           ProductRepository productRepository,
                           AuthenticatedUserService authenticatedUserService) {
        this.repository = repository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    /**
     * Crea un nuevo ítem de carrito, validando stock y relaciones.
     * @param item datos del ítem
     * @return el ítem creado
     * @throws InsufficientStockException si no hay stock suficiente
     * @throws CartNotFoundException si el carrito no existe
     * @throws ProductNotFoundException si el producto no existe
     * @throws InvalidCartStatusException si el carrito no está activo
     */
    public CartItem create(CartItem item)
        throws InsufficientStockException, CartNotFoundException, ProductNotFoundException, InvalidCartStatusException {

        if (item.getCart() == null || item.getCart().getId() == null) {
            throw new IllegalArgumentException("Debe informar un cart.id válido para crear el item");
        }
        if (item.getProduct() == null || item.getProduct().getId() == null) {
            throw new IllegalArgumentException("Debe informar un product.id válido para crear el item");
        }

        Long cartId = item.getCart().getId();
        Long productId = item.getProduct().getId();

        if (authenticatedUserService.isCurrentUserAdmin()) {
            item.setCart(cartRepository.findById(cartId).orElseThrow(CartNotFoundException::new));
        } else {
            Long currentUserId = authenticatedUserService.getCurrentUserId();
            item.setCart(cartRepository.findByIdAndUserId(cartId, currentUserId)
                .orElseThrow(CartNotFoundException::new));
        }
        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);

        if (item.getCart().getStatus() != CartStatus.ACTIVE) throw new InvalidCartStatusException();

        int available = product.getStock();
        if (item.getQuantity() > available) throw new InsufficientStockException();
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
     * Elimina un ítem de carrito por su ID.
     * @param id identificador del ítem
     * @throws CartItemNotFoundException si el ítem no existe
     * @throws InvalidCartStatusException si el carrito del ítem no está activo
     */
    public void delete(@NonNull Long id) throws CartItemNotFoundException, InvalidCartStatusException {
        if (authenticatedUserService.isCurrentUserAdmin()) {
            CartItem item = repository.findById(id).orElseThrow(CartItemNotFoundException::new);
            if (item.getCart() == null || item.getCart().getStatus() != CartStatus.ACTIVE) {
                throw new InvalidCartStatusException();
            }
            repository.delete(item);
            return;
        }

        Long currentUserId = authenticatedUserService.getCurrentUserId();
        CartItem item = repository.findByIdAndCartUserId(id, currentUserId)
            .orElseThrow(CartItemNotFoundException::new);
        if (item.getCart() == null || item.getCart().getStatus() != CartStatus.ACTIVE) {
            throw new InvalidCartStatusException();
        }
        repository.delete(item);
    }
}