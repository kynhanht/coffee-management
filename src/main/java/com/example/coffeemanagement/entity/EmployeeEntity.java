package com.example.coffeemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntity {

    private String id;

    private String positionId;

    private String fullName;

    private String phone;

    private String address;

    private String picture;

    private String username;

    private String password;

    private String role;

    private String status;

}
