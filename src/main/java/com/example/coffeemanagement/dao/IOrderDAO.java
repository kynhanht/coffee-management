package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.OrderDTO;
import com.example.coffeemanagement.entity.OrderEntity;

import java.math.BigDecimal;
import java.util.Optional;

public interface IOrderDAO {
    String generateNextId();
    Optional<OrderEntity> findById(String id);
    Optional<OrderDTO> findDetailById(String id);
    Optional<String> findOrderIdByTableIdAndStatus(String tableId, String status);
    Optional<String> findTotalAmountById(String id);
    int insert(OrderEntity entity);
    int updateTotalById(String id);
    int updateStatusById(String id, String status);
    int updateTableIdById(String id, String tableId);
    int payOrder(String orderId, BigDecimal amountPaid, BigDecimal changeAmount, String status);

}
