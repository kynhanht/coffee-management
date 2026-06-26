package com.example.coffeemanagement.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GoodEntity {

    private String id;
    private String name;
    private Integer quantity;
    private String unitId;
    private BigDecimal price;
    private LocalDate importDate;
    private LocalDate exportDate;
    private String status;

}
