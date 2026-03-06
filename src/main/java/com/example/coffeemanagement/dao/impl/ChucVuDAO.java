package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IChucVuDAO;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.model.ChucVu;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Repository
public class ChucVuDAO implements IChucVuDAO {

    private final DataSource dataSource;

    public ChucVuDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public List<ChucVu> findAll() {
        String sql = """
            SELECT maChucVu, tenChucVu, luong
            FROM ChucVu
            """;

        List<ChucVu> danhSach = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ChucVu cv = new ChucVu();
                cv.setMaChucVu(rs.getString("maChucVu"));
                cv.setTenChucVu(rs.getString("tenChucVu"));
                cv.setLuong(rs.getBigDecimal("luong")); // giống cách bạn đang dùng
                danhSach.add(cv);
            }
        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }

        return danhSach;
    }
}
