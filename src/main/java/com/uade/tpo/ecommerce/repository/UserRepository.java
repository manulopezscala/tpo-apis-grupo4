package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}