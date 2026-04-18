package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.OrderItem;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	Optional<OrderItem> findByIdAndOrderUserId(Long id, Long userId);

	boolean existsByIdAndOrderUserId(Long id, Long userId);
}