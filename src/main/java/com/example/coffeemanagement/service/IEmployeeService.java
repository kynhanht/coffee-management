package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.EmployeeDTO;
import com.example.coffeemanagement.dto.EmployeeListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.dto.request.EmployeeProfileRequest;
import com.example.coffeemanagement.dto.response.EmployeeProfileResponse;

public interface IEmployeeService {

    EmployeeDTO getEmployee(String id);

    EmployeeProfileResponse getProfile(String id);

    void updateProfile(String id, EmployeeProfileRequest request);

    PageDTO<EmployeeListDTO> getAllEmployees(int page, int size, String sort, String dir, String searchValue);

    void createEmployee(EmployeeDTO dto);

    void updateEmployee(String id, EmployeeDTO dto);

    void deleteEmployee(String id);
}
