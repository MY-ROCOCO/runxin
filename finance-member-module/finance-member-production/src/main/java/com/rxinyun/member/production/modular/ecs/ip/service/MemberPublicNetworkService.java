package com.rxinyun.member.production.modular.ecs.ip.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPublicNetwork;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPublicNetworkIdParam;
import com.rxinyun.member.production.modular.ecs.ip.param.MemberPublicNetworkPageParam;
import java.util.List;

public interface MemberPublicNetworkService {
   Page<MemberPublicNetwork> page(MemberPublicNetworkPageParam pageParam);

   MemberPublicNetwork detail(MemberPublicNetworkIdParam serverIpIdParam);

   MemberPublicNetwork queryEntity(String id);

   MemberPublicNetwork selectByMaster(String masterId, String lineId);

   void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion);

   void updateStatus(String id, int code);

   void updateStatus(String ip);

   void updateStatus(List<String> ipList);

   void rollbackStatus(String id);
}
