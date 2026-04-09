package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.MenuItemDTO;

import java.util.List;
import java.util.Optional;

public interface IMenuItemDAO {

    Optional<MenuItemDTO> findById(String id);
    List<MenuItemDTO> findByStatus(String status);


}
