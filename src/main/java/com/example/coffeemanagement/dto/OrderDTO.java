package com.example.coffeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String totalAmount;
    private String amountPaid;
    private String changeAmount;
    private String createdDate;
    private String status;
    private List<OrderItemDTO> orderItemList;
}
