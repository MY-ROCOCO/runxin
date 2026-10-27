package com.rxinyun.member.production.modular.ecs.ip.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPrivateNetwork;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPrivateNetworkIdParam;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPrivateNetworkPageParam;

public interface MemberPrivateNetworkService {
   Page<MemberPrivateNetwork> page(MemberPrivateNetworkPageParam pageParam);

   MemberPrivateNetwork detail(MemberPrivateNetworkIdParam PrivateNetworkIdParam);

   MemberPrivateNetwork queryEntity(String id);

   MemberPrivateNetwork selectByMaster(String masterId);

   void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion);

   void updateStatus(String id, int code);

   void updateStatus(String ip);

   void rollbackStatus(String id);
}
