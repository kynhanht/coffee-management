package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.EmployeeDTO;
import com.example.coffeemanagement.dto.EmployeeDetailDTO;
import com.example.coffeemanagement.dto.EmployeeListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.model.Employee;

import java.util.Optional;

public interface IEmployeeDAO {

    String generateNextId();

    Optional<Employee> findByUsername(String userName);

    Optional<EmployeeDTO> findById(String id);

    Optional<EmployeeDetailDTO> findDetailByUsername(String username);

    int updateProfile(String username, Employee model);

    PageDTO<EmployeeListDTO> findAll(int page, int size, String sort, String dir, String searchValue);

    void insert(Employee model);
    void update(String id, Employee model);

    void delete(String id);

}
