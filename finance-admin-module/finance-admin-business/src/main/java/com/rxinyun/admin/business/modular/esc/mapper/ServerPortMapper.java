package com.rxinyun.admin.business.modular.esc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.business.modular.esc.entity.ServerPort;
import org.apache.ibatis.annotations.Param;

public interface ServerPortMapper extends BaseMapper<ServerPort> {
   Integer checkPortExist(@Param("masterIp") String masterIp, @Param("port") int port);

   int deletePort(@Param("masterIp") String masterIp, @Param("port") int port);

   int deleteByServerId(@Param("serverId") String serverId);

   int deleteById(@Param("id") String id);
}
