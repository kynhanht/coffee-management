package com.example.coffeemanagement.entity;

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
public class ExportGoodDetailEntity {

    private String id;
    private String employeeId;
    private String goodId;
    private LocalDate exportDate;
    private BigDecimal price;
    private Integer quantity;
}
