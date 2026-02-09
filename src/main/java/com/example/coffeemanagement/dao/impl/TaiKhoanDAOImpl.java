package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.dao.TaiKhoanDAO;
import com.example.coffeemanagement.mapper.TaiKhoanMapper;
import com.example.coffeemanagement.model.TaiKhoanModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TaiKhoanDAOImpl implements TaiKhoanDAO {

    private final JdbcTemplate jdbcTemplate;

    public TaiKhoanDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TaiKhoanModel findByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT * FROM TaiKhoan WHERE TenDangNhap = ?";
        return jdbcTemplate.queryForObject(sql, new TaiKhoanMapper(), tenDangNhap);
    }
}
