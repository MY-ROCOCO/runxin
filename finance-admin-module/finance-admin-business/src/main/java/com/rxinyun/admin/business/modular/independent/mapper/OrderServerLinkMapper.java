package com.rxinyun.admin.business.modular.independent.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface OrderServerLinkMapper {
   @Select({"SELECT ORDER_NO FROM ORDER_SERVER WHERE SERVER_ID = #{serverId} ORDER BY ID DESC LIMIT 1"})
   String selectLatestOrderNoByServerId(@Param("serverId") String serverId);
}
