package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.GoodListDTO;
import com.example.coffeemanagement.dto.ImportExportGoodDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.entity.GoodEntity;

import java.util.List;
import java.util.Optional;

public interface IGoodDAO {

    String generateNextId();
    PageDTO<GoodListDTO> findAll(int page, int size, String sort, String dir, String searchValue);
    List<GoodEntity> findAll();
    Optional<GoodEntity> findById(String id);
    int insert(GoodEntity entity);
    int updateById(String id, GoodEntity entity);
    int updateStatusById(String id, String status);
    int updateImport(String id, ImportExportGoodDTO dto);
    int updateExport(String id, ImportExportGoodDTO dto);
}
