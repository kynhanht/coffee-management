package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.entity.ImportGoodDetailEntity;

public interface IImportGoodDetailDAO {

    String generateNextId();
    int insert(ImportGoodDetailEntity entity);
}
