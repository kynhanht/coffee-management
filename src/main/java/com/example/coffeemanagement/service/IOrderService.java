package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.request.OrderTableRequest;
import com.example.coffeemanagement.dto.request.PayOrderRequest;

public interface IOrderService {

    String getUnpaidOrderByTableId(String tableId);

    void saveOrder(OrderTableRequest request);

    void payOrder(PayOrderRequest request);


}
