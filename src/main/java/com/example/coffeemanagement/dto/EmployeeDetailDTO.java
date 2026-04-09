package com.example.coffeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDetailDTO {

    private String id;
    private String fullName;
    private String address;
    private String phone;
    private String positionId;
    private String positionName;
    private BigDecimal salary;
    private String picture;
    private MultipartFile file;
    private String username;
    private String password;


}
