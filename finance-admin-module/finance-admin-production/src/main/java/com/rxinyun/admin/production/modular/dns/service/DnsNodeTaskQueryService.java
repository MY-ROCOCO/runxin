package com.rxinyun.admin.production.modular.dns.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.production.modular.dns.entity.DnsNode;
import com.rxinyun.admin.production.modular.dns.entity.DnsNodeTask;
import com.rxinyun.admin.production.modular.dns.mapper.DnsNodeMapper;
import com.rxinyun.admin.production.modular.dns.mapper.DnsNodeTaskMapper;
import com.rxinyun.admin.production.modular.dns.util.DnsTokenUtil;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DnsNodeTaskQueryService implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(DnsNodeTaskQueryService.class);
   private static final int TASK_RUNNING = 2;
   private static final int TASK_SUCCEEDED = 3;
   private static final int TASK_FAILED = -1;
   private static final String TASK_QUERY_PATH_PREFIX = "/api/v1/tasks/";
   private static final int BATCH_SIZE = 100;
   private final AtomicBoolean running = new AtomicBoolean(false);
   @Resource
   private DnsNodeMapper nodeMapper;
   @Resource
   private DnsNodeTaskMapper nodeTaskMapper;

   public void action() {
      if (this.running.compareAndSet(false, true)) {
         try {
            List<DnsNodeTask> tasks = this.nodeTaskMapper
               .selectList(
                  (Wrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)new LambdaQueryWrapper<DnsNodeTask>().eq(DnsNodeTask::getTaskStatus, 2))
                           .isNotNull(DnsNodeTask::getNodeTaskId))
                        .orderByAsc(DnsNodeTask::getStartTime))
                     .last("LIMIT 100")
               );
            tasks.forEach(this::queryTaskResult);
         } finally {
            this.running.set(false);
         }
      }
   }

   private void queryTaskResult(DnsNodeTask task) {
      DnsNode node = (DnsNode)this.nodeMapper.selectById(task.getNodeId());
      if (node == null) {
         this.markTaskFailed(task, "DNS节点不存在：" + task.getNodeId());
      } else {
         try {
            HttpResponse response = ((HttpRequest)HttpRequest.get(this.buildTaskQueryUrl(node.getApiUrl(), task.getNodeTaskId()))
                  .header("Authorization", "Bearer " + DnsTokenUtil.decryptApiToken(node.getApiToken())))
               .timeout(10000)
               .execute();

            label57: {
               try {
                  task.setResponseBody(response.body());
                  if (response.getStatus() >= 200 && response.getStatus() < 300) {
                     this.applyTaskQueryResult(node, task, JSON.parseObject(response.body()));
                     break label57;
                  }

                  this.nodeTaskMapper.updateById(task);
                  log.warn("DNS节点任务查询失败，taskId={}, nodeId={}, status={}", new Object[]{task.getId(), node.getId(), response.getStatus()});
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
            log.warn("DNS节点任务查询异常，taskId={}, nodeId={}, error={}", new Object[]{task.getId(), task.getNodeId(), var8.getMessage()});
         }
      }
   }

   private void applyTaskQueryResult(DnsNode node, DnsNodeTask task, JSONObject body) {
      String status = body.getString("status");
      if ("QUEUED".equals(status) || "RUNNING".equals(status)) {
         this.nodeTaskMapper.updateById(task);
      } else if ("SUCCEEDED".equals(status)) {
         task.setTaskStatus(3);
         task.setEndTime(new Date());
         task.setErrorMessage(null);
         this.nodeTaskMapper.updateById(task);
         this.updateNodeVersionIfPresent(node, body);
      } else {
         if ("FAILED".equals(status)) {
            task.setTaskStatus(-1);
            task.setEndTime(new Date());
            task.setErrorMessage(this.resolveErrorMessage(body));
            this.nodeTaskMapper.updateById(task);
         }
      }
   }

   private void markTaskFailed(DnsNodeTask task, String errorMessage) {
      task.setTaskStatus(-1);
      task.setEndTime(new Date());
      task.setErrorMessage(StrUtil.maxLength(errorMessage, 1000));
      this.nodeTaskMapper.updateById(task);
   }

   private void updateNodeVersionIfPresent(DnsNode node, JSONObject body) {
      Integer version = body.getInteger("version");
      if (version == null && body.getJSONObject("result") != null) {
         version = body.getJSONObject("result").getInteger("version");
      }

      if (version != null) {
         node.setVersion(String.valueOf(version));
         this.nodeMapper.updateById(node);
      }
   }

   private String resolveErrorMessage(JSONObject body) {
      JSONObject error = body.getJSONObject("error");
      if (error != null) {
         String message = error.getString("message");
         if (StrUtil.isNotBlank(message)) {
            return StrUtil.maxLength(message, 1000);
         }

         String code = error.getString("code");
         if (StrUtil.isNotBlank(code)) {
            return StrUtil.maxLength(code, 1000);
         }
      }

      return "DNS节点任务执行失败";
   }

   private String buildTaskQueryUrl(String apiUrl, String nodeTaskId) {
      return StrUtil.removeSuffix(StrUtil.trim(apiUrl), "/") + "/api/v1/tasks/" + nodeTaskId;
   }
}
