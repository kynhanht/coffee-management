package com.example.coffeemanagement.dto.response;

import com.example.coffeemanagement.dto.OrderItemSelectDTO;
import com.example.coffeemanagement.dto.TableDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderResponse {

    private TableDTO sourceTable;

    private List<OrderItemSelectDTO> orderItemList;

    private String totalAmount;
}
