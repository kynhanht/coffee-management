package com.example.coffeemanagement.dto;

import java.math.BigDecimal;

public class NhanVienListDTO {

    private String maNhanVien;
    private String hoTen;
    private String tenChucVu;
    private BigDecimal luong;

    public String getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(String maNhanVien) { this.maNhanVien = maNhanVien; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getTenChucVu() { return tenChucVu; }
    public void setTenChucVu(String tenChucVu) { this.tenChucVu = tenChucVu; }

    public BigDecimal getLuong() { return luong; }
    public void setLuong(BigDecimal luong) { this.luong = luong; }
}
