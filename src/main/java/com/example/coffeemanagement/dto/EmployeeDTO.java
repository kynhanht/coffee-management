package com.example.coffeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private String id;
    private String fullName;
    private String address;
    private String positionId;
    private String phone;
    private String username;
    private String password;
    private String picture;
    private MultipartFile file;

}
