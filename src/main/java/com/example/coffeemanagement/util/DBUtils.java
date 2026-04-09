package com.example.coffeemanagement.util;

import com.example.coffeemanagement.constant.ErrorMessageConstants;
import com.example.coffeemanagement.exception.InternalException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DBUtils {

    public static void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null && !ps.isClosed()) ps.close();
            if (rs != null && !rs.isClosed()) rs.close();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }
    }

    public static void close(PreparedStatement ps) {
        try {
            if (ps != null && !ps.isClosed()) ps.close();
        } catch (Exception e) {
            throw new InternalException(ErrorMessageConstants.DATABASE_ERROR, e);
        }
    }

}
