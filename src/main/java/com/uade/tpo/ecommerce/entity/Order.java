package com.uade.tpo.ecommerce.entity;

import com.uade.tpo.ecommerce.enums.OrderStatus;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // RELACIÓN CON USER
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // RELACIÓN CON ORDER ITEMS
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
}