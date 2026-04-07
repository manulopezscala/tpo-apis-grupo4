package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}