package com.example.coffeemanagement.dto.response;

import com.example.coffeemanagement.dto.TableDTO;
import com.example.coffeemanagement.dto.TableOptionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveTableResponse {

    private TableDTO sourceTable;

    private List<TableOptionDTO> tableList;

}
