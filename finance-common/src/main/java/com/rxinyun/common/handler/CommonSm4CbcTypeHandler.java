package com.rxinyun.common.handler;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.rxinyun.common.util.CommonCryptogramUtil;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

@MappedJdbcTypes({JdbcType.VARCHAR})
public class CommonSm4CbcTypeHandler<T> extends BaseTypeHandler<T> {
   public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
      ps.setString(i, CommonCryptogramUtil.doSm4CbcEncrypt((String)parameter));
   }

   public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
      String columnValue = rs.getString(columnName);
      return (T)(StringUtils.isBlank(columnValue) ? columnValue : CommonCryptogramUtil.doSm4CbcDecrypt(columnValue));
   }

   public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
      String columnValue = rs.getString(columnIndex);
      return (T)(StringUtils.isBlank(columnValue) ? columnValue : CommonCryptogramUtil.doSm4CbcDecrypt(columnValue));
   }

   public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
      String columnValue = cs.getString(columnIndex);
      return (T)(StringUtils.isBlank(columnValue) ? columnValue : CommonCryptogramUtil.doSm4CbcDecrypt(columnValue));
   }
}
