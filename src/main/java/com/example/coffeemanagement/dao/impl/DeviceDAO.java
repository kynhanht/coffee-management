package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IDeviceDAO;
import com.example.coffeemanagement.dto.DeviceListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.entity.DeviceEntity;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DeviceDAO implements IDeviceDAO {

    private final DataSource dataSource;

    public DeviceDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public String generateNextId() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                    SELECT MAX(MaThietBi) FROM ThietBi
                    """;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next() && rs.getString(1) != null) {
                String maxId = rs.getString(1); // NV03
                int number = Integer.parseInt(maxId.substring(2));
                return String.format("TB%02d", number + 1);
            }
            return "TB01";
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e.getCause());
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public PageDTO<DeviceListDTO> findAll(int page, int size, String sort, String dir, String searchValue) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;

        Map<String, String> validSortColumns = Map.of("TenThietBi", "TenThietBi", "SoLuong", "SoLuong", "DonGia", "DonGia", "NgayMua", "NgayMua");
        String sortColumn = validSortColumns.getOrDefault(sort, "TenThietBi");
        String direction = dir.equalsIgnoreCase("desc") ? "DESC" : "ASC";
        if (searchValue == null) searchValue = "";

        List<DeviceListDTO> list = new ArrayList<>();
        long total = 0;

        try {
            String countSql = """
                    SELECT COUNT(*)
                    FROM ThietBi TB
                    WHERE TrangThai = 'ACTIVE'
                    AND TenThietBi LIKE ?
                    """;

            String dataSql = """
                    SELECT MaThietBi,
                           TenThietBi,
                           NgayMua,
                           SoLuong,
                           DonGia
                    FROM ThietBi
                    WHERE TrangThai = 'ACTIVE'
                    AND TenThietBi LIKE ?
                    ORDER BY %s %s
                    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                    """.formatted(sortColumn, direction);

            ps1 = conn.prepareStatement(countSql);
            ps1.setString(1, "%" + searchValue + "%");
            rs1 = ps1.executeQuery();
            if (rs1.next()) {
                total = rs1.getLong(1);
            }

            ps2 = conn.prepareStatement(dataSql);
            // Lấy dữ liệu
            int offset = (page - 1) * size;
            ps2.setString(1, "%" + searchValue + "%");
            ps2.setInt(2, offset);
            ps2.setInt(3, size);
            rs2 = ps2.executeQuery();
            while (rs2.next()) {
                DeviceListDTO dto = new DeviceListDTO();
                dto.setId(rs2.getString("MaThietBi"));
                dto.setName(rs2.getString("TenThietBi"));
                Date purchaseDate = rs2.getDate("NgayMua");
                dto.setPurchaseDate(purchaseDate != null ? purchaseDate.toLocalDate() : null);
                dto.setQuantity(rs2.getInt("SoLuong"));
                dto.setPrice(rs2.getBigDecimal("DonGia"));
                list.add(dto);
            }
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps1, rs1);
            DBUtils.close(ps2, rs2);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        int totalPages = (int) Math.ceil((double) total / size);
        return new PageDTO<>(list, page, size, totalPages, total, sort, dir, searchValue);
    }

    @Override
    public Optional<DeviceEntity> findById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = """
                    SELECT *
                    FROM ThietBi
                    WHERE MaThietBi = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                DeviceEntity entity = new DeviceEntity();
                entity.setId(rs.getString("MaThietBi"));
                entity.setName(rs.getString("TenThietBi"));
                entity.setQuantity(rs.getInt("SoLuong"));
                Date purchaseDate = rs.getDate("NgayMua");
                entity.setPurchaseDate(purchaseDate != null ? purchaseDate.toLocalDate() : null);
                entity.setPrice(rs.getBigDecimal("DonGia"));
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
    public int insert(DeviceEntity entity) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;

        try {
            String sql = """
                    INSERT INTO ThietBi 
                    (MaThietBi, TenThietBi, SoLuong, NgayMua, DonGia, TrangThai)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, entity.getId());
            ps.setString(2, entity.getName());
            ps.setInt(3, entity.getQuantity());
            ps.setDate(4, entity.getPurchaseDate() != null ? Date.valueOf(entity.getPurchaseDate()) : null);
            ps.setBigDecimal(5, entity.getPrice());
            ps.setString(6, entity.getStatus());
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int updateById(String id, DeviceEntity entity) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE ThietBi SET TenThietBi = ?, NgayMua = ?, SoLuong = ?, DonGia = ?
                    WHERE MaThietBi = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, entity.getName());
            ps.setDate(2, Date.valueOf(entity.getPurchaseDate()));
            ps.setInt(3, entity.getQuantity());
            ps.setBigDecimal(4, entity.getPrice());
            ps.setString(5, id);
            return ps.executeUpdate();
        } catch (Exception e) {
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
                    UPDATE ThietBi SET TrangThai = ?
                    WHERE MaThietBi = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, id);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
