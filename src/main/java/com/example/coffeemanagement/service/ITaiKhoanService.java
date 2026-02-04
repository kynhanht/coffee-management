package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.TaiKhoanDTO;

public interface ITaiKhoanService {

    TaiKhoanDTO findByTenDangNhap(String tenDangNhap);
}
