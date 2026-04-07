package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPrice;

    // RELACIÓN CON CART
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // RELACIÓN CON PRODUCT
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}