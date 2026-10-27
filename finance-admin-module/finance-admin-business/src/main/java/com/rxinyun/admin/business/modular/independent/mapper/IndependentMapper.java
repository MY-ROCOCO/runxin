package com.rxinyun.admin.business.modular.independent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import java.time.LocalDateTime;
import java.util.List;

public interface IndependentMapper extends BaseMapper<Independent> {
   List<Independent> getServersToCheck(LocalDateTime sevenDaysLater);

   List<Independent> getServersForAutoRenew(LocalDateTime windowEnd);

   void updateServerStatus(String serverId);

   List<Independent> getServersToDeleteCheck();
}
