package com.example.coffeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GoodDTO {
    private String id;
    private String name;
    private int quantity;
    private BigDecimal price;
    private String unitId;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate importDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate exportDate;


}
