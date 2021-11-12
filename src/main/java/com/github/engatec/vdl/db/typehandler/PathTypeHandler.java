package com.github.engatec.vdl.db.typehandler;

import java.nio.file.Path;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

@MappedJdbcTypes(JdbcType.VARCHAR)
public class PathTypeHandler extends BaseTypeHandler<Path> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Path parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public Path getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String str = rs.getString(columnName);
        return str == null ? null : Path.of(str);
    }

    @Override
    public Path getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String str = rs.getString(columnIndex);
        return str == null ? null : Path.of(str);
    }

    @Override
    public Path getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String str = cs.getString(columnIndex);
        return str == null ? null : Path.of(str);
    }
}
