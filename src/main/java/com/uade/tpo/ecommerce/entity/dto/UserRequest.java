package com.uade.tpo.ecommerce.entity.dto;

public record UserRequest(
    String username,
    String email,
    String password,
    String firstName,
    String lastName,
    String role
) {}
