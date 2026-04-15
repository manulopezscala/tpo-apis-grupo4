package com.uade.tpo.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "products")
@JsonIgnoreProperties({"orderItems", "cartItems"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    @Column
    private Boolean active = true;

    // RELACIÓN CON USER
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    // RELACIÓN CON CATEGORY
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // RELACIÓN CON ORDER ITEMS
    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    // RELACIÓN CON CART ITEMS
    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    // RELACIÓN CON DISCOUNT
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Discount discount;

    // RELACIÓN CON PRODUCT IMAGES
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;
}