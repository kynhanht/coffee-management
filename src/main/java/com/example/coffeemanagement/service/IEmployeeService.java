package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.EmployeeDTO;
import com.example.coffeemanagement.dto.EmployeeDetailDTO;
import com.example.coffeemanagement.dto.EmployeeListDTO;
import com.example.coffeemanagement.dto.PageDTO;

public interface IEmployeeService {

    EmployeeDetailDTO getDetail(String username);

    void updateProfile(String username, EmployeeDetailDTO dto);

    PageDTO<EmployeeListDTO> getAll(int page, int size, String sort, String dir, String searchValue);

    void create(EmployeeDTO dto);

    EmployeeDTO getById(String id);

    void update(String id, EmployeeDTO dto);

    void delete(String id);
}
