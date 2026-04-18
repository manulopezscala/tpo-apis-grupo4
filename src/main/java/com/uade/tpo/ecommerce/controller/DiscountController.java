package com.uade.tpo.ecommerce.controller;

import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.entity.dto.CustomResponse;
import com.uade.tpo.ecommerce.exceptions.DiscountDuplicateException;
import com.uade.tpo.ecommerce.exceptions.DiscountNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.service.DiscountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/discounts")
public class DiscountController {

    private final DiscountService service;

    public DiscountController(DiscountService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Discount create(@RequestBody Discount discount) throws DiscountDuplicateException, ProductNotFoundException {
        return service.create(discount);
    }

    @DeleteMapping("/{id}")
    public CustomResponse delete(@PathVariable Long id) throws DiscountNotFoundException {
        service.delete(id);
        return new CustomResponse(true, "Descuento eliminado correctamente");
    }
}
