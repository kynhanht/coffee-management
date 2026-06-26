package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IGoodDAO;
import com.example.coffeemanagement.dto.*;
import com.example.coffeemanagement.entity.DeviceEntity;
import com.example.coffeemanagement.entity.GoodEntity;
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
public class GoodDAO implements IGoodDAO {

    private final DataSource dataSource;

    public GoodDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String generateNextId() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                    SELECT MAX(MaHangHoa) FROM HangHoa
                    """;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next() && rs.getString(1) != null) {
                String maxId = rs.getString(1); // NV03
                int number = Integer.parseInt(maxId.substring(2));
                return String.format("HH%02d", number + 1);
            }
            return "HH01";
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e.getCause());
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public PageDTO<GoodListDTO> findAll(int page, int size, String sort, String dir, String searchValue) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;

        Map<String, String> validSortColumns = Map.of("TenHangHoa", "TenHangHoa", "NgayNhap", "NgayNhap", "NgayXuat", "NgayXuat", "SoLuong", "SoLuong", "DonVi", "DonVi", "DonGia", "DonGia", "TongTien", "TongTien");
        String sortColumn = validSortColumns.getOrDefault(sort, "TenHangHoa");
        String direction = dir.equalsIgnoreCase("desc") ? "DESC" : "ASC";
        if (searchValue == null) searchValue = "";

        List<GoodListDTO> list = new ArrayList<>();
        long total = 0;

        try {
            String countSql = """
                    SELECT COUNT(*)
                    FROM HangHoa HH
                    WHERE TrangThai = 'ACTIVE'
                    AND TenHangHoa LIKE ?
                    """;

            String dataSql = """
                    SELECT
                        HH.MaHangHoa,
                        HH.TenHangHoa,
                        NgayNhap,
                        NgayXuat,
                        HH.SoLuong,
                        DVT.TenDonVi AS DonVi,
                        HH.DonGia,
                        HH.SoLuong * HH.DonGia AS TongTien
                    FROM HangHoa HH
                    LEFT JOIN DonViTinh DVT ON HH.MaDonViTinh = DVT.MaDonViTinh
                    WHERE TrangThai = 'ACTIVE' AND TenHangHoa LIKE ?
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
                GoodListDTO dto = new GoodListDTO();
                dto.setId(rs2.getString("MaHangHoa"));
                dto.setName(rs2.getString("TenHangHoa"));
                Timestamp importDate = rs2.getTimestamp("NgayNhap");
                dto.setImportDate(importDate != null ? importDate.toLocalDateTime().toLocalDate() : null);
                Timestamp exportDate = rs2.getTimestamp("NgayXuat");
                dto.setExportDate(exportDate != null ? exportDate.toLocalDateTime().toLocalDate() : null);
                dto.setQuantity(rs2.getInt("SoLuong"));
                dto.setUnitName(rs2.getString("DonVi"));
                dto.setPrice(rs2.getBigDecimal("DonGia"));
                dto.setTotalAmount(rs2.getBigDecimal("TongTien"));
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
    public List<GoodEntity> findAll() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<GoodEntity> list = new ArrayList<>();

        try {
            String sql = """
                    SELECT *
                    FROM HangHoa
                    WHERE TrangThai = 'ACTIVE'
                    """;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                GoodEntity entity = new GoodEntity();
                entity.setId(rs.getString("MaHangHoa"));
                entity.setName(rs.getString("TenHangHoa"));
                entity.setQuantity(rs.getInt("SoLuong"));
                entity.setUnitId(rs.getString("MaDonViTinh"));
                entity.setPrice(rs.getBigDecimal("DonGia"));
                Date importDate = rs.getDate("NgayNhap");
                entity.setImportDate(importDate != null ? importDate.toLocalDate() : null);
                Date exportDate = rs.getDate("NgayXuat");
                entity.setExportDate(exportDate != null ? exportDate.toLocalDate() : null);
                entity.setStatus(rs.getString("TrangThai"));
                list.add(entity);
            }
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return list;
    }

    @Override
    public Optional<GoodEntity> findById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = """
                    SELECT *
                    FROM HangHoa
                    WHERE MaHangHoa = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                GoodEntity entity = new GoodEntity();
                entity.setId(rs.getString("MaHangHoa"));
                entity.setName(rs.getString("TenHangHoa"));
                entity.setQuantity(rs.getInt("SoLuong"));
                entity.setUnitId(rs.getString("MaDonViTinh"));
                entity.setPrice(rs.getBigDecimal("DonGia"));
                Date importDate = rs.getDate("NgayNhap");
                entity.setImportDate(importDate != null ? importDate.toLocalDate() : null);
                Date exportDate = rs.getDate("NgayXuat");
                entity.setExportDate(exportDate != null ? exportDate.toLocalDate() : null);
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
    public int insert(GoodEntity entity) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;

        try {
            String sql = """
                    INSERT INTO HangHoa 
                    (MaHangHoa, TenHangHoa, SoLuong, MaDonViTinh, DonGia, NgayNhap, NgayXuat, TrangThai)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, entity.getId());
            ps.setString(2, entity.getName());
            ps.setInt(3, entity.getQuantity());
            ps.setString(4, entity.getUnitId());
            ps.setBigDecimal(5, entity.getPrice());
            ps.setDate(6, entity.getImportDate() != null ? Date.valueOf(entity.getImportDate()) : null);
            ps.setDate(7, entity.getExportDate() != null ? Date.valueOf(entity.getExportDate()) : null);
            ps.setString(8, entity.getStatus());
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int updateById(String id, GoodEntity entity) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE HangHoa SET TenHangHoa = ?, SoLuong = ?, DonGia = ?, MaDonViTinh = ?, NgayNhap = ?, NgayXuat = ?
                    WHERE MaHangHoa = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, entity.getName());
            ps.setInt(2, entity.getQuantity());
            ps.setBigDecimal(3, entity.getPrice());
            ps.setString(4, entity.getUnitId());
            ps.setDate(5, entity.getImportDate() != null ? Date.valueOf(entity.getImportDate()) : null);
            ps.setDate(6, entity.getExportDate() != null ? Date.valueOf(entity.getExportDate()) : null);
            ps.setString(7, entity.getId());
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
                    UPDATE HangHoa SET TrangThai = ?
                    WHERE MaHangHoa = ?
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

    @Override
    public int updateImport(String id, ImportExportGoodDTO dto) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE HangHoa SET SoLuong = SoLuong + ?, NgayNhap = ?
                    WHERE MaHangHoa = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dto.getQuantity());
            ps.setDate(2, dto.getImportDate() != null ? Date.valueOf(dto.getImportDate()) : null);
            ps.setString(3, id);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int updateExport(String id, ImportExportGoodDTO dto) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE HangHoa SET SoLuong = SoLuong - ?, NgayXuat = ?
                    WHERE MaHangHoa = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setInt(1, dto.getQuantity());
            ps.setDate(2, dto.getExportDate() != null ? Date.valueOf(dto.getExportDate()) : null);
            ps.setString(3, id);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

}
