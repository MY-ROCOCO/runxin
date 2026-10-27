package com.rxinyun.admin.business.modular.vhost.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.vhost.entity.AdminVhost;
import com.rxinyun.admin.business.modular.vhost.mapper.AdminVhostMapper;
import com.rxinyun.admin.business.modular.vhost.task.entity.VhostAutoTask;
import com.rxinyun.admin.business.modular.vhost.task.mapper.VhostAutoTaskMapper;
import com.rxinyun.admin.business.modular.vhost.task.param.VhostAutoTaskIdParam;
import com.rxinyun.admin.business.modular.vhost.task.param.VhostAutoTaskPageParam;
import com.rxinyun.admin.business.modular.vhost.task.service.VhostAutoTaskService;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VhostAutoTaskServiceImpl extends ServiceImpl<VhostAutoTaskMapper, VhostAutoTask> implements VhostAutoTaskService {
   @Resource
   private AdminVhostMapper adminVhostMapper;

   @Override
   public Page<VhostAutoTask> page(VhostAutoTaskPageParam pageParam) {
      QueryWrapper<VhostAutoTask> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (pageParam != null && StrUtil.isNotBlank(pageParam.getMasterId())) {
         queryWrapper.lambda().eq(VhostAutoTask::getMasterId, pageParam.getMasterId());
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getHostId())) {
         queryWrapper.lambda().eq(VhostAutoTask::getHostId, pageParam.getHostId());
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getHostName())) {
         List<String> hostIds = this.listHostIdsByName(pageParam.getHostName());
         if (CollUtil.isEmpty(hostIds)) {
            queryWrapper.lambda().eq(VhostAutoTask::getId, "-1");
         } else {
            queryWrapper.lambda().in(VhostAutoTask::getHostId, hostIds);
         }
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getTaskType())) {
         queryWrapper.lambda().eq(VhostAutoTask::getTaskType, pageParam.getTaskType());
      }

      if (pageParam != null && pageParam.getTaskStatus() != null) {
         queryWrapper.lambda().eq(VhostAutoTask::getTaskStatus, pageParam.getTaskStatus());
      }

      if (pageParam != null && CollUtil.isNotEmpty(pageParam.getTaskStatusList())) {
         queryWrapper.lambda().in(VhostAutoTask::getTaskStatus, pageParam.getTaskStatusList());
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getPlatform())) {
         queryWrapper.lambda().eq(VhostAutoTask::getPlatform, pageParam.getPlatform());
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getSearchKey())) {
         List<String> hostIds = this.listHostIdsByName(pageParam.getSearchKey());
         queryWrapper.lambda().and(q -> {
            q.like(VhostAutoTask::getTaskDesc, pageParam.getSearchKey());
            if (CollUtil.isNotEmpty(hostIds)) {
               ((LambdaQueryWrapper<VhostAutoTask>)q.or()).in(VhostAutoTask::getHostId, hostIds);
            }
         });
      }

      queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      Page<VhostAutoTask> page = (Page<VhostAutoTask>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillNames(page.getRecords());
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult<String> retry(VhostAutoTaskIdParam idParam) {
      VhostAutoTask task = (VhostAutoTask)this.getById(idParam.getId());
      if (task == null) {
         return CommonResult.error("任务不存在");
      } else if (!Integer.valueOf(-1).equals(task.getTaskStatus())) {
         return CommonResult.error("仅失败状态的任务允许重试");
      } else {
         if (StrUtil.isNotBlank(task.getHostId())) {
            long pending = this.count(
               (Wrapper<VhostAutoTask>)((LambdaQueryWrapper<VhostAutoTask>)((LambdaQueryWrapper<VhostAutoTask>)new LambdaQueryWrapper<VhostAutoTask>().eq(VhostAutoTask::getHostId, task.getHostId()))
                     .in(VhostAutoTask::getTaskStatus, new Object[]{1, 2}))
                  .ne(VhostAutoTask::getId, task.getId())
            );
            if (pending > 0L) {
               return CommonResult.error("虚拟主机存在未处理的任务，请等待......");
            }
         }

         this.update(
            (Wrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)new LambdaUpdateWrapper<VhostAutoTask>()
                                 .eq(VhostAutoTask::getId, task.getId()))
                              .set(VhostAutoTask::getTaskStatus, 1))
                           .set(VhostAutoTask::getRetryCount, 0))
                        .set(VhostAutoTask::getNextRetryTime, new Date()))
                     .set(VhostAutoTask::getStartTime, null))
                  .set(VhostAutoTask::getEndTime, null))
               .set(VhostAutoTask::getExecuteResult, "手动重试中")
         );
         return CommonResult.ok("手动重试任务成功");
      }
   }

   @Override
   public void recoverStuckTasks() {
      Date threshold = new Date(System.currentTimeMillis() - 1800000L);

      for (VhostAutoTask task : this.list(
         (Wrapper<VhostAutoTask>)((LambdaQueryWrapper<VhostAutoTask>)((LambdaQueryWrapper<VhostAutoTask>)new LambdaQueryWrapper<VhostAutoTask>().eq(VhostAutoTask::getTaskStatus, 2))
               .isNotNull(VhostAutoTask::getStartTime))
            .lt(VhostAutoTask::getStartTime, threshold)
      )) {
         this.update(
            (Wrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)new LambdaUpdateWrapper<VhostAutoTask>().eq(VhostAutoTask::getId, task.getId()))
                     .set(VhostAutoTask::getTaskStatus, -1))
                  .set(VhostAutoTask::getEndTime, new Date()))
               .set(VhostAutoTask::getExecuteResult, "任务执行超时（超过30分钟未完成）")
         );
      }
   }

   @Override
   public List<VhostAutoTask> listPending(int limit) {
      int size = limit < 1 ? 1 : Math.min(limit, 50);
      return this.list(
         (Wrapper<VhostAutoTask>)((LambdaQueryWrapper<VhostAutoTask>)((LambdaQueryWrapper<VhostAutoTask>)((LambdaQueryWrapper<VhostAutoTask>)((LambdaQueryWrapper<VhostAutoTask>)new LambdaQueryWrapper<VhostAutoTask>()
                        .in(VhostAutoTask::getTaskType, new Object[]{"CREATE", "RENEW", "START", "STOP", "DELETE"}))
                     .eq(VhostAutoTask::getTaskStatus, 1))
                  .and(
                     q -> ((LambdaQueryWrapper<VhostAutoTask>)((LambdaQueryWrapper<VhostAutoTask>)q.isNull(VhostAutoTask::getNextRetryTime)).or())
                        .le(VhostAutoTask::getNextRetryTime, new Date())
                  ))
               .orderByAsc(CommonEntity::getCreateTime))
            .last("limit " + size)
      );
   }

   @Override
   public boolean claim(String id) {
      return this.update(
         (Wrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)new LambdaUpdateWrapper<VhostAutoTask>().eq(VhostAutoTask::getId, id))
                  .eq(VhostAutoTask::getTaskStatus, 1))
               .set(VhostAutoTask::getTaskStatus, 2))
            .set(VhostAutoTask::getStartTime, new Date())
      );
   }

   @Override
   public void bindMaster(String id, String masterId) {
      if (!StrUtil.isBlank(id) && !StrUtil.isBlank(masterId)) {
         this.update((Wrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)new LambdaUpdateWrapper<VhostAutoTask>().eq(VhostAutoTask::getId, id)).set(VhostAutoTask::getMasterId, masterId));
      }
   }

   @Override
   public void markSuccess(String id, String masterId, String executeResult) {
      this.update(
         (Wrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)new LambdaUpdateWrapper<VhostAutoTask>()
                        .eq(VhostAutoTask::getId, id))
                     .set(VhostAutoTask::getTaskStatus, 3))
                  .set(StrUtil.isNotBlank(masterId), VhostAutoTask::getMasterId, masterId))
               .set(VhostAutoTask::getEndTime, new Date()))
            .set(VhostAutoTask::getExecuteResult, this.abbreviate(executeResult))
      );
   }

   @Override
   public void markFailure(VhostAutoTask task, String message, boolean retryable) {
      int retryCount = task.getRetryCount() == null ? 0 : task.getRetryCount();
      int maxRetry = task.getMaxRetry() == null ? 3 : task.getMaxRetry();
      LambdaUpdateWrapper<VhostAutoTask> update = (LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)new LambdaUpdateWrapper<VhostAutoTask>()
               .eq(VhostAutoTask::getId, task.getId()))
            .set(VhostAutoTask::getEndTime, new Date()))
         .set(VhostAutoTask::getExecuteResult, this.abbreviate(message));
      if (retryable && retryCount < maxRetry) {
         ((LambdaUpdateWrapper<VhostAutoTask>)((LambdaUpdateWrapper<VhostAutoTask>)update.set(VhostAutoTask::getTaskStatus, 1)).set(VhostAutoTask::getRetryCount, retryCount + 1))
            .set(VhostAutoTask::getNextRetryTime, new Date(System.currentTimeMillis() + 60000L));
      } else {
         update.set(VhostAutoTask::getTaskStatus, -1);
      }

      this.update(update);
   }

   private String abbreviate(String message) {
      String text = StrUtil.blankToDefault(message, "执行失败");
      return text.length() > 900 ? text.substring(0, 900) : text;
   }

   private List<String> listHostIdsByName(String hostName) {
      return this.adminVhostMapper
         .selectList(((LambdaQueryWrapper<AdminVhost>)new LambdaQueryWrapper<AdminVhost>().like(AdminVhost::getHostName, hostName)).select(AdminVhost::getId))
         .stream()
         .map(AdminVhost::getId)
         .filter(CharSequenceUtil::isNotBlank)
         .collect(Collectors.toList());
   }

   private void fillNames(List<VhostAutoTask> tasks) {
      if (!CollUtil.isEmpty(tasks)) {
         List<String> hostIds = tasks.stream().map(VhostAutoTask::getHostId).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
         List<String> masterIds = tasks.stream().map(VhostAutoTask::getMasterId).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
         Map<String, String> hostNames = new HashMap<>();
         if (CollUtil.isNotEmpty(hostIds)) {
            this.adminVhostMapper
               .selectList(
                  ((LambdaQueryWrapper<AdminVhost>)new LambdaQueryWrapper<AdminVhost>().in(AdminVhost::getId, hostIds))
                     .select(AdminVhost::getId, AdminVhost::getHostName)
               )
               .forEach(host -> hostNames.put(host.getId(), host.getHostName()));
         }

         Map<String, String> masterNames = this.loadMasterNames(masterIds);

         for (VhostAutoTask task : tasks) {
            task.setHostName(hostNames.get(task.getHostId()));
            task.setMasterName(masterNames.get(task.getMasterId()));
         }
      }
   }

   private Map<String, String> loadMasterNames(Collection<String> masterIds) {
      Map<String, String> names = new HashMap<>();
      if (CollUtil.isEmpty(masterIds)) {
         return names;
      } else {
         List<Map<String, Object>> rows = ((VhostAutoTaskMapper)this.baseMapper).selectMasterNames(masterIds);
         if (rows == null) {
            return names;
         } else {
            for (Map<String, Object> row : rows) {
               Object id = row == null ? null : this.firstValue(row, "id", "ID");
               if (id != null) {
                  Object name = this.firstValue(row, "name", "NAME");
                  names.put(String.valueOf(id), name == null ? null : String.valueOf(name));
               }
            }

            return names;
         }
      }
   }

   private Object firstValue(Map<String, Object> row, String... keys) {
      for (String key : keys) {
         if (row.containsKey(key)) {
            return row.get(key);
         }
      }

      return null;
   }
}
