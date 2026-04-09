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
public class MoveTableRequest {

    @NotBlank(message = "Mã bàn hiện tại không được trống")
    private String sourceTableId;
    @NotBlank(message = "Mã bàn mới không được để trống")
    private String targetTableId;

}
