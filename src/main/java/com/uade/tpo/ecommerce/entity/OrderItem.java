package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPrice;

    // RELACIÓN CON ORDER
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // RELACIÓN CON PRODUCT
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}