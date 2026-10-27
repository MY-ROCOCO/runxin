package com.rxinyun.admin.business.modular.esc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import java.time.LocalDateTime;
import java.util.List;

public interface ServerMapper extends BaseMapper<Server> {
   List<Server> getServersToCheck(LocalDateTime sevenDaysLater);

   List<Server> getServersForAutoRenew(LocalDateTime windowEnd);

   void updateServerStatus(String serverId);

   List<Server> getServersToDeleteCheck();
}
