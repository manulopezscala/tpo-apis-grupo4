package com.uade.tpo.ecommerce.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cart {
    private int id;
    private String userId;
    private String currency;
    private String status;
}
