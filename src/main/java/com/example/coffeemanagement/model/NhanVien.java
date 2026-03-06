package com.example.coffeemanagement.model;

public class NhanVien {

    private String maNhanVien;

    private String maChucVu;

    private String hoTen;

    private String soDienThoai;

    private String diaChi;

    private String anh;

    private String tenDangNhap;

    private String matKhau;

    private String quyenHan;

    private String trangThai;


    public NhanVien() {
    }

    public NhanVien(String maNhanVien, String maChucVu, String hoTen, String soDienThoai, String diaChi, String anh, String tenDangNhap, String matKhau, String quyenHan, String trangThai) {
        this.maNhanVien = maNhanVien;
        this.maChucVu = maChucVu;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
        this.anh = anh;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.quyenHan = quyenHan;
        this.trangThai = trangThai;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getMaChucVu() {
        return maChucVu;
    }

    public void setMaChucVu(String maChucVu) {
        this.maChucVu = maChucVu;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getQuyenHan() {
        return quyenHan;
    }

    public void setQuyenHan(String quyenHan) {
        this.quyenHan = quyenHan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
