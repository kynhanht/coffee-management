package com.example.coffeemanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

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
