package com.uade.tpo.ecommerce.service;

import java.util.List;
import java.util.Optional;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;

public interface CategoryService {
    List<Category> getCategories();

    Optional<Category> getCategoryById(Long categoryId);

    Category createCategory(String description) throws CategoryDuplicateException;

}
