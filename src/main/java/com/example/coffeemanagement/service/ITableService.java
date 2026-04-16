package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.TableDTO;
import com.example.coffeemanagement.dto.TableInfoDTO;
import com.example.coffeemanagement.dto.TableOptionDTO;
import com.example.coffeemanagement.dto.request.*;
import com.example.coffeemanagement.enums.TableStatus;

import java.util.List;

public interface ITableService {

    TableDTO getTable(String id);
    List<TableDTO> getAllTables();
    TableInfoDTO getTableInfo(String id);
    void reserveTable(ReserveTableRequest request);
    void moveTable(MoveTableRequest request);
    List<TableOptionDTO> getSelectableTables(String id, List<TableStatus> statues);
    void mergeTables(MergeTableRequest request);
    void splitTable(SplitTableRequest request);
    void cancelTable(String id);


}
