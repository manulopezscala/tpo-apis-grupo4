package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.ProductImage;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-images")
public class ProductImageController {

    private final ProductImageService service;

    public ProductImageController(ProductImageService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductImage create(@RequestBody ProductImage image) throws ProductNotFoundException {
        return service.create(image);
    }
}
