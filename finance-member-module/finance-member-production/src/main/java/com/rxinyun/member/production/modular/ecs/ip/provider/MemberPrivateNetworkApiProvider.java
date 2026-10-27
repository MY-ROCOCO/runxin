package com.rxinyun.member.production.modular.ecs.ip.provider;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPrivateNetwork;
import com.rxinyun.member.production.modular.ecs.ip.service.MemberPrivateNetworkService;
import com.rxinyun.production.api.MemberPrivateNetworkApi;
import com.rxinyun.production.core.pojo.BaseMemberPrivateNetwork;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberPrivateNetworkApiProvider implements MemberPrivateNetworkApi {
   private static final Logger log = LoggerFactory.getLogger(MemberPrivateNetworkApiProvider.class);
   @Resource
   private MemberPrivateNetworkService memberPrivateNetworkService;

   public BaseMemberPrivateNetwork selectByMaster(String masterId) {
      MemberPrivateNetwork memberPrivateNetwork = this.memberPrivateNetworkService.selectByMaster(masterId);
      log.info("selectByMaster:{}", JSONObject.toJSONString(memberPrivateNetwork));
      return memberPrivateNetwork != null
         ? (BaseMemberPrivateNetwork)BeanUtil.copyProperties(memberPrivateNetwork, BaseMemberPrivateNetwork.class, new String[0])
         : null;
   }

   public void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion) {
      this.memberPrivateNetworkService.updateStatusWithOptimisticLock(id, newStatus, currentVersion);
   }

   public void updateStatus(String id, int code) {
      this.memberPrivateNetworkService.updateStatus(id, code);
   }

   public void updateStatus(String ip) {
      this.memberPrivateNetworkService.updateStatus(ip);
   }

   public void rollbackStatus(String id) {
      this.memberPrivateNetworkService.rollbackStatus(id);
   }
}
