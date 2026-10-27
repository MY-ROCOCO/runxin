package com.rxinyun.admin.business.modular.esc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.business.modular.esc.entity.ServerIp;
import java.util.List;

public interface ServerIpMapper extends BaseMapper<ServerIp> {
   void deleteByServerId(String serverId);

   void removeByIds(List<String> list);
}
