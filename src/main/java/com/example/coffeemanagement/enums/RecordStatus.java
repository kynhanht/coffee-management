package com.example.coffeemanagement.enums;

import lombok.Getter;

@Getter
public enum RecordStatus {

    ACTIVE("Còn hiệu lực", "success"),
    INACTIVE("Không còn hiệu lực", "warning");

    private final String label;
    private final String color;

    RecordStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }
}
