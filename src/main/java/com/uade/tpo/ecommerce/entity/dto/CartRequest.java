package com.uade.tpo.ecommerce.entity.dto;

import lombok.Data;

@Data
public class CartRequest {
    private String userId;
    private String currency;
}
