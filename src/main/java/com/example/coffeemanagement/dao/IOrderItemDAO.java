package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.MergedItemDTO;
import com.example.coffeemanagement.dto.OrderMenuItemDTO;
import com.example.coffeemanagement.model.OrderItem;

import java.util.List;

public interface IOrderItemDAO {

    int insert(OrderItem model);

    int deleteByOrderId(String orderId);

    List<OrderMenuItemDTO> findOrderByTableId(String tableId);

    List<MergedItemDTO> findMergedItems(List<String> orderIds);

    int updateQuantityById(String orderId, String menuItemId, int delta);
}
