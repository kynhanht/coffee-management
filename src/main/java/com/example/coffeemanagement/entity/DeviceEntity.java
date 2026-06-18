package com.example.coffeemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceEntity {

    private String id;
    private String name;
    private Integer quantity;
    private LocalDate purchaseDate;
    private BigDecimal price;
    private String status;
}
