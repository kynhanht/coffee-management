package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.constant.SystemConstants;
import com.example.coffeemanagement.dao.IEmployeeDAO;
import com.example.coffeemanagement.dto.EmployeeDTO;
import com.example.coffeemanagement.dto.EmployeeDetailDTO;
import com.example.coffeemanagement.dto.EmployeeListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.model.Employee;
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
    public EmployeeDetailDTO getDetail(String username) {
        return employeeDAO.findDetailByUsername(username)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.EMPLOYEE_NOT_FOUND + ": " + username));
    }

    @Transactional
    @Override
    public void updateProfile(String username, EmployeeDetailDTO dto) {

        Employee employee = new Employee();
        // mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        // Quyền hạn
        String role = dto.getPositionId().equals(SystemConstants.CODE_ROLE_ADMIN) ? SystemConstants.ROLE_ADMIN : SystemConstants.ROLE_USER;
        // Lưu file
        String fileName = fileStorageService.storeFile(dto.getFile());


        if(fileName != null) dto.setPicture(fileName);

        employee.setId(dto.getId());
        employee.setPositionId(dto.getPositionId());
        employee.setFullName(dto.getFullName());
        employee.setPhone(dto.getPhone());
        employee.setAddress(dto.getAddress());
        employee.setPicture(dto.getPicture());
        employee.setUsername(username);
        employee.setPassword(encodedPassword);
        employee.setRole(role);

        int rows = employeeDAO.updateProfile(username, employee);
        if (rows == 0) {
            throw new NotFoundException(ErrorMessageConstants.ACCOUNT_NOT_FOUND + " : " + dto.getUsername());
        }
    }
    @Transactional(readOnly = true)
    @Override
    public PageDTO<EmployeeListDTO> getAll(int page, int size, String sort, String dir, String searchValue) {
        return employeeDAO.findAll(page, size, sort, dir, searchValue);
    }
    @Transactional
    @Override
    public void create(EmployeeDTO dto) {
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
        Employee employee = new Employee(
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
        employeeDAO.insert(employee);

    }
    @Transactional(readOnly = true)
    @Override
    public EmployeeDTO getById(String id) {
        return employeeDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.EMPLOYEE_NOT_FOUND + ": " + id));
    }

    @Transactional
    @Override
    public void update(String id, EmployeeDTO dto) {
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
        Employee nhanVien = new Employee();
        nhanVien.setId(id);
        nhanVien.setPositionId(dto.getPositionId());
        nhanVien.setFullName(dto.getFullName());
        nhanVien.setPhone(dto.getPhone());
        nhanVien.setAddress(dto.getAddress());
        nhanVien.setPicture(dto.getPicture());
        nhanVien.setUsername(dto.getUsername());
        nhanVien.setPassword(encodedPassword);
        nhanVien.setRole(role);
        employeeDAO.update(id, nhanVien);

    }
    @Transactional
    @Override
    public void delete(String id) {
        employeeDAO.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessageConstants.EMPLOYEE_NOT_FOUND + ": " + id));
        employeeDAO.delete(id);
    }


}
