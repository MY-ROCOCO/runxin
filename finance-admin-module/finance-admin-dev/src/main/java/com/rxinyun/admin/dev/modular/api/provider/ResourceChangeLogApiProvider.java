package com.rxinyun.admin.dev.modular.api.provider;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.dev.modular.api.entity.ResourceChangeLog;
import com.rxinyun.admin.dev.modular.api.service.ResourceChangeLogService;
import com.rxinyun.dev.api.ResourceChangeLogApi;
import com.rxinyun.dev.core.pojo.BaseResourceChangeLog;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component("resourceChangeLogApiProvider")
public class ResourceChangeLogApiProvider implements ResourceChangeLogApi {
   private static final Logger log = LoggerFactory.getLogger(ResourceChangeLogApiProvider.class);
   @Resource
   private ResourceChangeLogService resourceChangeLogService;

   public List<BaseResourceChangeLog> listPendingByOrderNoAndUpstreamServerId(String orderNo, String upstreamServerId) {
      List<ResourceChangeLog> entityList = this.resourceChangeLogService
         .list(
            (Wrapper<ResourceChangeLog>)((LambdaQueryWrapper<ResourceChangeLog>)((LambdaQueryWrapper<ResourceChangeLog>)new LambdaQueryWrapper<ResourceChangeLog>().eq(ResourceChangeLog::getOrderNo, orderNo))
                  .eq(ResourceChangeLog::getUpstreamServerId, upstreamServerId))
               .eq(ResourceChangeLog::getProcessed, 0)
         );
      List<BaseResourceChangeLog> baseList = new ArrayList<>();

      for (ResourceChangeLog entity : entityList) {
         BaseResourceChangeLog base = this.convertToBase(entity);
         baseList.add(base);
      }

      return baseList;
   }

   public boolean save(BaseResourceChangeLog changeLog) {
      try {
         ResourceChangeLog entity = this.convertToEntity(changeLog);
         this.resourceChangeLogService.save(entity);
         log.info(
            "✅ 已保存资源变更日志，订单号：{}，upstreamServerId：{}，poolSyncTarget：{}",
            new Object[]{changeLog.getOrderNo(), changeLog.getUpstreamServerId(), changeLog.getPoolSyncTarget()}
         );
         return true;
      } catch (Exception var3) {
         log.error("保存资源变更日志失败，订单号：{}，错误：{}", new Object[]{changeLog.getOrderNo(), var3.getMessage(), var3});
         return false;
      }
   }

   public boolean updateById(BaseResourceChangeLog changeLog) {
      try {
         ResourceChangeLog entity = this.convertToEntity(changeLog);
         this.resourceChangeLogService.updateById(entity);
         log.info(
            "✅ 已更新资源变更日志，订单号：{}，upstreamServerId：{}，poolSyncTarget：{}",
            new Object[]{changeLog.getOrderNo(), changeLog.getUpstreamServerId(), changeLog.getPoolSyncTarget()}
         );
         return true;
      } catch (Exception var3) {
         log.error("更新资源变更日志失败，订单号：{}，错误：{}", new Object[]{changeLog.getOrderNo(), var3.getMessage(), var3});
         return false;
      }
   }

   private BaseResourceChangeLog convertToBase(ResourceChangeLog entity) {
      if (entity == null) {
         return null;
      } else {
         BaseResourceChangeLog base = new BaseResourceChangeLog();
         BeanUtils.copyProperties(entity, base);
         return base;
      }
   }

   private ResourceChangeLog convertToEntity(BaseResourceChangeLog base) {
      if (base == null) {
         return null;
      } else {
         ResourceChangeLog entity = new ResourceChangeLog();
         BeanUtils.copyProperties(base, entity);
         return entity;
      }
   }
}
