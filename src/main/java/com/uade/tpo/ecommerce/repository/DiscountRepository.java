package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    boolean existsByProductId(Long productId);
}