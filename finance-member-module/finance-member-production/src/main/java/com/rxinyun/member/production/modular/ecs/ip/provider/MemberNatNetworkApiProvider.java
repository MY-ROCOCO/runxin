package com.rxinyun.member.production.modular.ecs.ip.provider;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberNatNetwork;
import com.rxinyun.member.production.modular.ecs.ip.service.MemberNatNetworkService;
import com.rxinyun.production.api.MemberNatNetworkApi;
import com.rxinyun.production.core.pojo.BaseMemberNatNetwork;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberNatNetworkApiProvider implements MemberNatNetworkApi {
   private static final Logger log = LoggerFactory.getLogger(MemberNatNetworkApiProvider.class);
   @Resource
   private MemberNatNetworkService memberNatNetworkService;

   public BaseMemberNatNetwork selectByMaster(String masterId) {
      MemberNatNetwork memberNatNetwork = this.memberNatNetworkService.selectByMaster(masterId);
      log.info("selectByMaster:{}", JSONObject.toJSONString(memberNatNetwork));
      return memberNatNetwork != null ? (BaseMemberNatNetwork)BeanUtil.copyProperties(memberNatNetwork, BaseMemberNatNetwork.class, new String[0]) : null;
   }

   public void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion) {
      this.memberNatNetworkService.updateStatusWithOptimisticLock(id, newStatus, currentVersion);
   }

   public void updateStatus(String id, int code) {
      this.memberNatNetworkService.updateStatus(id, code);
   }

   public void updateStatus(String ip) {
      this.memberNatNetworkService.updateStatus(ip);
   }

   public void updateStatus(List<String> ipList) {
      this.memberNatNetworkService.updateStatus(ipList);
   }

   public BaseMemberNatNetwork selectById(String publicNetworkId) {
      MemberNatNetwork memberNatNetwork = this.memberNatNetworkService.queryEntity(publicNetworkId);
      log.info("selectById:{}", JSONObject.toJSONString(memberNatNetwork));
      return memberNatNetwork != null ? (BaseMemberNatNetwork)BeanUtil.copyProperties(memberNatNetwork, BaseMemberNatNetwork.class, new String[0]) : null;
   }

   public void rollbackStatus(String id) {
      this.memberNatNetworkService.rollbackStatus(id);
   }
}
