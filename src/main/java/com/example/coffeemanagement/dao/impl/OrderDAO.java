package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IOrderDAO;
import com.example.coffeemanagement.dto.OrderDTO;
import com.example.coffeemanagement.entity.OrderEntity;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.util.DBUtils;
import com.example.coffeemanagement.util.SystemUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Locale;
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
    public Optional<OrderEntity> findById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                SELECT *
                FROM HoaDon
                WHERE MaBan = ?
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setId(rs.getString("MaHoaDon"));
                orderEntity.setTableId(rs.getString("MaBan"));
                orderEntity.setEmployeeId(rs.getString("MaNhanVien"));
                orderEntity.setPromotionId(rs.getString("MaKhuyenMai"));
                orderEntity.setCustomerName(rs.getString("TenKhachHang"));
                orderEntity.setCustomerPhone(rs.getString("SdtKhachHang"));
                orderEntity.setTotalAmount(rs.getBigDecimal("TongTien"));
                orderEntity.setAmountPaid(rs.getBigDecimal("TienKhachDua"));
                orderEntity.setChangeAmount(rs.getBigDecimal("TienThoi"));
                Timestamp ts = rs.getTimestamp("createdDate");
                LocalDateTime createdDate = ts != null ? ts.toLocalDateTime() : null;
                orderEntity.setCreatedDate(createdDate);
                orderEntity.setStatus(rs.getString("TrangThai"));
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
    public Optional<OrderDTO> findDetailById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = """
            SELECT 
                o.MaHoaDon,
                t.TenBan AS TenBan,
                e.HoTen AS TenNhanVien,
                p.TenKhuyenMai AS TenKhuyenMai,
                o.TenKhachHang,
                o.SdtKhachHang,
                o.TongTien,
                o.TienKhachDua,
                o.TienThoi,
                o.NgayGioTao,
                o.TrangThai
            FROM HoaDon o
            LEFT JOIN Ban t ON o.MaBan = t.MaBan
            LEFT JOIN NhanVien e ON o.MaNhanVien = e.MaNhanVien
            LEFT JOIN KhuyenMai p ON o.MaKhuyenMai = p.MaKhuyenMai
            WHERE o.MaHoaDon = ?
        """;

            ps = conn.prepareStatement(sql);
            ps.setString(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                OrderDTO dto = new OrderDTO();

                dto.setId(rs.getString("MaHoaDon"));
                dto.setTableName(rs.getString("TenBan"));
                dto.setEmployeeName(rs.getString("TenNhanVien"));
                dto.setPromotionName(rs.getString("TenKhuyenMai"));
                dto.setCustomerName(rs.getString("TenKhachHang"));
                dto.setCustomerPhone(rs.getString("SdtKhachHang"));
                dto.setTotalAmount(rs.getBigDecimal("TongTien"));
                dto.setAmountPaid(rs.getBigDecimal("TienKhachDua"));
                dto.setChangeAmount(rs.getBigDecimal("TienThoi"));
                Timestamp createdDate = rs.getTimestamp("NgayGioTao");
                dto.setCreatedDate(createdDate != null ? createdDate.toLocalDateTime() : null);
                dto.setStatus(rs.getString("TrangThai"));
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
    public Optional<String> findOrderIdByTableIdAndStatus(String tableId, String status) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                SELECT MaHoaDon
                FROM HoaDon
                WHERE MaBan = ? AND TrangThai = ?
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableId);
            ps.setString(2, status);
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
    public Optional<BigDecimal> findTotalAmountById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                SELECT TongTien
                FROM HoaDon
                WHERE MaHoaDon = ?
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(rs.getBigDecimal("TongTien"));
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
    public int insert(OrderEntity entity) {
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
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, entity.getId());
            ps.setString(2, entity.getTableId());
            ps.setString(3, entity.getEmployeeId());
            ps.setString(4, entity.getPromotionId());
            ps.setString(5, entity.getCustomerName());
            ps.setString(6, entity.getCustomerPhone());
            ps.setBigDecimal(7, entity.getTotalAmount());
            ps.setBigDecimal(8, entity.getAmountPaid());
            ps.setBigDecimal(9, entity.getChangeAmount());
            ps.setTimestamp(10, Timestamp.valueOf(entity.getCreatedDate()));
            ps.setString(11, entity.getStatus());

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
                )
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
    public int payOrder(String orderId, BigDecimal amountPaid, BigDecimal changeAmount, String status) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
            UPDATE HoaDon
            SET TienKhachDua = ?, 
                TienThoi = ?, 
                TrangThai = ?
            WHERE MaHoaDon = ?
        """;
            ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, amountPaid);
            ps.setBigDecimal(2, changeAmount);
            ps.setString(3, status);
            ps.setString(4, orderId);
            return ps.executeUpdate();

        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
