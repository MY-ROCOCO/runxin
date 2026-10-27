package com.rxinyun.member.production.modular.ecs.master.service;

import com.rxinyun.member.production.modular.ecs.master.entity.MemberMaster;
import com.rxinyun.member.production.modular.ecs.master.param.MemberMasterIdParam;

public interface MemberMasterService {
   MemberMaster detail(MemberMasterIdParam serverMasterIdParam);

   MemberMaster queryEntity(String id);

   MemberMaster queryByLineId(String lineId);

   void updateHostStats(String masterId, Integer instanceCount, Integer ramRemaining, Long diskRemaining);
}
