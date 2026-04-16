package com.example.coffeemanagement.dto.request;

import com.example.coffeemanagement.dto.OrderItemSelectDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTableRequest {

    @NotBlank(message = "Mã bàn không được để trống")
    private String sourceTableId;

    private String employeeId;

    private List<OrderItemSelectDTO> orderItemList;
}
