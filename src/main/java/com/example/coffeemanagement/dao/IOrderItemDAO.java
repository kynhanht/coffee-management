package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.MergedItemDTO;
import com.example.coffeemanagement.dto.OrderItemDTO;
import com.example.coffeemanagement.entity.OrderItemEntity;

import java.util.List;

public interface IOrderItemDAO {

    List<OrderItemEntity> findByOrderId(String orderId);

    int insert(OrderItemEntity entity);

    int deleteByOrderId(String orderId);

    List<OrderItemDTO> findByTableIdAndOrderStatus(String tableId, String status);

    List<MergedItemDTO> findMergedItemsByOrderIds(List<String> orderIds);

    int updateQuantityById(String orderId, String menuItemId, int delta);
}
