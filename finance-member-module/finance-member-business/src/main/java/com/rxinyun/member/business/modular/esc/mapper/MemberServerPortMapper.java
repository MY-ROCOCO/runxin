package com.rxinyun.member.business.modular.esc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.business.modular.esc.entity.MemberServerPort;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MemberServerPortMapper extends BaseMapper<MemberServerPort> {
   Integer checkPortExist(@Param("masterId") String masterId, @Param("port") int port);

   int deletePort(@Param("masterId") String masterId, @Param("port") int port);

   void delete(@Param("serverId") String serverId);

   void deleteByIds(List<String> memberServerPortIdList);
}
