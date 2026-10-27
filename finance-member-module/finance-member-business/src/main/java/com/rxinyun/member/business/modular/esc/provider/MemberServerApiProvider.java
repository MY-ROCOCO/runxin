package com.rxinyun.member.business.modular.esc.provider;

import cn.dev33.satoken.exception.NotWebContextException;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.rxinyun.business.api.MemberServerApi;
import com.rxinyun.business.core.pojo.BaseMemberAddServer;
import com.rxinyun.business.core.pojo.BaseMemberServer;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.esc.entity.MemberServer;
import com.rxinyun.member.business.modular.esc.entity.MemberServerIp;
import com.rxinyun.member.business.modular.esc.param.MemberServerAddParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerIpService;
import com.rxinyun.member.business.modular.esc.service.MemberServerService;
import com.rxinyun.member.core.util.StpClientUtil;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberServerApiProvider implements MemberServerApi {
   private static final Logger log = LoggerFactory.getLogger(MemberServerApiProvider.class);
   @Resource
   private MemberServerService memberServerService;
   @Resource
   private MemberServerIpService memberServerIpService;

   public BaseMemberServer selectByServerId(String serverId) {
      MemberServer memberServer;
      try {
         if (StpClientUtil.isLogin()) {
            memberServer = this.memberServerService.queryEntity(serverId);
         } else {
            memberServer = (MemberServer)this.memberServerService.getById(serverId);
         }
      } catch (NotWebContextException var4) {
         memberServer = (MemberServer)this.memberServerService.getById(serverId);
      }

      return memberServer == null ? null : (BaseMemberServer)BeanUtil.toBean(memberServer, BaseMemberServer.class);
   }

   public BaseMemberServer selectByServerIdAndMemberNo(String serverId, Long memberNo) {
      if (serverId != null && memberNo != null) {
         MemberServer memberServer = this.memberServerService.getByIdAndMemberNo(serverId, String.valueOf(memberNo));
         return memberServer == null ? null : (BaseMemberServer)BeanUtil.toBean(memberServer, BaseMemberServer.class);
      } else {
         return null;
      }
   }

   public String selectMainIp(String serverId, Long memberNo) {
      if (!StrUtil.isBlank(serverId) && memberNo != null) {
         MemberServer memberServer = this.memberServerService.getByIdAndMemberNo(serverId, String.valueOf(memberNo));
         if (memberServer == null) {
            return null;
         } else {
            return StrUtil.isNotBlank(memberServer.getPublicIp())
               ? memberServer.getPublicIp()
               : this.pickMainIp(this.memberServerIpService.queryByServerId(memberServer.getId()));
         }
      } else {
         return null;
      }
   }

   private String pickMainIp(List<MemberServerIp> serverIps) {
      if (serverIps != null && !serverIps.isEmpty()) {
         String publicMain = null;
         String natMain = null;
         String otherMain = null;

         for (MemberServerIp serverIp : serverIps) {
            if (serverIp != null && !StrUtil.isBlank(serverIp.getIp()) && Integer.valueOf(1).equals(serverIp.getIsMain())) {
               if ("PUBLIC".equals(serverIp.getType())) {
                  publicMain = serverIp.getIp();
               } else if ("NAT".equals(serverIp.getType())) {
                  natMain = serverIp.getIp();
               } else if (otherMain == null) {
                  otherMain = serverIp.getIp();
               }
            }
         }

         if (publicMain != null) {
            return publicMain;
         } else {
            return natMain != null ? natMain : otherMain;
         }
      } else {
         return null;
      }
   }

   public Boolean createServer(BaseMemberAddServer baseMemberAddServer) {
      try {
         MemberServerAddParam memberServerAddParam = (MemberServerAddParam)BeanUtil.toBean(baseMemberAddServer, MemberServerAddParam.class);
         this.memberServerService.add(memberServerAddParam);
         baseMemberAddServer.setId(memberServerAddParam.getId());
      } catch (Exception var3) {
         log.error("创建服务器失败", var3);
         return false;
      }

      return true;
   }

   public Boolean upgradeHardDisksServer(String orderNo, String serverId, Integer systemDisk, List<Integer> dataDisks, BigDecimal renewPrice) {
      try {
         this.memberServerService.upgradeHardDisksServer(orderNo, serverId, systemDisk, dataDisks, renewPrice);
      } catch (Exception var7) {
         log.error("升级服务器失败", var7);
         return false;
      }

      return true;
   }

   public Boolean upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      try {
         this.memberServerService.upgradeBandwidthServer(orderNo, serverId, bandwidth, renewPrice);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }

      return true;
   }

   public Boolean upgradeCpuRamServer(String orderNo, String serverId, Integer cpu, Integer ram, BigDecimal renewPrice) {
      try {
         this.memberServerService.upgradeCpuRamServer(orderNo, serverId, cpu, ram, renewPrice);
      } catch (Exception var7) {
         log.error("升级服务器失败", var7);
         return false;
      }

      return true;
   }

   public Boolean renewServer(String orderNo, String serverId, Integer months) {
      try {
         CommonResult<?> result = this.memberServerService.renewServer(orderNo, serverId, months);
         if (result != null && 200 == result.getCode()) {
            return true;
         } else {
            log.warn("会员云服务器续费失败，orderNo={}，serverId={}，msg={}", new Object[]{orderNo, serverId, result == null ? "返回结果为空" : result.getMsg()});
            return false;
         }
      } catch (Exception var5) {
         log.error("云服务器续费失败", var5);
         return false;
      }
   }

   public Boolean upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      try {
         this.memberServerService.upgradeDefenseServer(orderNo, serverId, defense, renewPrice);
      } catch (Exception var6) {
         log.error("升级服务器失败", var6);
         return false;
      }

      return true;
   }
}
