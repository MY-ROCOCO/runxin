package com.rxinyun.member.production.modular.ecs.ip.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberNatNetwork;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberNatNetworkIdParam;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberNatNetworkPageParam;
import java.util.List;

public interface MemberNatNetworkService {
   Page<MemberNatNetwork> page(MemberNatNetworkPageParam pageParam);

   MemberNatNetwork detail(MemberNatNetworkIdParam memberNatNetworkIdParam);

   MemberNatNetwork queryEntity(String id);

   MemberNatNetwork selectByMaster(String masterId);

   void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion);

   void updateStatus(String id, int code);

   void updateStatus(String ip);

   void updateStatus(List<String> ipList);

   void rollbackStatus(String id);
}
