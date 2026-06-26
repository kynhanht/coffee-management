package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IExportGoodDetailDAO;
import com.example.coffeemanagement.entity.ExportGoodDetailEntity;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class ExportGoodDetailDAO implements IExportGoodDetailDAO {
    private final DataSource dataSource;

    public ExportGoodDetailDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String generateNextId() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                    SELECT MAX(MaDonXuatHangHoa) FROM ChiTietXuatHang
                    """;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next() && rs.getString(1) != null) {
                String maxId = rs.getString(1); // NV03
                int number = Integer.parseInt(maxId.substring(2));
                return String.format("XH%03d", number + 1);
            }
            return "XH001";
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e.getCause());
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int insert(ExportGoodDetailEntity entity) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;

        try {
            String sql = """
                    INSERT INTO ChiTietXuatHang
                    (MaDonXuatHangHoa, MaNhanVien, MaHangHoa, NgayXuat, DonGia, SoLuong)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, entity.getId());
            ps.setString(2, entity.getEmployeeId());
            ps.setString(3, entity.getGoodId());
            ps.setDate(4, entity.getExportDate() != null ? Date.valueOf(entity.getExportDate()) : null);
            ps.setBigDecimal(5, entity.getPrice());
            ps.setInt(6, entity.getQuantity());
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
