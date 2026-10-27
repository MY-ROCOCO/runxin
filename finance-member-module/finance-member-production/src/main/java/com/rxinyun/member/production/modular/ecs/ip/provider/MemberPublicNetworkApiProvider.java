package com.rxinyun.member.production.modular.ecs.ip.provider;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.member.production.modular.ecs.ip.entity.MemberPublicNetwork;
import com.rxinyun.member.production.modular.ecs.ip.service.MemberPublicNetworkService;
import com.rxinyun.production.api.MemberPublicNetworkApi;
import com.rxinyun.production.core.pojo.BaseMemberPublicNetwork;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberPublicNetworkApiProvider implements MemberPublicNetworkApi {
   private static final Logger log = LoggerFactory.getLogger(MemberPublicNetworkApiProvider.class);
   @Resource
   private MemberPublicNetworkService memberPublicNetworkService;

   public BaseMemberPublicNetwork selectByMaster(String masterId, String lineId) {
      MemberPublicNetwork memberPublicNetwork = this.memberPublicNetworkService.selectByMaster(masterId, lineId);
      log.info("selectByMaster:{}", JSONObject.toJSONString(memberPublicNetwork));
      return memberPublicNetwork != null
         ? (BaseMemberPublicNetwork)BeanUtil.copyProperties(memberPublicNetwork, BaseMemberPublicNetwork.class, new String[0])
         : null;
   }

   public void updateStatusWithOptimisticLock(String id, int newStatus, int currentVersion) {
      this.memberPublicNetworkService.updateStatusWithOptimisticLock(id, newStatus, currentVersion);
   }

   public void updateStatus(String id, int code) {
      this.memberPublicNetworkService.updateStatus(id, code);
   }

   public void updateStatus(String ip) {
      this.memberPublicNetworkService.updateStatus(ip);
   }

   public void updateStatus(List<String> ipList) {
      this.memberPublicNetworkService.updateStatus(ipList);
   }

   public BaseMemberPublicNetwork selectById(String publicNetworkId) {
      MemberPublicNetwork memberPublicNetwork = this.memberPublicNetworkService.queryEntity(publicNetworkId);
      log.info("selectById:{}", JSONObject.toJSONString(memberPublicNetwork));
      return memberPublicNetwork != null
         ? (BaseMemberPublicNetwork)BeanUtil.copyProperties(memberPublicNetwork, BaseMemberPublicNetwork.class, new String[0])
         : null;
   }

   public void rollbackStatus(String id) {
      this.memberPublicNetworkService.rollbackStatus(id);
   }
}
