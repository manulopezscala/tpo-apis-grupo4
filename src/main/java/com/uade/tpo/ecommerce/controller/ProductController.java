package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.dto.CustomResponse;
import com.uade.tpo.ecommerce.exceptions.CategoryNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ProductDuplicateException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) throws ProductNotFoundException {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product)
            throws ProductDuplicateException, UserNotFoundException, CategoryNotFoundException {
        return service.create(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product)
            throws ProductNotFoundException, UserNotFoundException, CategoryNotFoundException {
        return service.update(id, product);
    }

    @DeleteMapping("/{id}")
    public CustomResponse delete(@PathVariable Long id) throws ProductNotFoundException {
        service.delete(id);
        return new CustomResponse(true, "Producto eliminado correctamente");
    }
}
