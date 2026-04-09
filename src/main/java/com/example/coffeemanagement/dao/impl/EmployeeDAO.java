package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IEmployeeDAO;
import com.example.coffeemanagement.dto.EmployeeDTO;
import com.example.coffeemanagement.dto.EmployeeDetailDTO;
import com.example.coffeemanagement.dto.EmployeeListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.model.Employee;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EmployeeDAO implements IEmployeeDAO {
    private final DataSource dataSource;

    public EmployeeDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String generateNextId() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                    SELECT MAX(MaNhanVien) FROM NhanVien
                    """;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next() && rs.getString(1) != null) {
                String maxId = rs.getString(1); // NV03
                int number = Integer.parseInt(maxId.substring(2));
                return String.format("NV%02d", number + 1);
            }
            return "NV01";
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public Optional<Employee> findByUsername(String username) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                    SELECT NV.MaNhanVien, NV.HoTen, NV.DiaChi,
                           NV.MaChucVu, NV.MatKhau, NV.Anh, NV.SoDienThoai, NV.TenDangNhap, NV.QuyenHan
                    FROM NhanVien NV
                    WHERE NV.tenDangNhap = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                Employee model = new Employee();
                model.setId(rs.getString("MaNhanVien"));
                model.setFullName(rs.getString("HoTen"));
                model.setAddress(rs.getString("DiaChi"));
                model.setPositionId(rs.getString("MaChucVu"));
                model.setPhone(rs.getString("SoDienThoai"));
                model.setPicture(rs.getString("Anh"));
                model.setUsername(rs.getString("TenDangNhap"));
                model.setPassword(rs.getString("MatKhau"));
                model.setRole(rs.getString("QuyenHan"));
                return Optional.of(model);
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
    public Optional<EmployeeDTO> findById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = """
                    SELECT MaNhanVien,MaChucVu, HoTen, SoDienThoai,
                        DiaChi, Anh, TenDangNhap, MatKhau
                    FROM NhanVien
                    WHERE MaNhanVien = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                EmployeeDTO dto = new EmployeeDTO();
                dto.setId(rs.getString("MaNhanVien"));
                dto.setPositionId(rs.getString("MaChucVu"));
                dto.setFullName(rs.getString("HoTen"));
                dto.setPhone(rs.getString("SoDienThoai"));
                dto.setAddress(rs.getString("DiaChi"));
                dto.setPicture(rs.getString("Anh"));
                dto.setUsername(rs.getString("TenDangNhap"));
                dto.setPassword(rs.getString("MatKhau"));
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
    public Optional<EmployeeDetailDTO> findDetailByUsername(String username) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                    SELECT NV.MaNhanVien, NV.HoTen, CV.MaChucVu, CV.TenChucVu,
                           NV.DiaChi, NV.SoDienThoai, CV.Luong, NV.Anh,
                           NV.TenDangNhap, NV.MatKhau
                    FROM NhanVien NV
                    INNER JOIN ChucVu CV ON NV.MaChucVu = CV.MaChucVu
                    WHERE NV.TenDangNhap = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                EmployeeDetailDTO dto = new EmployeeDetailDTO();
                dto.setId(rs.getString("MaNhanVien"));
                dto.setFullName(rs.getString("HoTen"));
                dto.setPositionId(rs.getString("MaChucVu"));
                dto.setPositionName(rs.getString("TenChucVu"));
                dto.setAddress(rs.getString("DiaChi"));
                dto.setPhone(rs.getString("SoDienThoai"));
                dto.setSalary(rs.getBigDecimal("Luong"));
                dto.setPicture(rs.getString("Anh"));
                dto.setUsername(rs.getString("TenDangNhap"));
                dto.setPassword(rs.getString("MatKhau"));
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
    public int updateProfile(String id, Employee model) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE NV
                    SET NV.HoTen = ?,
                        NV.DiaChi = ?,
                        NV.SoDienThoai = ?,
                        NV.MaChucVu = ?,
                        NV.Anh = ?,
                        NV.QuyenHan = ?
                    FROM NhanVien NV
                    WHERE NV.MaNhanVien = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, model.getFullName());
            ps.setString(2, model.getAddress());
            ps.setString(3, model.getPhone());
            ps.setString(4, model.getPositionId());
            ps.setString(5, model.getPicture());
            ps.setString(6, model.getRole());
            ps.setString(7, id);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public PageDTO<EmployeeListDTO> findAll(int page, int size, String sort, String dir, String searchValue) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps1 = null;
        ResultSet rs1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs2 = null;

        Map<String, String> validSortColumns = Map.of("HoTen", "NV.HoTen", "TenChucVu", "CV.TenChucVu", "Luong", "CV.Luong");
        String sortColumn = validSortColumns.getOrDefault(sort, "NV.HoTen");
        String direction = dir.equalsIgnoreCase("desc") ? "DESC" : "ASC";
        if (searchValue == null) searchValue = "";

        List<EmployeeListDTO> list = new ArrayList<>();
        long total = 0;

        try {
            String countSql = """
                    SELECT COUNT(*)
                    FROM NhanVien NV
                    INNER JOIN ChucVu CV ON NV.MaChucVu = CV.MaChucVu
                    WHERE NV.TrangThai = 'ACTIVE'
                    AND NV.HoTen LIKE ?
                    """;

            String dataSql = """
                    SELECT NV.MaNhanVien,
                           NV.HoTen,
                           CV.TenChucVu,
                           CV.Luong
                    FROM NhanVien NV
                    INNER JOIN ChucVu CV
                        ON NV.MaChucVu = CV.MaChucVu
                    WHERE NV.TrangThai = 'ACTIVE'
                    AND NV.HoTen LIKE ?
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
                EmployeeListDTO dto = new EmployeeListDTO();
                dto.setId(rs2.getString("MaNhanVien"));
                dto.setFullName(rs2.getString("HoTen"));
                dto.setPositionName(rs2.getString("TenChucVu"));
                dto.setSalary(rs2.getBigDecimal("Luong"));
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
    public void insert(Employee model) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;

        try {
            String sql = """
                    INSERT INTO NhanVien 
                    (MaNhanVien, MaChucVu, HoTen, SoDienThoai, DiaChi, Anh, TenDangNhap, MatKhau, QuyenHan, TrangThai)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, model.getId());
            ps.setString(2, model.getPositionId());
            ps.setString(3, model.getFullName());
            ps.setString(4, model.getPhone());
            ps.setString(5, model.getAddress());
            ps.setString(6, model.getPicture());
            ps.setString(7, model.getUsername());
            ps.setString(8, model.getPassword());
            ps.setString(9, model.getRole());
            ps.setString(10, model.getStatus());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void update(String id, Employee model) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE NhanVien SET HoTen = ?, DiaChi = ?, MaChucVu = ?, SoDienThoai = ?, Anh = ?, TenDangNhap = ?, MatKhau = ?, QuyenHan = ?
                    WHERE MaNhanVien = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, model.getFullName());
            ps.setString(2, model.getAddress());
            ps.setString(3, model.getPositionId());
            ps.setString(4, model.getPhone());
            ps.setString(5, model.getPicture());
            ps.setString(6, model.getUsername());
            ps.setString(7, model.getPassword());
            ps.setString(8, model.getRole());
            ps.setString(9, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public void delete(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE NhanVien SET TrangThai = 'INACTIVE'
                    WHERE MaNhanVien = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }


}
