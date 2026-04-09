package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.TableDTO;
import com.example.coffeemanagement.dto.TableInfoDTO;
import com.example.coffeemanagement.dto.TableOptionDTO;
import com.example.coffeemanagement.enums.TableStatus;

import java.util.List;
import java.util.Optional;

public interface ITableDAO {

    Optional<TableDTO> findById(String id);

    List<TableDTO> findAll();

    Optional<TableInfoDTO> findTableInfo(String tableId);

    int updateStatus(String id, String status);

    int copyStatus(String currentId, String newId);

    List<TableOptionDTO> findByStatuses(List<TableStatus> statuses);




}
