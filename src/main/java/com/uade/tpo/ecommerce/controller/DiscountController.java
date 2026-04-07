package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.exceptions.DiscountDuplicateException;
import com.uade.tpo.ecommerce.service.DiscountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discounts")
public class DiscountController {

    private final DiscountService service;

    public DiscountController(DiscountService service) {
        this.service = service;
    }

    @PostMapping
    public Discount create(@RequestBody Discount discount) throws DiscountDuplicateException {
        return service.create(discount);
    }
}
