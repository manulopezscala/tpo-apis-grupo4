package com.uade.tpo.ecommerce.service;

import java.util.ArrayList;
import java.util.Optional;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;

public interface CategoryService {
    ArrayList<Category> getCategories();

    Optional<Category> getCategoryById(int categoryId);

    Category createCategory(int newCategoryId, String description) throws CategoryDuplicateException;

}
