package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.exceptions.DiscountDuplicateException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.DiscountRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    private final DiscountRepository repository;
    private final ProductRepository productRepository;

    public DiscountService(DiscountRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    public Discount create(Discount discount) throws DiscountDuplicateException, ProductNotFoundException {
        if (discount.getProduct() == null || discount.getProduct().getId() == null) {
            throw new IllegalArgumentException("Debe informar un product.id válido para crear el descuento");
        }

        Long productId = discount.getProduct().getId();
        if (repository.existsByProductId(productId)) {
            throw new DiscountDuplicateException();
        }

        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        discount.setProduct(product);
        return repository.save(discount);
    }
}