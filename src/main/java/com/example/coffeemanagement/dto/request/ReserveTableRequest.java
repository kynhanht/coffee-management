package com.example.coffeemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveTableRequest {

    @NotBlank(message = "Mã bàn không được để trống")
    private String sourceTableId;

    private String employeeId;

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String customerPhone;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @NotNull(message = "Ngày giờ đặt không được để trống")
    private LocalDateTime reservationDate;
}
