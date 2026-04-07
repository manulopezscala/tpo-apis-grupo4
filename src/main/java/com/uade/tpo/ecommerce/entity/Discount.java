package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double percentage;

    @Column(nullable = false)
    private Boolean active;

    // RELACIÓN CON PRODUCT
    @OneToOne
    @JoinColumn(name = "product_id", unique = true)
    private Product product;
}