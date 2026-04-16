package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.EmployeeListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.dto.response.EmployeeProfileResponse;
import com.example.coffeemanagement.entity.EmployeeEntity;

import java.util.Optional;

public interface IEmployeeDAO {

    String generateNextId();
    Optional<EmployeeEntity> findByUsername(String userName);
    Optional<EmployeeEntity> findById(String id);
    Optional<EmployeeProfileResponse> findProfileById(String id);
    int updateProfileById(String id, EmployeeEntity entity);
    PageDTO<EmployeeListDTO> findAll(int page, int size, String sort, String dir, String searchValue);
    int insert(EmployeeEntity entity);
    int updateById(String id, EmployeeEntity entity);
    int deleteById(String id);

}
