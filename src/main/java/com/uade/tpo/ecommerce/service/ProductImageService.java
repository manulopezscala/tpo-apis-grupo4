package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.ProductImage;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.repository.ProductImageRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductImageService {

    private final ProductImageRepository repository;
    private final ProductRepository productRepository;

    public ProductImageService(ProductImageRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    public ProductImage create(ProductImage image) throws ProductNotFoundException {
        if (image.getProduct() == null || image.getProduct().getId() == null) {
            throw new IllegalArgumentException("Debe informar un product.id válido para crear la imagen");
        }
        image.setProduct(productRepository.findById(image.getProduct().getId()).orElseThrow(ProductNotFoundException::new));
        return repository.save(image);
    }
}