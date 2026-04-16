package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.entity.MenuItemEntity;

import java.util.List;
import java.util.Optional;

public interface IMenuItemDAO {

    Optional<MenuItemEntity> findById(String id);
    List<MenuItemEntity> findByStatus(String status);


}
