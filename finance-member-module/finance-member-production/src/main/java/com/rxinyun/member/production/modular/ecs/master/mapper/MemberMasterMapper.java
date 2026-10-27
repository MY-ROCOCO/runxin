package com.rxinyun.member.production.modular.ecs.master.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rxinyun.member.production.modular.ecs.master.entity.MemberMaster;
import org.apache.ibatis.annotations.Param;

public interface MemberMasterMapper extends BaseMapper<MemberMaster> {
   MemberMaster selectFirstByLineId(@Param("lineId") String lineId, @Param("status") Integer status, @Param("masterIndex") Integer masterIndex);

   int updateHostStats(
      @Param("masterId") String masterId,
      @Param("instanceCount") Integer instanceCount,
      @Param("ramRemaining") Integer ramRemaining,
      @Param("diskRemaining") Long diskRemaining
   );
}
