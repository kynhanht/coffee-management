package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IMenuItemDAO;
import com.example.coffeemanagement.entity.MenuItemEntity;
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
    public Optional<MenuItemEntity> findById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                    SELECT *
                    FROM MonAn
                    WHERE MaMonAn = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                MenuItemEntity entity = new MenuItemEntity();
                entity.setId(rs.getString("MaMonAn"));
                entity.setName(rs.getString("TenMonAn"));
                entity.setPrice(rs.getBigDecimal("GiaHienTai"));
                entity.setType(rs.getString("LoaiMonAn"));
                entity.setStatus(rs.getString("TrangThai"));
                return Optional.of(entity);
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
    public List<MenuItemEntity> findByStatus(String status) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<MenuItemEntity> menuItemList = new ArrayList<>();
        try {
            String sql = """
                    SELECT *
                    FROM MonAn
                    WHERE TrangThai = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            rs = ps.executeQuery();
            while (rs.next()) {
                MenuItemEntity entity = new MenuItemEntity();
                entity.setId(rs.getString("MaMonAn"));
                entity.setName(rs.getString("TenMonAn"));
                entity.setPrice(rs.getBigDecimal("GiaHienTai"));
                entity.setType(rs.getString("LoaiMonAn"));
                entity.setStatus(rs.getString("TrangThai"));
                menuItemList.add(entity);
            }
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return menuItemList;
    }

}
