package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.model.TaiKhoanModel;

public interface TaiKhoanDAO {

    TaiKhoanModel findByTenDangNhap(String tenDangNhap);
}
