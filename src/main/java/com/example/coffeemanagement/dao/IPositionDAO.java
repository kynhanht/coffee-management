package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.model.Position;

import java.util.List;

public interface IPositionDAO {

    List<Position> findAll();
}
