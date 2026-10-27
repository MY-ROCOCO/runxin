package com.rxinyun.member.business.modular.independent.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.business.api.MemberIndependentApi;
import com.rxinyun.business.core.pojo.BaseMemberIndependentAddServer;
import com.rxinyun.business.core.pojo.BaseMemberIndependentServer;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependent;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependentIp;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentAddParam;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentIpService;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberIndependentApiProvider implements MemberIndependentApi {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentApiProvider.class);
   @Resource
   private MemberIndependentService memberIndependentService;
   @Resource
   private MemberIndependentIpService memberIndependentIpService;

   public BaseMemberIndependentServer selectByServerId(String serverId) {
      if (StrUtil.isBlank(serverId)) {
         return null;
      } else {
         MemberIndependent memberIndependent = (MemberIndependent)this.memberIndependentService.getById(serverId);
         if (memberIndependent == null) {
            memberIndependent = (MemberIndependent)this.memberIndependentService
               .getOne((Wrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getUpstreamServerId, serverId));
         }

         return memberIndependent == null ? null : (BaseMemberIndependentServer)BeanUtil.toBean(memberIndependent, BaseMemberIndependentServer.class);
      }
   }

   public BaseMemberIndependentServer selectByServerIdAndMemberNo(String serverId, Long memberNo) {
      if (!StrUtil.isBlank(serverId) && memberNo != null) {
         MemberIndependent memberIndependent = (MemberIndependent)this.memberIndependentService
            .getOne((Wrapper<MemberIndependent>)((LambdaQueryWrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getId, serverId)).eq(MemberIndependent::getMemberNo, memberNo));
         return memberIndependent == null ? null : (BaseMemberIndependentServer)BeanUtil.toBean(memberIndependent, BaseMemberIndependentServer.class);
      } else {
         return null;
      }
   }

   public String selectMainIp(String serverId, Long memberNo) {
      BaseMemberIndependentServer server = this.selectByServerIdAndMemberNo(serverId, memberNo);
      if (server == null || StrUtil.isBlank(server.getId())) {
         return null;
      } else if (StrUtil.isNotBlank(server.getServerIp())) {
         return server.getServerIp();
      } else {
         List<MemberIndependentIp> serverIps = this.memberIndependentIpService.queryByServerId(server.getId());
         if (serverIps != null && !serverIps.isEmpty()) {
            String publicMain = null;
            String otherMain = null;

            for (MemberIndependentIp serverIp : serverIps) {
               if (serverIp != null && !StrUtil.isBlank(serverIp.getIp()) && Integer.valueOf(1).equals(serverIp.getIsMain())) {
                  if ("PUBLIC".equals(serverIp.getType())) {
                     publicMain = serverIp.getIp();
                  } else if (otherMain == null) {
                     otherMain = serverIp.getIp();
                  }
               }
            }

            return publicMain != null ? publicMain : otherMain;
         } else {
            return null;
         }
      }
   }

   public Boolean createServer(BaseMemberIndependentAddServer baseMemberIndependentAddServer) {
      try {
         MemberIndependentAddParam memberIndependentAddParam = (MemberIndependentAddParam)BeanUtil.toBean(
            baseMemberIndependentAddServer, MemberIndependentAddParam.class
         );
         this.memberIndependentService.add(memberIndependentAddParam);
         baseMemberIndependentAddServer.setId(memberIndependentAddParam.getId());
      } catch (Exception var3) {
         log.error("创建服务器失败", var3);
         return false;
      }

      return true;
   }

   public Boolean savePoolPendingServer(BaseMemberIndependentAddServer baseMemberIndependentAddServer) {
      try {
         MemberIndependentAddParam memberIndependentAddParam = (MemberIndependentAddParam)BeanUtil.toBean(
            baseMemberIndependentAddServer, MemberIndependentAddParam.class
         );
         this.memberIndependentService.savePoolPendingServer(memberIndependentAddParam);
      } catch (Exception var3) {
         log.error("资源池独立服务器占位保存失败", var3);
         return false;
      }

      return true;
   }

   public Boolean upgradeIndependentBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      try {
         CommonResult<?> result = this.memberIndependentService.upgradeBandwidthServer(orderNo, serverId, bandwidth, renewPrice);
         return this.isSuccess(result, "升级带宽", orderNo, serverId);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }
   }

   public Boolean upgradeIndependentIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice) {
      try {
         CommonResult<?> result = this.memberIndependentService.upgradeIpServer(orderNo, serverId, ipSize, renewPrice);
         return this.isSuccess(result, "升级IP", orderNo, serverId);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }
   }

   public Boolean renewServer(String orderNo, String serverId, Integer months, String unit) {
      try {
         CommonResult<?> result = this.memberIndependentService.renewServer(orderNo, serverId, months, unit);
         return this.isSuccess(result, "续费", orderNo, serverId);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }
   }

   public Boolean upgradeIndependentDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      try {
         CommonResult<?> result = this.memberIndependentService.upgradeDefenseServer(orderNo, serverId, defense, renewPrice);
         return this.isSuccess(result, "升级防御", orderNo, serverId);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }
   }

   private boolean isSuccess(CommonResult<?> result, String action, String orderNo, String serverId) {
      if (result != null && 200 == result.getCode()) {
         return true;
      } else {
         String msg = result == null ? "返回结果为空" : result.getMsg();
         log.warn("会员独立服务器{}失败，orderNo={}，serverId={}，msg={}", new Object[]{action, orderNo, serverId, msg});
         return false;
      }
   }
}
