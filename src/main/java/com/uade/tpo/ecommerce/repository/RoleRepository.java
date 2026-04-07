package com.uade.tpo.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uade.tpo.ecommerce.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}