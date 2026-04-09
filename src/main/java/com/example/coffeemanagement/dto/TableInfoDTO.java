package com.example.coffeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoDTO {
    private String customerName;
    private String reservationDate;
    private String tableName;
}
