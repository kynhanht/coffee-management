package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.dao.IChucVuDAO;
import com.example.coffeemanagement.model.ChucVu;
import com.example.coffeemanagement.service.IChucVuService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChucVuService implements IChucVuService {

    private final IChucVuDAO chucVuDAO;

    public ChucVuService(IChucVuDAO chucVuDAO) {
        this.chucVuDAO = chucVuDAO;
    }

    @Override
    public List<ChucVu> getAll() {
        return chucVuDAO.findAll();
    }
}
