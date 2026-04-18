package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.CartItem;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	Optional<CartItem> findByIdAndCartUserId(Long id, Long userId);

	boolean existsByIdAndCartUserId(Long id, Long userId);
}