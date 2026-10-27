package com.rxinyun.admin.production.modular.dns.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.production.modular.dns.entity.DnsNode;
import com.rxinyun.admin.production.modular.dns.entity.DnsNodeTask;
import com.rxinyun.admin.production.modular.dns.mapper.DnsNodeMapper;
import com.rxinyun.admin.production.modular.dns.mapper.DnsNodeTaskMapper;
import com.rxinyun.admin.production.modular.dns.util.DnsTokenUtil;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DnsNodeTaskDispatchService implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(DnsNodeTaskDispatchService.class);
   private static final int NODE_ENABLED = 1;
   private static final int TASK_PENDING = 1;
   private static final int TASK_RUNNING = 2;
   private static final int TASK_FAILED = -1;
   private static final int BATCH_SIZE = 100;
   private static final long RETRY_INTERVAL_MILLIS = 60000L;
   private final AtomicBoolean running = new AtomicBoolean(false);
   @Resource
   private DnsNodeMapper nodeMapper;
   @Resource
   private DnsNodeTaskMapper nodeTaskMapper;

   public void action() {
      if (this.running.compareAndSet(false, true)) {
         try {
            Date now = new Date();
            List<DnsNodeTask> tasks = this.nodeTaskMapper
               .selectList(
                  (Wrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)new LambdaQueryWrapper<DnsNodeTask>().eq(DnsNodeTask::getTaskStatus, 1))
                           .and(
                              q -> ((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)q.isNull(DnsNodeTask::getNextRetryTime)).or())
                                 .le(DnsNodeTask::getNextRetryTime, now)
                           ))
                        .orderByAsc(CommonEntity::getCreateTime))
                     .last("LIMIT 100")
               );
            tasks.forEach(this::dispatchTask);
         } finally {
            this.running.set(false);
         }
      }
   }

   private void dispatchTask(DnsNodeTask task) {
      DnsNode node = (DnsNode)this.nodeMapper.selectById(task.getNodeId());
      if (node == null) {
         this.markTaskFailed(task, "DNS节点不存在：" + task.getNodeId());
      } else if (!Integer.valueOf(1).equals(node.getStatus())) {
         this.markTaskRetryOrFailed(task, "DNS节点未启用：" + node.getId());
      } else if (StrUtil.hasBlank(new CharSequence[]{node.getApiUrl(), node.getApiToken()})) {
         this.markTaskRetryOrFailed(task, "DNS节点API地址或Token未配置：" + node.getId());
      } else {
         try {
            HttpResponse response = ((HttpRequest)((HttpRequest)HttpRequest.post(this.buildNodeUrl(node.getApiUrl(), task.getRequestPath()))
                     .header("Authorization", "Bearer " + DnsTokenUtil.decryptApiToken(node.getApiToken())))
                  .header("Content-Type", "application/json"))
               .body(task.getRequestBody())
               .timeout(10000)
               .execute();

            label72: {
               try {
                  task.setResponseBody(response.body());
                  if (response.getStatus() != 202 && response.getStatus() != 200) {
                     this.markTaskRetryOrFailed(task, "节点命令入队失败，HTTP状态码：" + response.getStatus());
                     break label72;
                  }

                  task.setTaskStatus(2);
                  task.setStartTime(new Date());
                  task.setNextRetryTime(null);
                  task.setErrorMessage(null);
                  this.nodeTaskMapper.updateById(task);
               } catch (Throwable var7) {
                  if (response != null) {
                     try {
                        response.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (response != null) {
                  response.close();
               }

               return;
            }

            if (response != null) {
               response.close();
            }
         } catch (Exception var8) {
            this.markTaskRetryOrFailed(task, var8.getMessage());
         }
      }
   }

   private void markTaskRetryOrFailed(DnsNodeTask task, String errorMessage) {
      int retryCount = (Integer)ObjectUtil.defaultIfNull(task.getRetryCount(), 0) + 1;
      int maxRetry = (Integer)ObjectUtil.defaultIfNull(task.getMaxRetry(), 3);
      task.setRetryCount(retryCount);
      task.setErrorMessage(StrUtil.maxLength(errorMessage, 1000));
      if (retryCount >= maxRetry) {
         task.setTaskStatus(-1);
         task.setEndTime(new Date());
         task.setNextRetryTime(null);
      } else {
         task.setTaskStatus(1);
         task.setNextRetryTime(new Date(System.currentTimeMillis() + 60000L * retryCount));
      }

      this.nodeTaskMapper.updateById(task);
      log.warn("DNS节点任务下发失败，taskId={}, nodeId={}, retry={}/{}, error={}", new Object[]{task.getId(), task.getNodeId(), retryCount, maxRetry, errorMessage});
   }

   private void markTaskFailed(DnsNodeTask task, String errorMessage) {
      task.setTaskStatus(-1);
      task.setEndTime(new Date());
      task.setErrorMessage(StrUtil.maxLength(errorMessage, 1000));
      this.nodeTaskMapper.updateById(task);
   }

   private String buildNodeUrl(String apiUrl, String path) {
      return StrUtil.removeSuffix(StrUtil.trim(apiUrl), "/") + StrUtil.addPrefixIfNot(path, "/");
   }
}
