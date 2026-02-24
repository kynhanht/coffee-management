package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.INhanVienDAO;
import com.example.coffeemanagement.dto.NhanVienDetailDTO;
import com.example.coffeemanagement.exception.InternalException;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
@Repository
public class NhanVienDAO implements INhanVienDAO {
    private final DataSource dataSource;

    public NhanVienDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<NhanVienDetailDTO> findDetailByTenDangNhap(String tenDangNhap) {

        String sql = """
                SELECT NV.MaNhanVien, NV.HoTen, CV.TenChucVu,
                       NV.DiaChi, NV.SoDienThoai,
                       CV.Luong,
                       TK.TenDangNhap, TK.MatKhau
                FROM NhanVien NV
                INNER JOIN ChucVu CV ON NV.MaChucVu = CV.MaChucVu
                INNER JOIN TaiKhoan TK ON NV.MaTaiKhoan = TK.MaTaiKhoan
                WHERE TK.TenDangNhap = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tenDangNhap);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    NhanVienDetailDTO dto = new NhanVienDetailDTO();
                    dto.setMaNhanVien(rs.getString("MaNhanVien"));
                    dto.setHoTen(rs.getString("HoTen"));
                    dto.setTenChucVu(rs.getString("TenChucVu"));
                    dto.setDiaChi(rs.getString("DiaChi"));
                    dto.setSoDienThoai(rs.getString("SoDienThoai"));
                    dto.setLuong(rs.getBigDecimal("Luong"));
                    dto.setTenDangNhap(rs.getString("TenDangNhap"));
                    dto.setMatKhau(rs.getString("MatKhau"));
                    return Optional.of(dto);
                }
            }
        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.SYSTEM_ERROR, e);
        }

        return Optional.empty();
    }

    @Override
    public int updateByTenDangNhap(NhanVienDetailDTO dto)  {
        String sql = """
                UPDATE NV
                SET NV.HoTen = ?,
                    NV.DiaChi = ?,
                    NV.SoDienThoai = ?
                FROM NhanVien NV
                INNER JOIN TaiKhoan TK
                    ON NV.MaTaiKhoan = TK.MaTaiKhoan
                WHERE TK.TenDangNhap = ?
                """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getHoTen());
            ps.setString(2, dto.getDiaChi());
            ps.setString(3, dto.getSoDienThoai());
            ps.setString(4, dto.getTenDangNhap());

            return ps.executeUpdate();

        } catch (SQLException e) {
            throw new InternalException(ErrorMessageConstants.SYSTEM_ERROR, e);
        }
    }
}
