package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.exceptions.ProductDuplicateException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
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

    public Product getById(Long id) throws ProductNotFoundException {
        return repository.findById(id).orElseThrow(ProductNotFoundException::new);
    }

    public Product create(Product product) throws ProductDuplicateException {
        if (repository.existsByNameIgnoreCase(product.getName()))
            throw new ProductDuplicateException();
        return repository.save(product);
    }

    public Product update(Long id, Product product) throws ProductNotFoundException {
        if (!repository.existsById(id)) throw new ProductNotFoundException();
        product.setId(id);
        return repository.save(product);
    }

    public void delete(Long id) throws ProductNotFoundException {
        if (!repository.existsById(id)) throw new ProductNotFoundException();
        repository.deleteById(id);
    }
}