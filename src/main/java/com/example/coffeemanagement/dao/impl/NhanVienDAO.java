package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.constant.SystemConstants;
import com.example.coffeemanagement.dao.INhanVienDAO;
import com.example.coffeemanagement.dto.NhanVienDTO;
import com.example.coffeemanagement.dto.NhanVienDetailDTO;
import com.example.coffeemanagement.dto.NhanVienListDTO;
import com.example.coffeemanagement.dto.PageDTO;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.model.NhanVien;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class NhanVienDAO implements INhanVienDAO {
    private final DataSource dataSource;

    public NhanVienDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Override
    public String generateNextId() {

        String sql = """
                SELECT MAX(MaNhanVien) FROM NhanVien
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next() && rs.getString(1) != null) {
                String maxId = rs.getString(1); // NV03
                int number = Integer.parseInt(maxId.substring(2));
                return String.format("NV%02d", number + 1);
            }
            return "NV01";
        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }
    }

    @Override
    public Optional<NhanVien> findByTenDangNhap(String tenDangNhap) {
        String sql = """
        SELECT NV.MaNhanVien, NV.HoTen, NV.DiaChi,
               NV.MaChucVu, NV.MatKhau, NV.Anh, NV.SoDienThoai, NV.TenDangNhap, NV.QuyenHan
        FROM NhanVien NV
        WHERE NV.tenDangNhap = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                NhanVien model = new NhanVien();
                model.setMaNhanVien(rs.getString("MaNhanVien"));
                model.setHoTen(rs.getString("HoTen"));
                model.setDiaChi(rs.getString("DiaChi"));
                model.setMaChucVu(rs.getString("MaChucVu"));
                model.setSoDienThoai(rs.getString("SoDienThoai"));
                model.setAnh(rs.getString("Anh"));
                model.setTenDangNhap(rs.getString("TenDangNhap"));
                model.setMatKhau(rs.getString("MatKhau"));
                model.setQuyenHan(rs.getString("QuyenHan"));
                return Optional.of(model);
            }
        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<NhanVienDTO> findById(String maNhanVien) {
        String sql = """
        SELECT MaNhanVien, HoTen, DiaChi,
            MaChucVu, SoDienThoai, TenDangNhap,
            MatKhau, Anh
        FROM NhanVien
        WHERE MaNhanVien = ?
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNhanVien);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                NhanVienDTO dto = new NhanVienDTO();
                dto.setMaNhanVien(rs.getString("MaNhanVien"));
                dto.setHoTen(rs.getString("HoTen"));
                dto.setDiaChi(rs.getString("DiaChi"));
                dto.setMaChucVu(rs.getString("MaChucVu"));
                dto.setSoDienThoai(rs.getString("SoDienThoai"));
                dto.setTenDangNhap(rs.getString("TenDangNhap"));
                dto.setMatKhau(rs.getString("MatKhau"));
                dto.setAnh(rs.getString("Anh"));
                return Optional.of(dto);
            }

        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<NhanVienDetailDTO> findDetailByTenDangNhap(String tenDangNhap) {

        String sql = """
                SELECT NV.MaNhanVien, NV.HoTen, CV.MaChucVu, CV.TenChucVu,
                       NV.DiaChi, NV.SoDienThoai, CV.Luong, NV.Anh,
                       NV.TenDangNhap, NV.MatKhau
                FROM NhanVien NV
                INNER JOIN ChucVu CV ON NV.MaChucVu = CV.MaChucVu
                WHERE NV.TenDangNhap = ?
                """;

        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhanVienDetailDTO dto = new NhanVienDetailDTO();
                    dto.setMaNhanVien(rs.getString("MaNhanVien"));
                    dto.setHoTen(rs.getString("HoTen"));
                    dto.setMaChucVu(rs.getString("MaChucVu"));
                    dto.setTenChucVu(rs.getString("TenChucVu"));
                    dto.setDiaChi(rs.getString("DiaChi"));
                    dto.setSoDienThoai(rs.getString("SoDienThoai"));
                    dto.setLuong(rs.getBigDecimal("Luong"));
                    dto.setAnh(rs.getString("Anh"));
                    dto.setTenDangNhap(rs.getString("TenDangNhap"));
                    dto.setMatKhau(rs.getString("MatKhau"));
                    return Optional.of(dto);
                }
            }
        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }

        return Optional.empty();
    }

    @Override
    public int updateProfile(String maNhanVien, NhanVien model) {
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

        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, model.getHoTen());
            ps.setString(2, model.getDiaChi());
            ps.setString(3, model.getSoDienThoai());
            ps.setString(4, model.getMaChucVu());
            ps.setString(5, model.getAnh());
            ps.setString(6, model.getQuyenHan());
            ps.setString(7, maNhanVien);
            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }
    }

    @Override
    public PageDTO<NhanVienListDTO> findAll(int page, int size, String sort, String dir, String searchValue) {
        Map<String, String> validSortColumns = Map.of("HoTen", "NV.HoTen", "TenChucVu", "CV.TenChucVu", "Luong", "CV.Luong");

        String sortColumn = validSortColumns.getOrDefault(sort, "NV.HoTen");

        String direction = dir.equalsIgnoreCase("desc") ? "DESC" : "ASC";

        if(searchValue == null) searchValue="";

        String countSql = """
            SELECT COUNT(*)
            FROM NhanVien NV
            INNER JOIN ChucVu CV ON NV.MaChucVu = CV.MaChucVu
            WHERE NV.TrangThai = ?
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
            WHERE NV.TrangThai = ?
            AND NV.HoTen LIKE ?
            ORDER BY %s %s
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """.formatted(sortColumn, direction);

        List<NhanVienListDTO> list = new ArrayList<>();
        long total = 0;

        try (Connection conn = dataSource.getConnection()) {

            // Đếm tổng
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {

                ps.setString(1, SystemConstants.ACTIVE_USER);
                ps.setString(2, "%" + searchValue + "%");

                try(ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        total = rs.getLong(1);
                    }
                }

            }
            // Lấy dữ liệu
            try (PreparedStatement ps = conn.prepareStatement(dataSql)) {

                int offset = (page - 1) * size;
                ps.setString(1, SystemConstants.ACTIVE_USER);
                ps.setString(2, "%" + searchValue + "%");
                ps.setInt(3, offset);
                ps.setInt(4, size);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        NhanVienListDTO dto = new NhanVienListDTO();
                        dto.setMaNhanVien(rs.getString("MaNhanVien"));
                        dto.setHoTen(rs.getString("HoTen"));
                        dto.setTenChucVu(rs.getString("TenChucVu"));
                        dto.setLuong(rs.getBigDecimal("Luong"));
                        list.add(dto);
                    }
                }
            }

        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }

        int totalPages = (int) Math.ceil((double) total / size);

        return new PageDTO<>(list, page, size, totalPages, total, sort, dir, searchValue);
    }
    @Override
    public void insert(NhanVien model) {

        String sql = """
                INSERT INTO NhanVien 
                (MaNhanVien, MaChucVu, HoTen, SoDienThoai, DiaChi, Anh, TenDangNhap, MatKhau, QuyenHan, TrangThai)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, model.getMaNhanVien());
            ps.setString(2, model.getMaChucVu());
            ps.setString(3, model.getHoTen());
            ps.setString(4, model.getSoDienThoai());
            ps.setString(5, model.getDiaChi());
            ps.setString(6, model.getAnh());
            ps.setString(7, model.getTenDangNhap());
            ps.setString(8, model.getMatKhau());
            ps.setString(9, model.getQuyenHan());
            ps.setString(10, model.getTrangThai());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }
    }

    @Override
    public void update(String maNhanVien, NhanVien model) {
        String sql = """
                UPDATE NhanVien SET HoTen = ?, DiaChi = ?, MaChucVu = ?, SoDienThoai = ?, Anh = ?, TenDangNhap = ?, MatKhau = ?, QuyenHan = ?
                WHERE MaNhanVien = ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, model.getHoTen());
            ps.setString(2, model.getDiaChi());
            ps.setString(3, model.getMaChucVu());
            ps.setString(4, model.getSoDienThoai());
            ps.setString(5, model.getAnh());
            ps.setString(6, model.getTenDangNhap());
            ps.setString(7, model.getMatKhau());
            ps.setString(8, model.getQuyenHan());
            ps.setString(9, maNhanVien);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }
    }

    @Override
    public void delete(String maNhanVien) {
        String sql = """
                UPDATE NhanVien SET TrangThai = ?
                WHERE MaNhanVien = ?
                """;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, SystemConstants.INACTIVE_USER);
            ps.setString(2, maNhanVien);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }
    }


}
