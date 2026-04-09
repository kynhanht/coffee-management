package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IMenuItemDAO;
import com.example.coffeemanagement.dto.MenuItemDTO;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MenuItemDAO implements IMenuItemDAO {

    private final DataSource dataSource;

    public MenuItemDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<MenuItemDTO> findById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                    SELECT
                        MaMonAn,
                        TenMonAn,
                        GiaHienTai,
                        LoaiMonAn,
                        TrangThai
                    FROM MonAn
                    WHERE MaMonAn = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                MenuItemDTO dto = new MenuItemDTO();
                dto.setId(rs.getString("MaMonAn"));
                dto.setName(rs.getString("TenMonAn"));
                dto.setPrice(rs.getBigDecimal("GiaHienTai"));
                return Optional.of(dto);
            }
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return Optional.empty();
    }

    @Override
    public List<MenuItemDTO> findByStatus(String status) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<MenuItemDTO> danhSach = new ArrayList<>();
        try {
            String sql = """
                    SELECT MaMonAn, TenMonAn, GiaHienTai
                    FROM MonAn
                    WHERE TrangThai = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            rs = ps.executeQuery();
            while (rs.next()) {
                MenuItemDTO dto = new MenuItemDTO();
                dto.setId(rs.getString("MaMonAn"));
                dto.setName(rs.getString("TenMonAn"));
                dto.setPrice(rs.getBigDecimal("GiaHienTai"));
                danhSach.add(dto);
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
