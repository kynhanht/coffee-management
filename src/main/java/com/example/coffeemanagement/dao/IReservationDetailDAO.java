package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.model.ReservationDetail;

public interface IReservationDetailDAO {

    int insert(ReservationDetail model);
    int updateTableId(String sourceTableId, String targetTableId);
    int updateByTableId(String tableId, ReservationDetail model);
    int deleteByTableId(String tableId);

}
