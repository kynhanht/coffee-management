package com.example.coffeemanagement.dao.impl;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.dao.IUnitDAO;
import com.example.coffeemanagement.entity.UnitEntity;
import com.example.coffeemanagement.exception.InternalException;
import com.example.coffeemanagement.util.DBUtils;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UnitDAO implements IUnitDAO {
    private final DataSource dataSource;

    public UnitDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<UnitEntity> findAll() {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<UnitEntity> positionList = new ArrayList<>();

        try {
            String sql = """
            SELECT maDonViTinh, tenDonVi
            FROM DonViTinh
            """;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                UnitEntity model = new UnitEntity();
                model.setId(rs.getString("maDonViTinh"));
                model.setName(rs.getString("tenDonVi"));
                positionList.add(model);
            }
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        } finally {
            DBUtils.close(ps, rs);
            DataSourceUtils.releaseConnection(conn, dataSource);
        }

        return positionList;
    }
}
