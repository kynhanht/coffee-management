package com.example.coffeemanagement.enums;

import lombok.Getter;

@Getter
public enum TableStatus {

    AVAILABLE("Trống", "success"),
    OCCUPIED("Đang phục vụ", "danger"),
    RESERVED("Đặt trước", "warning");

    private final String label;
    private final String color;

    TableStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

}
