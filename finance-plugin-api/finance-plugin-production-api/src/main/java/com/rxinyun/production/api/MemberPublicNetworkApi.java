package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseMemberPublicNetwork;
import java.util.List;

public interface MemberPublicNetworkApi {
   BaseMemberPublicNetwork selectByMaster(String masterId, String lineId);

   void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion);

   void updateStatus(String id, int code);

   void updateStatus(String ip);

   void updateStatus(List<String> ipList);

   BaseMemberPublicNetwork selectById(String publicNetworkId);

   void rollbackStatus(String id);
}
