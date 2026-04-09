package com.example.coffeemanagement.service;


import com.example.coffeemanagement.dto.OrderMenuItemDTO;

import java.util.List;

public interface IOrderItemService {

    List<OrderMenuItemDTO> getOrderByTableId(String tableId);
    List<OrderMenuItemDTO> getMenuWithOrderByTableId(String tableId);

}
