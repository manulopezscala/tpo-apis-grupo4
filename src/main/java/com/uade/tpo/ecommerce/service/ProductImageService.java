package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.ProductImage;
import com.uade.tpo.ecommerce.repository.ProductImageRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductImageService {

    private final ProductImageRepository repository;

    public ProductImageService(ProductImageRepository repository) {
        this.repository = repository;
    }

    public ProductImage create(ProductImage image) {
        return repository.save(image);
    }
}