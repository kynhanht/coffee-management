package com.example.coffeemanagement.service.impl;

import com.example.coffeemanagement.converter.TaiKhoanConverter;
import com.example.coffeemanagement.dto.TaiKhoanDTO;
import com.example.coffeemanagement.entity.TaiKhoan;
import com.example.coffeemanagement.exception.NotFoundException;
import com.example.coffeemanagement.repository.TaiKhoanRepository;
import com.example.coffeemanagement.service.ITaiKhoanService;
import org.springframework.stereotype.Service;

@Service
public class TaiKhoanService implements ITaiKhoanService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final TaiKhoanConverter taiKhoanConverter;


    public TaiKhoanService(TaiKhoanRepository taiKhoanRepository, TaiKhoanConverter taiKhoanConverter) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.taiKhoanConverter = taiKhoanConverter;
    }


    @Override
    public TaiKhoanDTO findByTenDangNhap(String tenDangNhap) {
        TaiKhoan entity = taiKhoanRepository
                .findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new NotFoundException("Không tồn tại tên đăng nhập: " + tenDangNhap));
        return taiKhoanConverter.converterToDTO(entity);
    }
}
