package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.NhanVienDetailDTO;

import java.util.Optional;

public interface INhanVienDAO {

    public Optional<NhanVienDetailDTO> findDetailByTenDangNhap(String tenDangNhap);

    public int updateByTenDangNhap(NhanVienDetailDTO dto);
}
