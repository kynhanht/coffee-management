package com.example.coffeemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderRequest {

    @NotBlank(message = "mã hóa đơn không được để trống")
    private String orderId;

    @NotBlank(message = "Mã bàn nguồn không được để trống")
    private String sourceTableId;

    @NotNull(message = "Tổng tiền hóa đơn không được để trống")
    private BigDecimal totalAmount;

    @NotNull(message = "Tiền khách trả không được để trống")
    private BigDecimal amountPaid;

    @NotNull(message = "Tiền thối không được để trống")
    private BigDecimal changeAmount;
}
