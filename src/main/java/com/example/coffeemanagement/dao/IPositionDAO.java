package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.entity.PositionEntity;

import java.util.List;

public interface IPositionDAO {

    List<PositionEntity> findAll();
}
