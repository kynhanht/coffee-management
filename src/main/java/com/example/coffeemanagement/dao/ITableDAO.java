package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.TableInfoDTO;
import com.example.coffeemanagement.dto.TableOptionDTO;
import com.example.coffeemanagement.entity.TableEntity;
import com.example.coffeemanagement.enums.TableStatus;

import java.util.List;
import java.util.Optional;

public interface ITableDAO {

    Optional<TableEntity> findById(String id);

    List<TableEntity> findAll();

    Optional<TableInfoDTO> findTableInfoById(String tableId);

    int updateStatusById(String id, String status);

    List<TableOptionDTO> findByStatuses(List<TableStatus> statuses);




}
