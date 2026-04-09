package com.example.coffeemanagement.dto;

import com.example.coffeemanagement.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TableDTO {

    private String id;
    private String name;
    private TableStatus status;

}
