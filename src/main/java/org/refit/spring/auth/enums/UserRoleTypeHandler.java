package org.refit.spring.auth.enums;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRoleTypeHandler extends BaseTypeHandler<UserRole> {
    //spring legacy에서 enum 사용 가능하도록 하는 코드입니다.
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserRole parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.name());
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return UserRole.valueOf(rs.getString(columnName));
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return UserRole.valueOf(rs.getString(columnIndex));
    }

    @Override
    public UserRole getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return UserRole.valueOf(cs.getString(columnIndex));
    }
}
