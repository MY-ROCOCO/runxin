package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseMemberMaster;

public interface MemberMasterApi {
   BaseMemberMaster selectByLineId(String LineId);

   BaseMemberMaster selectByMasterId(String lineId);

   void updateHostStats(String masterId, Integer instanceCount, Integer ramRemaining, Long diskRemaining);
}
