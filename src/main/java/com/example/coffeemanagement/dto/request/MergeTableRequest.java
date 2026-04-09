package com.example.coffeemanagement.dto.request;

import com.example.coffeemanagement.dto.TableOptionDTO;
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
public class MergeTableRequest {

    @NotBlank(message = "Mã bàn hiện tại không được để trống")
    private String sourceTableId;

    private String employeeId;

    private List<TableOptionDTO> mergeableSourceTableList;

    @NotBlank(message = "Mã bàn đích không được để trống")
    private String targetTableId;

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;

    @NotBlank(message = "Số điện thoại khách hàng không được để trống")
    private String customerPhone;
}
