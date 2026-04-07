package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}