package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.exceptions.DiscountDuplicateException;
import com.uade.tpo.ecommerce.repository.DiscountRepository;
import org.springframework.stereotype.Service;

@Service
public class DiscountService {

    private final DiscountRepository repository;

    public DiscountService(DiscountRepository repository) {
        this.repository = repository;
    }

    public Discount create(Discount discount) throws DiscountDuplicateException {
        if (discount.getProduct() != null && repository.existsByProductId(discount.getProduct().getId()))
            throw new DiscountDuplicateException();
        return repository.save(discount);
    }
}