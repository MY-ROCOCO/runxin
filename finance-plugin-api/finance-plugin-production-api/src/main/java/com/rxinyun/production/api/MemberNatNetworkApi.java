package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseMemberNatNetwork;
import java.util.List;

public interface MemberNatNetworkApi {
   BaseMemberNatNetwork selectByMaster(String masterId);

   void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion);

   void updateStatus(String id, int code);

   void updateStatus(String ip);

   void updateStatus(List<String> ipList);

   BaseMemberNatNetwork selectById(String natNetworkId);

   void rollbackStatus(String id);
}
