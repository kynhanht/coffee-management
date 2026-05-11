package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.ITableDAO;
import com.example.coffeemanagement.dto.TableInfoDTO;
import com.example.coffeemanagement.dto.TableOptionDTO;
import com.example.coffeemanagement.entity.TableEntity;
import com.example.coffeemanagement.enums.TableStatus;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TableDAO implements ITableDAO {

    private final DataSource dataSource;

    public TableDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<TableEntity> findById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = """
                    SELECT MaBan, TenBan, TrangThai
                    FROM Ban
                    WHERE MaBan = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                TableEntity entity = new TableEntity();
                entity.setId(rs.getString("MaBan"));
                entity.setName(rs.getString("TenBan"));
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
    public List<TableEntity> findAll() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;

        String sql = """
                SELECT MaBan, TenBan, TrangThai
                FROM Ban
                """;
        List<TableEntity> tableList = new ArrayList<>();

        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TableEntity entity = new TableEntity();
                entity.setId(rs.getString("MaBan"));
                entity.setName(rs.getString("TenBan"));
                entity.setStatus(rs.getString("TrangThai"));
                tableList.add(entity);
            }

        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return tableList;
    }

    @Override
    public Optional<TableInfoDTO> findTableInfoById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = """
                    SELECT
                        TOP 1
                        CTDB.TenKhachHang,
                        CTDB.NgayGioDat,
                        B.TenBan
                    FROM Ban B
                    JOIN ChiTietDatBan CTDB 
                        ON B.MaBan = CTDB.MaBan
                    WHERE B.MaBan = ?
                    """;
            ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                TableInfoDTO dto = new TableInfoDTO();

                dto.setCustomerName(rs.getString("TenKhachHang"));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H'h' dd/MM/yyyy");
                Optional
                        .ofNullable(rs.getTimestamp("NgayGioDat"))
                        .map(Timestamp::toLocalDateTime)
                        .map(dt -> dt.format(formatter))
                        .ifPresent(dto::setReservationDate);
                dto.setTableName(rs.getString("TenBan"));
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
    public int updateStatusById(String id, String status) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        try {
            String sql = """
                    UPDATE Ban
                    SET TrangThai = ?
                    WHERE MaBan = ?
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
    public List<TableOptionDTO> findByStatuses(List<TableStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return Collections.emptyList();
        }

        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TableOptionDTO> tableList = new ArrayList<>();

        try {
            String placeholders = statuses.stream()
                    .map(s -> "?")
                    .collect(Collectors.joining(","));

            String sql = """
                SELECT MaBan, TenBan
                FROM Ban
                WHERE TrangThai IN (%s)
                """.formatted(placeholders);

            ps = conn.prepareStatement(sql);

            for (int i = 0; i < statuses.size(); i++) {
                ps.setString(i + 1, statuses.get(i).name());
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                TableOptionDTO dto = new TableOptionDTO();
                dto.setId(rs.getString("MaBan"));
                dto.setName(rs.getString("TenBan"));
                tableList.add(dto);
            }
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return tableList;
    }


}
