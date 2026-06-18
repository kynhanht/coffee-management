package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.*;

public interface IDeviceService {
    PageDTO<DeviceListDTO> getAllDevices(int page, int size, String sort, String dir, String searchValue);

    DeviceDTO getDevice(String id);

    void createDevice(DeviceDTO dto);

    void updateDevice(String id, DeviceDTO dto);

    void deleteDevice(String id);
}
