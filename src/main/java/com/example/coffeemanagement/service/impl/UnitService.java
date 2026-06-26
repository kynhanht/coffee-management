package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.dao.IUnitDAO;
import com.example.coffeemanagement.dto.UnitDTO;
import com.example.coffeemanagement.service.IUnitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UnitService implements IUnitService {
    private final IUnitDAO unitDAO;

    public UnitService(IUnitDAO unitDAO) {
        this.unitDAO = unitDAO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UnitDTO> getAllUnits() {
        return unitDAO.findAll().stream()
                .map(unit -> new UnitDTO(unit.getId(), unit.getName()))
                .toList();
    }
}
