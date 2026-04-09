package com.example.coffeemanagement.dto.request;

import com.example.coffeemanagement.dto.OrderMenuItemDTO;
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
public class SplitTableRequest {

    @NotBlank(message = "Mã bàn nguồn không được trống")
    private String sourceTableId;

    @NotBlank(message = "Mã bàn đích không được trống")
    private String targetTableId;

    private List<OrderMenuItemDTO> splitOrderList;

    private String customerName;

    private String customerPhone;

    private String employeeId;
}
