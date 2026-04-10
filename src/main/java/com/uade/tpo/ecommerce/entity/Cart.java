package com.uade.tpo.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.uade.tpo.ecommerce.enums.CartStatus;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private CartStatus status;

    @Column
    private String currency;

    // RELACIÓN CON USER
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // RELACIÓN CON CART ITEMS
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items;

    // RELACIÓN CON ORDER
    @JsonIgnore
    @OneToOne(mappedBy = "cart")
    private Order order;
}