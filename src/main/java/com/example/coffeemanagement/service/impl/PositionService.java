package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.dao.IPositionDAO;
import com.example.coffeemanagement.model.Position;
import com.example.coffeemanagement.service.IPositionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PositionService implements IPositionService {

    private final IPositionDAO chucVuDAO;

    public PositionService(IPositionDAO chucVuDAO) {
        this.chucVuDAO = chucVuDAO;
    }
    @Transactional(readOnly = true)
    @Override
    public List<Position> getAll() {
        return chucVuDAO.findAll();
    }
}
