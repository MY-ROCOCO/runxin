package com.rxinyun.member.business.modular.vhost.provider;

import com.rxinyun.business.api.MemberVhostApi;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.business.core.pojo.BaseMemberVhostCreate;
import com.rxinyun.business.core.pojo.BaseMemberVhostProvision;
import com.rxinyun.member.business.modular.vhost.service.MemberVhostService;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberVhostApiProvider implements MemberVhostApi {
   private static final Logger log = LoggerFactory.getLogger(MemberVhostApiProvider.class);
   @Resource
   private MemberVhostService memberVhostService;

   public Boolean create(BaseMemberVhostCreate param) {
      try {
         this.memberVhostService.create(param);
         return true;
      } catch (Exception var3) {
         log.error("创建虚拟主机实例失败，订单号:{}", param == null ? null : param.getOrderNo(), var3);
         return false;
      }
   }

   public BaseMemberVhost selectById(String hostId) {
      return this.memberVhostService.selectById(hostId);
   }

   public BaseMemberVhost selectByIdAndMemberNo(String hostId, String memberNo) {
      return this.memberVhostService.selectByIdAndMemberNo(hostId, memberNo);
   }

   public List<BaseMemberVhost> listByOrderNo(String orderNo) {
      return this.memberVhostService.listByOrderNo(orderNo);
   }

   public Boolean renew(String hostId, String memberNo, Integer months, String orderNo, String platform) {
      try {
         this.memberVhostService.renew(hostId, memberNo, months, orderNo, platform);
         return true;
      } catch (Exception var7) {
         log.error("虚拟主机续费失败，订单号:{}", orderNo, var7);
         return false;
      }
   }

   public Boolean upgradeTraffic(String hostId, String memberNo, Integer upgradeTraffic, String orderNo) {
      try {
         this.memberVhostService.upgradeTraffic(hostId, memberNo, upgradeTraffic, orderNo);
         return true;
      } catch (Exception var6) {
         log.error("虚拟主机升级流量失败，订单号:{}", orderNo, var6);
         return false;
      }
   }

   public List<BaseMemberVhost> listExpiredToStop() {
      return this.memberVhostService.listExpiredToStop();
   }

   public void expireStop(String hostId) {
      this.memberVhostService.expireStop(hostId);
   }

   public List<BaseMemberVhost> listExpiredToDelete() {
      return this.memberVhostService.listExpiredToDelete();
   }

   public void expireDelete(String hostId) {
      this.memberVhostService.expireDelete(hostId);
   }

   public void refundDelete(String hostId) {
      this.memberVhostService.refundDelete(hostId);
   }

   public Boolean provision(BaseMemberVhostProvision param) {
      try {
         this.memberVhostService.provision(param);
         return true;
      } catch (Exception var3) {
         log.error("虚拟主机开通回写失败，实例:{}", param == null ? null : param.getHostId(), var3);
         return false;
      }
   }
}
