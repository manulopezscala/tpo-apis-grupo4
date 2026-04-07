package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.ProductImage;
import com.uade.tpo.ecommerce.service.ProductImageService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-images")
public class ProductImageController {

    private final ProductImageService service;

    public ProductImageController(ProductImageService service) {
        this.service = service;
    }

    @PostMapping
    public ProductImage create(@RequestBody ProductImage image) {
        return service.create(image);
    }
}
