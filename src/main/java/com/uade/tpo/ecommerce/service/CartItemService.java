package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.exceptions.CartItemNotFoundException;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.InsufficientStockException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.CartItemRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

    private final CartItemRepository repository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartItemService(CartItemRepository repository,
                           CartRepository cartRepository,
                           ProductRepository productRepository) {
        this.repository = repository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public CartItem create(CartItem item)
            throws InsufficientStockException, CartNotFoundException, ProductNotFoundException {
        if (item.getCart() == null || item.getCart().getId() == null) {
            throw new IllegalArgumentException("Debe informar un cart.id válido para crear el item");
        }
        if (item.getProduct() == null || item.getProduct().getId() == null) {
            throw new IllegalArgumentException("Debe informar un product.id válido para crear el item");
        }

        item.setCart(cartRepository.findById(item.getCart().getId()).orElseThrow(CartNotFoundException::new));
        Product product = productRepository.findById(item.getProduct().getId()).orElseThrow(ProductNotFoundException::new);

        int available = product.getStock();
        if (item.getQuantity() > available) throw new InsufficientStockException();
        item.setProduct(product);
        return repository.save(item);
    }

    public void delete(Long id) throws CartItemNotFoundException {
        if (!repository.existsById(id)) throw new CartItemNotFoundException();
        repository.deleteById(id);
    }
}