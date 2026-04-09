package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IReservationDetailDAO;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.model.ReservationDetail;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

@Repository
public class ReservationDetailDAO implements IReservationDetailDAO {

    private final DataSource dataSource;

    public ReservationDetailDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int insert(ReservationDetail model) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """       
                    INSERT INTO ChiTietDatBan(MaBan, MaNhanVien, TenKhachHang, SdtKhachHang, NgayGioDat)
                    VALUES (?, ?, ?, ?, ?)
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, model.getTableId());
            ps.setString(2, model.getEmployeeId());
            ps.setString(3, model.getCustomerName());
            ps.setString(4, model.getCustomerPhone());
            ps.setTimestamp(5, Timestamp.valueOf(model.getReservationDate()));
            return ps.executeUpdate();

        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int updateTableId(String sourceTableId, String targetTableId) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE ChiTietDatBan 
                    SET MaBan = ?, NgayGioDat = GETDATE()
                    WHERE MaBan = ?
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, targetTableId);
            ps.setString(2, sourceTableId);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int updateByTableId(String tableId, ReservationDetail model) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE ChiTietDatBan 
                    SET MaNhanVien = ?, TenKhachHang = ?, SdtKhachHang = ?, NgayGioDat = ?
                    WHERE MaBan = ?
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, model.getEmployeeId());
            ps.setString(2, model.getCustomerName());
            ps.setString(3, model.getCustomerPhone());
            ps.setTimestamp(4, Timestamp.valueOf(model.getReservationDate()));
            ps.setString(5, model.getTableId());
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public int deleteByTableId(String tableId) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                   DELETE ChiTietDatBan
                   WHERE MaBan = ?
                """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, tableId);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }
}
