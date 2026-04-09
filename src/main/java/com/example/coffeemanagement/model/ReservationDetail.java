package com.example.coffeemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetail {

    private String tableId;

    private String employeeId;
    
    private String customerName;

    private String customerPhone;

    private LocalDateTime reservationDate;

}
