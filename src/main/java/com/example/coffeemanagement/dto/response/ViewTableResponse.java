package com.example.coffeemanagement.dto.response;

import com.example.coffeemanagement.dto.OrderMenuItemDTO;
import com.example.coffeemanagement.dto.TableInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewTableResponse {

    private TableInfoDTO tableInfo;
    private List<OrderMenuItemDTO> orderList;
}
