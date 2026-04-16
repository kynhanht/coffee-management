package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.entity.ReservationDetailEntity;

public interface IReservationDetailDAO {

    int insert(ReservationDetailEntity entity);
    int updateTableId(String sourceTableId, String targetTableId);
    int updateByTableId(String tableId, ReservationDetailEntity entity);
    int deleteByTableId(String tableId);

}
