package com.rxinyun.admin.production.modular.dns.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.production.modular.dns.entity.DnsNode;
import com.rxinyun.admin.production.modular.dns.mapper.DnsNodeMapper;
import com.rxinyun.admin.production.modular.dns.util.DnsTokenUtil;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DnsNodeHeartbeatService implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(DnsNodeHeartbeatService.class);
   private static final int ENABLED = 1;
   private static final int INSTALLED = 1;
   private static final int ONLINE = 1;
   private static final int OFFLINE = 0;
   private static final int OFFLINE_FAIL_THRESHOLD = 3;
   private static final String HEARTBEAT_PATH = "/api/v1/heartbeat";
   private final AtomicBoolean running = new AtomicBoolean(false);
   @Resource
   private DnsNodeMapper nodeMapper;

   public void action() {
      if (this.running.compareAndSet(false, true)) {
         try {
            for (DnsNode node : this.nodeMapper
               .selectList((Wrapper<DnsNode>)((LambdaQueryWrapper<DnsNode>)new LambdaQueryWrapper<DnsNode>().eq(DnsNode::getStatus, 1)).eq(DnsNode::getInstallStatus, 1))) {
               this.refreshNodeHeartbeat(node);
            }
         } finally {
            this.running.set(false);
         }
      }
   }

   private void refreshNodeHeartbeat(DnsNode node) {
      try {
         String token = DnsTokenUtil.decryptApiToken(node.getApiToken());
         HttpResponse response = ((HttpRequest)HttpRequest.get(this.buildHeartbeatUrl(node.getApiUrl())).header("Authorization", "Bearer " + token))
            .timeout(5000)
            .execute();
         if (response.getStatus() < 200 || response.getStatus() >= 300) {
            this.markHeartbeatFailed(node, "HTTP " + response.getStatus());
            return;
         }

         JSONObject body = JSON.parseObject(response.body());
         if (!"ONLINE".equals(body.getString("status"))) {
            this.markHeartbeatFailed(node, "节点状态：" + body.getString("status"));
            return;
         }

         this.applyHeartbeatSuccess(node, body);
      } catch (Exception var5) {
         this.markHeartbeatFailed(node, var5.getMessage());
      }
   }

   private void applyHeartbeatSuccess(DnsNode node, JSONObject body) {
      node.setHeartbeatStatus(1);
      node.setHeartbeatFailCount(0);
      node.setLastHeartbeatTime(this.parseTime(body.getString("timestamp")));
      node.setUptimeSeconds(body.getLong("uptimeSeconds"));
      node.setLastCommandId(body.getString("lastCommandId"));
      node.setZoneCount(body.getInteger("zoneCount"));
      node.setRecordCount(body.getInteger("recordCount"));
      Integer version = body.getInteger("version");
      if (version != null) {
         node.setVersion(String.valueOf(version));
      }

      JSONObject dns = body.getJSONObject("dns");
      if (dns != null) {
         node.setDnsQueryCount(dns.getLong("queries"));
      }

      JSONObject tasks = body.getJSONObject("tasks");
      if (tasks != null) {
         node.setTaskQueuedCount(tasks.getInteger("queued"));
         node.setTaskRunningCount(tasks.getInteger("running"));
         node.setTaskSucceededCount(tasks.getInteger("succeeded"));
         node.setTaskFailedCount(tasks.getInteger("failed"));
      }

      this.nodeMapper.updateById(node);
   }

   private void markHeartbeatFailed(DnsNode node, String reason) {
      int failCount = node.getHeartbeatFailCount() == null ? 1 : node.getHeartbeatFailCount() + 1;
      node.setHeartbeatFailCount(failCount);
      if (failCount >= 3) {
         node.setHeartbeatStatus(0);
      }

      this.nodeMapper.updateById(node);
      log.warn("DNS节点心跳失败，nodeId={}, failCount={}, reason={}", new Object[]{node.getId(), failCount, reason});
   }

   private String buildHeartbeatUrl(String apiUrl) {
      String baseUrl = StrUtil.removeSuffix(StrUtil.trim(apiUrl), "/");
      return baseUrl + "/api/v1/heartbeat";
   }

   private Date parseTime(String timestamp) {
      if (StrUtil.isBlank(timestamp)) {
         return new Date();
      } else {
         try {
            return DateUtil.parse(timestamp);
         } catch (Exception var3) {
            return new Date();
         }
      }
   }
}
