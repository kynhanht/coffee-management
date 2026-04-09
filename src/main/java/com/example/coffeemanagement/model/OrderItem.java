package com.example.coffeemanagement.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private String orderId;
    private String menuItemId;
    private Integer quantity;
    private BigDecimal currentPrice;
}
