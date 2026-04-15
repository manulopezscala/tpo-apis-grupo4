package com.uade.tpo.ecommerce.entity.dto;

import com.uade.tpo.ecommerce.enums.OrderStatus;

public class OrderStatusUpdateRequest {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderStatus toOrderStatus() throws IllegalArgumentException {
        return OrderStatus.valueOf(status);
    }
}
