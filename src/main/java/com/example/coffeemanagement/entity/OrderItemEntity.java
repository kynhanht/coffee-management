package com.example.coffeemanagement.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {

    private String orderId;
    private String menuItemId;
    private Integer quantity;
    private BigDecimal currentPrice;
}
