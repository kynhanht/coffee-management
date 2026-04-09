package com.example.coffeemanagement.dto.response;

import com.example.coffeemanagement.dto.OrderMenuItemDTO;
import com.example.coffeemanagement.dto.TableDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderResponse {

    private TableDTO sourceTable;

    private List<OrderMenuItemDTO> orderList;

    private BigDecimal totalAmount;

    public BigDecimal getTotalAmount() {
        return orderList.stream()
                .map(menuItem -> menuItem.getCurrentPrice().multiply(BigDecimal.valueOf(menuItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
