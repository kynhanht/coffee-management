package com.example.coffeemanagement.mapper;

import com.example.coffeemanagement.model.TaiKhoanModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class TaiKhoanMapper implements RowMapper<TaiKhoanModel> {


    @Override
    public TaiKhoanModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        TaiKhoanModel taiKhoan = new TaiKhoanModel();
        taiKhoan.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
        taiKhoan.setTenDangNhap(rs.getString("TenDangNhap"));
        taiKhoan.setMatKhau(rs.getString("MatKhau"));
        taiKhoan.setQuyenHan(rs.getString("QuyenHan"));
        return taiKhoan;
    }
}
