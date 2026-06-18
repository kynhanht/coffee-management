package com.example.coffeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DeviceListDTO {

    private String id;
    private String name;
    private LocalDate purchaseDate;
    private int quantity;
    private BigDecimal price;
}
