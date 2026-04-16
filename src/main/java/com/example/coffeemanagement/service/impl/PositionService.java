package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.dao.IPositionDAO;
import com.example.coffeemanagement.dto.PositionDTO;
import com.example.coffeemanagement.service.IPositionService;
import com.example.coffeemanagement.util.SystemUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class PositionService implements IPositionService {

    private final IPositionDAO positionDAO;

    public PositionService(IPositionDAO positionDAO) {
        this.positionDAO = positionDAO;
    }
    @Transactional(readOnly = true)
    @Override
    public List<PositionDTO> getAllPositions() {
        return positionDAO.findAll().stream()
                .map(position -> new PositionDTO(position.getId(), position.getName(), SystemUtils.bigDecimalToString(position.getSalary(), Locale.US)))
                .toList();
    }
}
