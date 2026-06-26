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
public class GoodListDTO {

    private String id;
    private String name;
    private LocalDate importDate;
    private LocalDate exportDate;
    private Integer quantity;
    private String unitName;
    private BigDecimal price;
    private BigDecimal totalAmount;
}
