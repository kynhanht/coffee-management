package com.example.coffeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemSelectDTO {

    private String menuItemId;

    private String menuItemName;

    private int quantity;

    private BigDecimal currentPrice;

    private BigDecimal lineTotal;

    private Boolean selected;

}
