package com.uade.tpo.ecommerce.entity.dto;

public record CustomResponse(
    boolean success,
    String message
) {}