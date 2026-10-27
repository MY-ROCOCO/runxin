package com.rxinyun.admin.business.modular.esc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.business.modular.esc.entity.Server;
import com.rxinyun.admin.business.modular.esc.service.HostStatsRefreshService;
import com.rxinyun.admin.business.modular.esc.service.ServerService;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HostStatsRefreshServiceImpl implements HostStatsRefreshService {
   private static final Logger log = LoggerFactory.getLogger(HostStatsRefreshServiceImpl.class);
   @Resource
   private ServerService serverService;
   @Resource
   private MemberMasterApi memberMasterApi;

   @Override
   public void refreshByMasterId(String masterId) {
      if (masterId != null) {
         try {
            List<Server> list = this.serverService
               .list(
                  (Wrapper<Server>)((LambdaQueryWrapper<Server>)new LambdaQueryWrapper<Server>().eq(Server::getMasterId, masterId))
                     .in(Server::getBusinessStatus, Arrays.asList(BusinessStatusEnum.NORMAL.getCode(), BusinessStatusEnum.EXPIRED.getCode()))
               );
            int instanceCount = list.size();
            int ramUsed = list.stream().mapToInt(s -> s.getRam() != null ? s.getRam() : 0).sum();
            long diskUsed = list.stream().mapToLong(s -> {
               int sys = s.getSystemDisk() != null ? s.getSystemDisk() : 0;
               int data = s.getDataDisks() != null ? s.getDataDisks().stream().mapToInt(x -> x != null ? x : 0).sum() : 0;
               return sys + data;
            }).sum();
            BaseMemberMaster master = this.memberMasterApi.selectByMasterId(masterId);
            if (master == null) {
               return;
            }

            int ramLimit = master.getRamLimit() != null ? master.getRamLimit() : 0;
            long diskLimit = master.getHarddiskLimit() != null ? master.getHarddiskLimit().longValue() : 0L;
            int ramRemaining = Math.max(0, ramLimit - ramUsed);
            long diskRemaining = Math.max(0L, diskLimit - diskUsed);
            this.memberMasterApi.updateHostStats(masterId, instanceCount, ramRemaining, diskRemaining);
         } catch (Exception var14) {
            log.warn("刷新宿主机统计失败 masterId={}", masterId, var14);
         }
      }
   }
}
