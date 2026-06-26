package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.entity.ExportGoodDetailEntity;
import com.example.coffeemanagement.entity.ImportGoodDetailEntity;

public interface IExportGoodDetailDAO {

    String generateNextId();
    int insert(ExportGoodDetailEntity entity);
}
