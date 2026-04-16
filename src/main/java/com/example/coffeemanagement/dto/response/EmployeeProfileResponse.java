package com.example.coffeemanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileResponse {

    private String fullName;
    private String positionName;
    private String phone;
    private String address;
    private String salary;
    private String username;
    private String password;
    private String picture;

}
