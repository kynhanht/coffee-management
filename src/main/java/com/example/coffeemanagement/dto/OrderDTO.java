package com.example.coffeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {

    private String id;
    private String tableName;
    private String employeeName;
    private String promotionName;
    private String customerName;
    private String customerPhone;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal changeAmount;
    private LocalDateTime createdDate;
    private String status;
    private List<OrderItemDTO> orderItemList;
}
