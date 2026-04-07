package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.repository.DiscountRepository;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    private final DiscountRepository repository;

    public DiscountService(DiscountRepository repository) {
        this.repository = repository;
    }

    public Discount create(Discount discount) {
        return repository.save(discount);
    }
}