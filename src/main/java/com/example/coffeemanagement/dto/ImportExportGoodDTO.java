package com.example.coffeemanagement.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImportExportGoodDTO {

    private String goodId;
    private int quantity;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate importDate;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate exportDate;
}
