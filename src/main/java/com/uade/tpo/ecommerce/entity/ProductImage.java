package com.uade.tpo.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    // RELACIÓN CON PRODUCT
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}