package com.rxinyun.production.api;

import com.rxinyun.production.core.pojo.BaseMemberPrivateNetwork;

public interface MemberPrivateNetworkApi {
   BaseMemberPrivateNetwork selectByMaster(String masterId);

   void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion);

   void updateStatus(String id, int code);

   void updateStatus(String ip);

   void rollbackStatus(String id);
}
