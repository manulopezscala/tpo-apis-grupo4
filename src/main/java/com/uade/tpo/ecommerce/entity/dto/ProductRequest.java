package com.uade.tpo.ecommerce.entity.dto;

public record ProductRequest(
    String name,
    String description,
    Double price,
    Integer stock,
    Boolean active,
    String sellerUsername,
    String categoryName
) {}
