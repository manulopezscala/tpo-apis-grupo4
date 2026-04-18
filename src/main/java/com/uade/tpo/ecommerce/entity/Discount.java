package com.uade.tpo.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    // Usamos WRITE_ONLY para aceptar product en el request pero no devolverlo en el response (evita bucles JSON)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
}