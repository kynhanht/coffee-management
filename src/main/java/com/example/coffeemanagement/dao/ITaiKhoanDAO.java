package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.model.TaiKhoan;

import java.util.Optional;

public interface ITaiKhoanDAO {

    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
}
