package com.example.coffeemanagement.service;

import com.example.coffeemanagement.dto.NhanVienDTO;
import com.example.coffeemanagement.dto.NhanVienDetailDTO;
import com.example.coffeemanagement.dto.NhanVienListDTO;
import com.example.coffeemanagement.dto.PageDTO;

public interface INhanVienService {

    NhanVienDetailDTO getDetail(String tenDangNhap);

    void updateProfile(String tenDangNhap, NhanVienDetailDTO dto);

    PageDTO<NhanVienListDTO> getAll(int page, int size, String sort, String dir, String searchValue);

    void createNhanVien(NhanVienDTO dto);

    NhanVienDTO getNhanVienById(String maNhanVien);

    void updateNhanVien(String maNhanVien, NhanVienDTO dto);

    void deleteNhanVien(String maNhanVien);
}
