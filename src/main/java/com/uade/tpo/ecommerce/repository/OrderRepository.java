package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
	boolean existsByCartId(Long cartId);

	List<Order> findAllByUserId(Long userId);

	Optional<Order> findByIdAndUserId(Long id, Long userId);

	boolean existsByIdAndUserId(Long id, Long userId);
}