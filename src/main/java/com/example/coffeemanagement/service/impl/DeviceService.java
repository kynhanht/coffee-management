package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IDeviceDAO;
import com.example.coffeemanagement.dto.DeviceDTO;
import com.example.coffeemanagement.dto.DeviceListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.entity.DeviceEntity;
import com.example.coffeemanagement.enums.RecordStatus;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.service.IDeviceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService implements IDeviceService {

    private final IDeviceDAO deviceDAO;

    public DeviceService(IDeviceDAO deviceDAO) {
        this.deviceDAO = deviceDAO;
    }

    @Transactional(readOnly = true)
    @Override
    public PageDTO<DeviceListDTO> getAllDevices(int page, int size, String sort, String dir, String searchValue) {
        return deviceDAO.findAll(page, size, sort, dir, searchValue);
    }

    @Transactional(readOnly = true)
    @Override
    public DeviceDTO getDevice(String id) {
        DeviceEntity entity = deviceDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.DEVICE_NOT_FOUND + ": " + id));
        DeviceDTO dto = new DeviceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPurchaseDate(entity.getPurchaseDate());
        dto.setQuantity(entity.getQuantity());
        dto.setPrice(entity.getPrice());
        return dto;
    }

    @Transactional
    @Override
    public void createDevice(DeviceDTO dto) {
        // Id
        String id = deviceDAO.generateNextId();
        // Insert Device
        DeviceEntity device = new DeviceEntity(
                id,
                dto.getName(),
                dto.getQuantity(),
                dto.getPurchaseDate(),
                dto.getPrice(),
                RecordStatus.ACTIVE.name()
        );
        int rows = deviceDAO.insert(device);
        if (rows == 0) {
            throw new NotFoundException(ErrorMessageConstants.DEVICE_NOT_FOUND + " : " + id);
        }
    }

    @Transactional
    @Override
    public void updateDevice(String id, DeviceDTO dto) {
        // Insert Device
        DeviceEntity device = new DeviceEntity();
        device.setId(id);
        device.setName(dto.getName());
        device.setQuantity(device.getQuantity());
        device.setPurchaseDate(dto.getPurchaseDate());
        device.setPrice(dto.getPrice());
        deviceDAO.updateById(id, device);
    }

    @Transactional
    @Override
    public void deleteDevice(String id) {
        deviceDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.DEVICE_NOT_FOUND + ": " + id));
        deviceDAO.updateStatusById(id, RecordStatus.INACTIVE.name());
    }
}
