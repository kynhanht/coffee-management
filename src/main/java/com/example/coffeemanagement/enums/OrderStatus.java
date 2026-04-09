package com.example.coffeemanagement.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PAID("Đã Thanh toán", "success"),
    UNPAID("Chưa thanh toán", "danger"),
    CANCELLED("Đã hủy", "warning");

    private final String label;
    private final String color;

    OrderStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }


}
