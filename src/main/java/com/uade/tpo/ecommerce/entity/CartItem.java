package com.uade.tpo.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    // Usamos WRITE_ONLY para aceptar cart en el request pero no devolverlo en el response (evita bucles JSON)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // RELACIÓN CON PRODUCT
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}