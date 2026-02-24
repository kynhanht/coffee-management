package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.ITaiKhoanDAO;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.model.TaiKhoan;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class TaiKhoanDAO implements ITaiKhoanDAO {
    private final DataSource dataSource;

    public TaiKhoanDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
        try(Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    TaiKhoan model = new TaiKhoan();
                    model.setMaTaiKhoan(rs.getString("MaTaiKhoan"));
                    model.setTenDangNhap(rs.getString("TenDangNhap"));
                    model.setMatKhau(rs.getString("MatKhau"));
                    model.setQuyenHan(rs.getString("QuyenHan"));
                    return Optional.of(model);
                }
            }
        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.SYSTEM_ERROR, e);
        }
        return Optional.empty();
    }
}
