package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IPositionDAO;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.model.Position;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PositionDAO implements IPositionDAO {

    private final DataSource dataSource;

    public PositionDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Position> findAll() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Position> danhSach = new ArrayList<>();

        try {
            String sql = """
            SELECT maChucVu, tenChucVu, luong
            FROM ChucVu
            """;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Position model = new Position();
                model.setId(rs.getString("maChucVu"));
                model.setName(rs.getString("tenChucVu"));
                model.setSalary(rs.getBigDecimal("luong")); // giống cách bạn đang dùng
                danhSach.add(model);
            }
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return danhSach;
    }
}
