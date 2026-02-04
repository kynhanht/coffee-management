package com.example.coffeemanagement.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {

    @Id
    @Column(name = "MaTaiKhoan", columnDefinition = "varchar(20)")
    private String maTaiKhoan;

    @Column(name = "TenDangNhap", columnDefinition = "nvarchar(50)")
    private String tenDangNhap;

    @Column(name = "MatKhau", columnDefinition = "TEXT")
    private String matKhau;

    @Column(name = "QuyenHan", columnDefinition = "nvarchar(50)")
    private String quyenHan;

    @Column(name = "Anh", columnDefinition = "VARBINARY(MAX)")
    private String anh;

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
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

    public String getAnh() {
        return anh;
    }

    public void setAnh(String anh) {
        this.anh = anh;
    }
}
