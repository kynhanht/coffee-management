package com.example.coffeemanagement.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {

    private String id;
    private String name;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate purchaseDate;
    private int quantity;
    private BigDecimal price;


}
