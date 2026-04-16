package com.example.coffeemanagement.service;


import com.example.coffeemanagement.dto.OrderItemSelectDTO;

import java.util.List;

public interface IOrderItemService {

    List<OrderItemSelectDTO> getOrderItemsForTable(String tableId);
    List<OrderItemSelectDTO> getMenuWithOrderItemsForTable(String tableId);

}
