package com.example.coffeemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetailEntity {

    private String tableId;

    private String employeeId;
    
    private String customerName;

    private String customerPhone;

    private LocalDateTime reservationDate;

}
