package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IOrderItemDAO;
import com.example.coffeemanagement.dto.MergedItemDTO;
import com.example.coffeemanagement.dto.OrderMenuItemDTO;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.model.OrderItem;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class OrderItemDAO implements IOrderItemDAO {

    private final DataSource dataSource;

    public OrderItemDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int insert(OrderItem model) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    INSERT INTO ChiTietHoaDon(
                        MaHoaDon,
                        MaMonAn,
                        SoLuong,
                        GiaTaiThoiDiemBan
                    )
                    VALUES (?, ?, ?, ?)
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, model.getOrderId());
            ps.setString(2, model.getMenuItemId());
            ps.setInt(3, model.getQuantity());
            ps.setBigDecimal(4, model.getCurrentPrice());
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int deleteByOrderId(String orderId) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    DELETE FROM ChiTietHoaDon
                    WHERE MaHoaDon = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, orderId);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public List<OrderMenuItemDTO> findOrderByTableId(String tableId) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<OrderMenuItemDTO> result = new ArrayList<>();
        try {
            String sql = """
                    SELECT
                        MA.MaMonAn,
                        MA.TenMonAn,
                        CTHD.SoLuong,
                        CTHD.GiaTaiThoiDiemBan
                    FROM HoaDon HD
                    JOIN ChiTietHoaDon CTHD 
                        ON HD.MaHoaDon = CTHD.MaHoaDon
                    JOIN MonAn MA 
                        ON CTHD.MaMonAn = MA.MaMonAn
                    WHERE HD.MaBan = ?
                      AND HD.TrangThai = 'UNPAID'
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableId);
            rs = ps.executeQuery();

            while (rs.next()) {
                OrderMenuItemDTO dto = new OrderMenuItemDTO();
                dto.setId(rs.getString("MaMonAn"));
                dto.setName(rs.getString("TenMonAn"));
                dto.setQuantity(rs.getInt("SoLuong"));
                dto.setCurrentPrice(rs.getBigDecimal("GiaTaiThoiDiemBan"));
                result.add(dto);
            }
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return result;
    }

    @Override
    public List<MergedItemDTO> findMergedItems(List<String> orderIds) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = orderIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        String sql = """
                SELECT
                    MaMonAn,
                    SUM(SoLuong) AS SoLuong,
                    MAX(GiaTaiThoiDiemBan) AS GiaTaiThoiDiemBan
                FROM ChiTietHoaDon
                WHERE MaHoaDon IN (%s)
                GROUP BY MaMonAn
                """.formatted(placeholders);

        List<MergedItemDTO> result = new ArrayList<>();
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < orderIds.size(); i++) {
                ps.setString(i + 1, orderIds.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                MergedItemDTO dto = new MergedItemDTO();
                dto.setId(rs.getString("MaMonAn"));
                dto.setQuantity(rs.getInt("SoLuong"));
                dto.setCurrentPrice(rs.getBigDecimal("GiaTaiThoiDiemBan"));
                result.add(dto);
            }
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return result;
    }

    @Override
    public int updateQuantityById(String orderId, String menuItemId, int delta) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE ChiTietHoaDon SET SoLuong = CASE WHEN (SoLuong + ?) < 0 THEN 0 ELSE SoLuong + ? END
                    WHERE MaHoaDon = ? AND MaMonAn= ?;
                    """;
            ps = conn.prepareStatement(sql);
            ps.setInt(1, delta);
            ps.setInt(2, delta);
            ps.setString(3, orderId);
            ps.setString(4, menuItemId);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
