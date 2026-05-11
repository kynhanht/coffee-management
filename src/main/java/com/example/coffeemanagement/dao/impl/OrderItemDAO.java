package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IOrderItemDAO;
import com.example.coffeemanagement.dto.MergedItemDTO;
import com.example.coffeemanagement.dto.OrderItemDTO;
import com.example.coffeemanagement.entity.OrderItemEntity;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.util.DBUtils;
import com.example.coffeemanagement.util.SystemUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Repository
public class OrderItemDAO implements IOrderItemDAO {

    private final DataSource dataSource;

    public OrderItemDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<OrderItemEntity> findByOrderId(String orderId) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = """
                SELECT 
                MaMonAn, MaHoaDon, SoLuong, GiaTaiThoiDiemBan
                FROM ChiTietHoaDon
                WHERE MaHoaDon = ?
                """;
        List<OrderItemEntity> orderItemList = new ArrayList<>();

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, orderId);
            rs = ps.executeQuery();

            while (rs.next()) {
                OrderItemEntity entity = new OrderItemEntity();
                entity.setOrderId(rs.getString("MaHoaDon"));
                entity.setMenuItemId(rs.getString("MaMonAn"));
                entity.setQuantity(rs.getInt("SoLuong"));
                entity.setCurrentPrice(rs.getBigDecimal("GiaTaiThoiDiemBan"));
                orderItemList.add(entity);
            }

        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return orderItemList;
    }

    @Override
    public int insert(OrderItemEntity entity) {
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
            ps.setString(1, entity.getOrderId());
            ps.setString(2, entity.getMenuItemId());
            ps.setInt(3, entity.getQuantity());
            ps.setBigDecimal(4, entity.getCurrentPrice());
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
    public List<OrderItemDTO> findByTableIdAndOrderStatus(String tableId, String orderStatus) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<OrderItemDTO> result = new ArrayList<>();
        try {
            String sql = """
                    SELECT
                        MA.MaMonAn,
                        MA.TenMonAn,
                        CTHD.SoLuong,
                        CTHD.GiaTaiThoiDiemBan
                    FROM HoaDon HD
                    INNER JOIN ChiTietHoaDon CTHD 
                        ON HD.MaHoaDon = CTHD.MaHoaDon
                    INNER JOIN MonAn MA 
                        ON CTHD.MaMonAn = MA.MaMonAn
                    WHERE HD.MaBan = ?
                      AND HD.TrangThai = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableId);
            ps.setString(2, orderStatus);
            rs = ps.executeQuery();

            while (rs.next()) {
                OrderItemDTO dto = new OrderItemDTO();
                dto.setMenuItemId(rs.getString("MaMonAn"));
                dto.setMenuItemName(rs.getString("TenMonAn"));
                int quantity = rs.getInt("SoLuong");
                dto.setQuantity(quantity);
                BigDecimal currentPrice = rs.getBigDecimal("GiaTaiThoiDiemBan");
                dto.setCurrentPrice(currentPrice);
                BigDecimal lineTotal = currentPrice.multiply(BigDecimal.valueOf(quantity));
                dto.setLineTotal(lineTotal);
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
    public List<MergedItemDTO> findMergedItemsByOrderIds(List<String> orderIds) {
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
                    UPDATE ChiTietHoaDon SET SoLuong = SoLuong + ?
                    WHERE MaHoaDon = ? AND MaMonAn= ?;
                    """;
            ps = conn.prepareStatement(sql);
            ps.setInt(1, delta);
            ps.setString(2, orderId);
            ps.setString(3, menuItemId);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
