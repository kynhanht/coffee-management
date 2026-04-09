package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IOrderDAO;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.model.Order;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;
@Repository
public class OrderDAO implements IOrderDAO {

    private final DataSource dataSource;

    public OrderDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String generateNextId() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                SELECT MAX(MaHoaDon) FROM HoaDon
                """;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next() && rs.getString(1) != null) {
                String maxId = rs.getString(1);
                int number = Integer.parseInt(maxId.substring(2));
                return String.format("HD%02d", number + 1);
            }
            return "HD01";
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public Optional<String> findUnpaidOrderByTableId(String tableId) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                SELECT MaHoaDon
                FROM HoaDon
                WHERE MaBan = ? AND TrangThai = 'UNPAID'
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getString("MaHoaDon"));
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
    public int insert(Order model) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try  {
            String sql = """
                INSERT INTO HoaDon(
                    MaHoaDon,
                    MaBan,
                    MaNhanVien,
                    MaKhuyenMai,
                    TenKhachHang,
                    SdtKhachHang,
                    TongTien,
                    TienKhachDua,
                    TienThoi,
                    NgayGioTao,
                    TrangThai
                    
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, model.getId());
            ps.setString(2, model.getTableId());
            ps.setString(3, model.getEmployeeId());
            ps.setString(4, model.getPromotionId());
            ps.setString(5, model.getCustomerName());
            ps.setString(6, model.getCustomerPhone());
            ps.setBigDecimal(7, model.getTotalAmount());
            ps.setBigDecimal(8, model.getAmountPaid());
            ps.setBigDecimal(9, model.getChangeAmount());
            ps.setTimestamp(10, Timestamp.valueOf(model.getCreatedDate()));
            ps.setString(11, model.getStatus());

            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int updateTotalById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                UPDATE HoaDon
                SET TongTien = (
                    SELECT SUM(SoLuong * GiaTaiThoiDiemBan)
                    FROM ChiTietHoaDon
                    WHERE MaHoaDon = ?
                ),
                NgayGioTao = GETDATE()
                WHERE MaHoaDon = ?
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int updateStatusById(String id, String status) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                UPDATE HoaDon
                SET TrangThai = ?
                WHERE MaHoaDon = ?
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int updateTableIdById(String id, String tableId) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                UPDATE HoaDon SET MaBan = ?
                WHERE MaHoaDon = ?
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableId);
            ps.setString(2, id);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int payOrder(String orderId, BigDecimal amountPaid, BigDecimal changeAmount) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
            UPDATE HoaDon
            SET TienKhachDua = ?, 
                TienThoi = ?, 
                TrangThai = 'PAID'
            WHERE MaHoaDon = ?
        """;
            ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, amountPaid);
            ps.setBigDecimal(2, changeAmount);
            ps.setString(3, orderId);
            return ps.executeUpdate();

        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
