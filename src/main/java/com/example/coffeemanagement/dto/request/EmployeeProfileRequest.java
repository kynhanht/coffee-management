package com.example.coffeemanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeProfileRequest {

    private String employeeId;
    private String fullName;
    private String phone;
    private String address;
    private String picture;
    private MultipartFile pictureFile;

}
