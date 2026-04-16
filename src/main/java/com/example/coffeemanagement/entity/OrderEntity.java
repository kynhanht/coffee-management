package com.example.coffeemanagement.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    private String id;
    private String tableId;
    private String employeeId;
    private String promotionId;
    private String customerName;
    private String customerPhone;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal changeAmount;
    private LocalDateTime createdDate;
    private String status;


}
