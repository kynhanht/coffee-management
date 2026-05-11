package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.OrderDTO;
import com.example.coffeemanagement.dto.request.OrderTableRequest;
import com.example.coffeemanagement.dto.request.PayOrderRequest;

import java.math.BigDecimal;

public interface IOrderService {

    String getUnpaidOrderId(String tableId);

    BigDecimal getTotalAmount(String id);

    void saveOrder(OrderTableRequest request);

    void payOrder(PayOrderRequest request);

    OrderDTO getOrder(String id);

}
