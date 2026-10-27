package com.rxinyun.common.handler;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.util.StringUtils;

@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes({List.class})
public class ListToStringTypeHandler extends BaseTypeHandler<List<String>> {
   private static ObjectMapper objectMapper = new ObjectMapper();

   public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
      ps.setObject(i, JSON.toJSONString(parameter));
   }

   public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
      return this.getString(rs.getString(columnName));
   }

   public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
      return this.getString(rs.getString(columnIndex));
   }

   public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
      return this.getString(cs.getString(columnIndex));
   }

   private List<String> getString(String value) {
      if (StringUtils.hasText(value)) {
         try {
            CollectionType type = objectMapper.getTypeFactory().constructCollectionType(ArrayList.class, String.class);
            return (List<String>)objectMapper.readValue(value, type);
         } catch (JsonProcessingException var3) {
            var3.printStackTrace();
         }
      }

      return null;
   }
}
