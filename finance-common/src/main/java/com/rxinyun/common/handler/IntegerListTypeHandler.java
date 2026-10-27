package com.rxinyun.common.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

@MappedJdbcTypes({JdbcType.VARCHAR})
@MappedTypes({List.class})
public class IntegerListTypeHandler implements TypeHandler<List<Integer>> {
   private static final ObjectMapper objectMapper = new ObjectMapper();

   public void setParameter(PreparedStatement preparedStatement, int i, List<Integer> integers, JdbcType jdbcType) throws SQLException {
      if (integers == null) {
         preparedStatement.setString(i, null);
      } else {
         try {
            String json = objectMapper.writeValueAsString(integers);
            preparedStatement.setString(i, json);
         } catch (JsonProcessingException var8) {
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < integers.size(); j++) {
               if (j == integers.size() - 1) {
                  sb.append(integers.get(j));
               } else {
                  sb.append(integers.get(j)).append(",");
               }
            }

            preparedStatement.setString(i, sb.toString());
         }
      }
   }

   public List<Integer> getResult(ResultSet resultSet, String s) throws SQLException {
      String resultString = resultSet.getString(s);
      return this.parseStringToList(resultString);
   }

   public List<Integer> getResult(ResultSet resultSet, int i) throws SQLException {
      String resultString = resultSet.getString(i);
      return this.parseStringToList(resultString);
   }

   public List<Integer> getResult(CallableStatement callableStatement, int i) throws SQLException {
      String resultString = callableStatement.getString(i);
      return this.parseStringToList(resultString);
   }

   private List<Integer> parseStringToList(String resultString) {
      if (StringUtils.isEmpty(resultString)) {
         return null;
      } else {
         try {
            return (List<Integer>)objectMapper.readValue(resultString, new TypeReference<List<Integer>>() {});
         } catch (JsonProcessingException var12) {
            try {
               String cleanedString = resultString.replaceAll("[\\[\\]]", "");
               if (cleanedString.trim().isEmpty()) {
                  return Collections.emptyList();
               } else {
                  String[] stringList = cleanedString.split(",");
                  List<Integer> integerList = new ArrayList<>();

                  for (String value : stringList) {
                     String trimmedValue = value.trim();
                     if (!trimmedValue.isEmpty()) {
                        integerList.add(Integer.valueOf(trimmedValue));
                     }
                  }

                  return integerList;
               }
            } catch (NumberFormatException var11) {
               return Collections.emptyList();
            }
         }
      }
   }
}
