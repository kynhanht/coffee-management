package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.constant.SystemConstants;
import com.example.coffeemanagement.dao.IEmployeeDAO;
import com.example.coffeemanagement.dto.EmployeeDTO;
import com.example.coffeemanagement.dto.EmployeeListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.dto.request.EmployeeProfileRequest;
import com.example.coffeemanagement.dto.response.EmployeeProfileResponse;
import com.example.coffeemanagement.entity.EmployeeEntity;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.service.IEmployeeService;
import com.example.coffeemanagement.service.IFileStorageService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService implements IEmployeeService {

    private final IEmployeeDAO employeeDAO;
    private final IFileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;


    public EmployeeService(IEmployeeDAO employeeDAO, IFileStorageService fileStorageService, PasswordEncoder passwordEncoder) {
        this.employeeDAO = employeeDAO;
        this.fileStorageService = fileStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @Override
    public EmployeeProfileResponse getProfile(String id) {
        return employeeDAO.findProfileById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.EMPLOYEE_NOT_FOUND + ": " + id));
    }

    @Transactional
    @Override
    public void updateProfile(String id, EmployeeProfileRequest request) {

        EmployeeEntity entity = new EmployeeEntity();

        // Lưu file
        String fileName = fileStorageService.storeFile(request.getPictureFile());

        if(fileName != null) request.setPicture(fileName);
        entity.setId(request.getEmployeeId());
        entity.setFullName(request.getFullName());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        entity.setPicture(request.getPicture());

        int rows = employeeDAO.updateProfileById(id, entity);
        if (rows == 0) {
            throw new NotFoundException(ErrorMessageConstants.EMPLOYEE_NOT_FOUND + " : " + id);
        }
    }
    @Transactional(readOnly = true)
    @Override
    public PageDTO<EmployeeListDTO> getAllEmployees(int page, int size, String sort, String dir, String searchValue) {
        return employeeDAO.findAll(page, size, sort, dir, searchValue);
    }
    @Transactional
    @Override
    public void createEmployee(EmployeeDTO dto) {
        // Sinh mã
        String id = employeeDAO.generateNextId();

        // Lưu file
        String fileName = fileStorageService.storeFile(dto.getFile());
        // mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // Quyền hạn
        String role = dto.getPositionId().equals(SystemConstants.CODE_ROLE_ADMIN) ? SystemConstants.ROLE_ADMIN : SystemConstants.ROLE_USER;

        // Trạng thái
        String status = SystemConstants.ACTIVE_USER;

        // Insert nhân viên
        EmployeeEntity employeeEntity = new EmployeeEntity(
                id,
                dto.getPositionId(),
                dto.getFullName(),
                dto.getPhone(),
                dto.getAddress(),
                fileName,
                dto.getUsername(),
                encodedPassword,
                role,
                status
                );
        int rows = employeeDAO.insert(employeeEntity);
        if (rows == 0) {
            throw new NotFoundException(ErrorMessageConstants.EMPLOYEE_NOT_FOUND + " : " + id);
        }

    }
    @Transactional(readOnly = true)
    @Override
    public EmployeeDTO getEmployee(String id) {
        EmployeeEntity entity=  employeeDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.EMPLOYEE_NOT_FOUND + ": " + id));
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setAddress(entity.getAddress());
        dto.setPositionId(entity.getPositionId());
        dto.setPhone(entity.getPhone());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setPicture(entity.getPicture());
        return dto;
    }

    @Transactional
    @Override
    public void updateEmployee(String id, EmployeeDTO dto) {
        // Lưu file
        String fileName = fileStorageService.storeFile(dto.getFile());

        if(fileName != null){
            dto.setPicture(fileName);
        }
        // Quyền hạn
        String role = dto.getPositionId().equals(SystemConstants.CODE_ROLE_ADMIN) ? SystemConstants.ROLE_ADMIN : SystemConstants.ROLE_USER;
        // mã hóa mật khẩu

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        // Insert nhân viên
        EmployeeEntity nhanVien = new EmployeeEntity();
        nhanVien.setId(id);
        nhanVien.setPositionId(dto.getPositionId());
        nhanVien.setFullName(dto.getFullName());
        nhanVien.setPhone(dto.getPhone());
        nhanVien.setAddress(dto.getAddress());
        nhanVien.setPicture(dto.getPicture());
        nhanVien.setUsername(dto.getUsername());
        nhanVien.setPassword(encodedPassword);
        nhanVien.setRole(role);
        employeeDAO.updateById(id, nhanVien);

    }
    @Transactional
    @Override
    public void deleteEmployee(String id) {
        employeeDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.EMPLOYEE_NOT_FOUND + ": " + id));
        employeeDAO.deleteById(id);
    }


}
