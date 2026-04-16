package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.OrderDTO;
import com.example.coffeemanagement.dto.request.OrderTableRequest;
import com.example.coffeemanagement.dto.request.PayOrderRequest;

public interface IOrderService {

    String getUnpaidOrderId(String tableId);

    String getTotalAmount(String id);

    void saveOrder(OrderTableRequest request);

    void payOrder(PayOrderRequest request);

    OrderDTO getOrder(String id);

}
