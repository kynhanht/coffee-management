package com.example.coffeemanagement.dto.response;

import com.example.coffeemanagement.dto.OrderItemSelectDTO;
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
    private List<OrderItemSelectDTO> orderItemList;
}
