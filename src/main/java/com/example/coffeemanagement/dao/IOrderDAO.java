package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.model.Order;

import java.math.BigDecimal;
import java.util.Optional;

public interface IOrderDAO {
    String generateNextId();
    Optional<String> findUnpaidOrderByTableId(String tableId);
    int insert(Order model);
    int updateTotalById(String id);
    int updateStatusById(String id, String status);
    int updateTableIdById(String id, String tableId);
    int payOrder(String orderId, BigDecimal amountPaid, BigDecimal changeAmount);

}
