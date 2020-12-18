package com.lwh.util;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

@MappedJdbcTypes(JdbcType.DATE)
@MappedTypes(String.class)
public class DateTypeHandler implements TypeHandler<String> {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public String getResult(ResultSet resultSet, String s) throws SQLException {
        return sdf.format(resultSet.getDate(s));
    }

    @Override
    public String getResult(ResultSet resultSet, int i) throws SQLException {
        return sdf.format(resultSet.getDate(i));
    }

    @Override
    public String getResult(CallableStatement callableStatement, int i) throws SQLException {
        return sdf.format(callableStatement.getDate(i));
    }
}
