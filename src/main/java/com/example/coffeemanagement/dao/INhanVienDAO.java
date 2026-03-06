package com.example.coffeemanagement.dao;

import com.example.coffeemanagement.dto.NhanVienDTO;
import com.example.coffeemanagement.dto.NhanVienDetailDTO;
import com.example.coffeemanagement.dto.NhanVienListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.model.NhanVien;

import java.util.List;
import java.util.Optional;

public interface INhanVienDAO {

    String generateNextId();

    Optional<NhanVien> findByTenDangNhap(String tenDangNhap);

    Optional<NhanVienDTO> findById(String maNhanVien);

    Optional<NhanVienDetailDTO> findDetailByTenDangNhap(String tenDangNhap);

    int updateProfile(String tenDangNhap, NhanVien model);

    PageDTO<NhanVienListDTO> findAll(int page, int size, String sort, String dir, String searchValue);

    void insert(NhanVien model);
    void update(String maNhanVien, NhanVien model);

    void delete(String maNhanVien);

}
