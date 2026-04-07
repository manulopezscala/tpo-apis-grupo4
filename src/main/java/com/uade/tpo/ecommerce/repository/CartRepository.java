package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}