package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;
import com.uade.tpo.ecommerce.exceptions.CategoryNotFoundException;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> getAll() {
        return repository.findAll();
    }

    public Category getById(Long id) throws CategoryNotFoundException {
        return repository.findById(id).orElseThrow(CategoryNotFoundException::new);
    }

    public Category create(Category category) throws CategoryDuplicateException {
        if (repository.findByDescriptionIgnoreCase(category.getDescription()).isPresent())
            throw new CategoryDuplicateException();
        return repository.save(category);
    }

    public void delete(Long id) throws CategoryNotFoundException {
        if (!repository.existsById(id)) throw new CategoryNotFoundException();
        repository.deleteById(id);
    }
}