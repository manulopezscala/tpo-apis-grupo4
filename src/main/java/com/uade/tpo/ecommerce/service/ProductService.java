package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public Product getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Product create(Product product) {
        return repository.save(product);
    }

    public Product update(Long id, Product product) {
        product.setId(id);
        return repository.save(product);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}