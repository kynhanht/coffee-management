package com.example.coffeemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderRequest {

    @NotBlank(message = "mã hóa đơn không được để trống")
    private String orderId;

    @NotBlank(message = "Mã bàn nguồn không được để trống")
    private String sourceTableId;

    @NotBlank(message = "Tổng tiền hóa đơn không được để trống")
    private String totalAmount;

    @NotBlank(message = "Tiền khách trả không được để trống")
    private String amountPaid;

    @NotBlank(message = "Tiền thối không được để trống")
    private String changeAmount;
}
