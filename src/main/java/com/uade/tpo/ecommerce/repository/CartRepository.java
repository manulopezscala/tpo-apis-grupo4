package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.enums.CartStatus;

import java.util.Optional;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);

	List<Cart> findAllByUserId(Long userId);

	Optional<Cart> findByIdAndUserId(Long id, Long userId);

	boolean existsByIdAndUserId(Long id, Long userId);
}