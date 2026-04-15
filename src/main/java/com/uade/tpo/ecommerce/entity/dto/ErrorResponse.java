package com.uade.tpo.ecommerce.entity.dto;

public record ErrorResponse(
    boolean error,
    String message
) {}