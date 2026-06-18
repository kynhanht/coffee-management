package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.DeviceListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.entity.DeviceEntity;

import java.util.Optional;

public interface IDeviceDAO {

    String generateNextId();
    PageDTO<DeviceListDTO> findAll(int page, int size, String sort, String dir, String searchValue);
    Optional<DeviceEntity> findById(String id);
    int insert(DeviceEntity entity);
    int updateById(String id, DeviceEntity entity);
    int updateStatusById(String id, String status);
}
