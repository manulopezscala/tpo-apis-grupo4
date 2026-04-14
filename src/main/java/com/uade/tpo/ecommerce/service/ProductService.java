package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.entity.dto.ProductRequest;
import com.uade.tpo.ecommerce.exceptions.ProductDuplicateException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ProductService(ProductRepository repository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public Product getById(Long id) throws ProductNotFoundException {
        return repository.findById(id).orElseThrow(ProductNotFoundException::new);
    }

    public Product create(ProductRequest request) throws ProductDuplicateException {
        if (repository.existsByNameIgnoreCase(request.name()))
            throw new ProductDuplicateException();

        Category category = categoryRepository.findByNameIgnoreCase(request.categoryName())
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + request.categoryName()));

        User seller = userRepository.findByUsername(request.sellerUsername())
            .orElseThrow(() -> new IllegalArgumentException("Vendedor no encontrado: " + request.sellerUsername()));

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setActive(request.active() != null ? request.active() : true);
        product.setCategory(category);
        product.setSeller(seller);
        return repository.save(product);
    }

    public Product update(Long id, ProductRequest request) throws ProductNotFoundException {
        Product existing = repository.findById(id).orElseThrow(ProductNotFoundException::new);

        Category category = categoryRepository.findByNameIgnoreCase(request.categoryName())
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada: " + request.categoryName()));

        User seller = userRepository.findByUsername(request.sellerUsername())
            .orElseThrow(() -> new IllegalArgumentException("Vendedor no encontrado: " + request.sellerUsername()));

        existing.setName(request.name());
        existing.setDescription(request.description());
        existing.setPrice(request.price());
        existing.setStock(request.stock());
        existing.setActive(request.active() != null ? request.active() : existing.getActive());
        existing.setCategory(category);
        existing.setSeller(seller);
        return repository.save(existing);
    }

    public void delete(Long id) throws ProductNotFoundException {
        if (!repository.existsById(id)) throw new ProductNotFoundException();
        repository.deleteById(id);
    }
}