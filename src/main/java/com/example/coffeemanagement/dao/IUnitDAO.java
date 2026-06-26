package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.entity.UnitEntity;

import java.util.List;

public interface IUnitDAO {

    List<UnitEntity> findAll();
}
