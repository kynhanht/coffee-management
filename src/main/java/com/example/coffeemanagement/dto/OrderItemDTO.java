package com.example.coffeemanagement.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    private String menuItemId;

    private String menuItemName;

    private int quantity;

    private String currentPrice;

    private String lineTotal;
}
