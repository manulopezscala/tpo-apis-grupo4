package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;
import com.uade.tpo.ecommerce.exceptions.CategoryNotFoundException;
import com.uade.tpo.ecommerce.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<Category> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Category getById(@PathVariable Long id) throws CategoryNotFoundException {
        return service.getById(id);
    }

    @PostMapping
    public Category create(@RequestBody Category category) throws CategoryDuplicateException {
        return service.create(category);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws CategoryNotFoundException {
        service.delete(id);
    }
}