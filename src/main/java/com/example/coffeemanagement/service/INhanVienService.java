package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.NhanVienDetailDTO;

public interface INhanVienService {

    public NhanVienDetailDTO getDetail(String tenDangNhap);

    public void updateNhanVien(NhanVienDetailDTO dto);
}
