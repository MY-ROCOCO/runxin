package com.rxinyun.admin.dev.modular.api.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.api.entity.ResourceChangeLog;
import com.rxinyun.admin.dev.modular.api.mapper.ResourceChangeLogMapper;
import com.rxinyun.admin.dev.modular.api.service.ResourceChangeLogService;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.TimeUtil;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourceChangeLogServiceImpl extends ServiceImpl<ResourceChangeLogMapper, ResourceChangeLog> implements ResourceChangeLogService {
   private static final Logger log = LoggerFactory.getLogger(ResourceChangeLogServiceImpl.class);

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void logChange(String orderNo, Long memberNo, String changeType, String upstreamServerId, String poolSyncTarget) {
      try {
         ResourceChangeLog changeLog = new ResourceChangeLog();
         changeLog.setId(IdUtil.fastSimpleUUID());
         changeLog.setOrderNo(orderNo);
         changeLog.setMemberNo(memberNo);
         changeLog.setChangeType(changeType);
         changeLog.setProcessed(0);
         changeLog.setCreateTime(new Date());
         changeLog.setUpstreamServerId(StrUtil.trimToNull(upstreamServerId));
         changeLog.setPoolSyncTarget(normalizePoolSyncTarget(poolSyncTarget));
         this.save(changeLog);
         log.info(
            "记录资源变更日志成功，订单号：{}，会员编号：{}，变更类型：{}，upstreamServerId：{}，poolSyncTarget：{}",
            new Object[]{orderNo, memberNo, changeType, changeLog.getUpstreamServerId(), changeLog.getPoolSyncTarget()}
         );
      } catch (Exception var7) {
         log.error("记录资源变更日志失败，订单号：{}，错误：{}", new Object[]{orderNo, var7.getMessage(), var7});
      }
   }

   private static String normalizePoolSyncTarget(String target) {
      String normalized = StrUtil.trimToNull(target);
      if (normalized == null) {
         return null;
      } else if (StrUtil.equalsIgnoreCase(normalized, "DEDICATED")) {
         return "INDEPENDENT";
      } else {
         return StrUtil.equalsIgnoreCase(normalized, "ECS") ? "CLOUD" : normalized.toUpperCase();
      }
   }

   @Override
   public List<ResourceChangeLog> queryPendingLogs(Long memberNo, LocalDateTime lastSyncTime, Integer limit) {
      LambdaQueryWrapper<ResourceChangeLog> wrapper = new LambdaQueryWrapper();
      ((LambdaQueryWrapper<ResourceChangeLog>)((LambdaQueryWrapper<ResourceChangeLog>)((LambdaQueryWrapper<ResourceChangeLog>)wrapper.eq(ResourceChangeLog::getMemberNo, memberNo))
               .eq(ResourceChangeLog::getProcessed, 0))
            .orderByAsc(CommonEntity::getCreateTime))
         .last("LIMIT " + limit);
      return this.list(wrapper);
   }

   @Override
   public List<ResourceChangeLog> queryPendingLogsForPool(LocalDateTime lastSyncTime, Integer limit) {
      LambdaQueryWrapper<ResourceChangeLog> wrapper = new LambdaQueryWrapper();
      ((LambdaQueryWrapper<ResourceChangeLog>)((LambdaQueryWrapper<ResourceChangeLog>)wrapper.eq(ResourceChangeLog::getProcessed, 0)).orderByAsc(CommonEntity::getCreateTime)).last("LIMIT " + limit);
      return this.list(wrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void markAsProcessed(String logId) {
      ResourceChangeLog changeLog = (ResourceChangeLog)this.getById(logId);
      if (changeLog != null && changeLog.getProcessed() == 0) {
         changeLog.setProcessed(1);
         changeLog.setProcessTime(TimeUtil.now());
         this.updateById(changeLog);
         log.debug("标记变更日志为已处理，ID: {}", logId);
      }
   }
}
