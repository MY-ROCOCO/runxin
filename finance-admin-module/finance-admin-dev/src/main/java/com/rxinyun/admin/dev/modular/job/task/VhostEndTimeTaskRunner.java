package com.rxinyun.admin.dev.modular.job.task;

import com.rxinyun.admin.dev.modular.log.util.LogUtil;
import com.rxinyun.business.api.MemberVhostApi;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import jakarta.annotation.Resource;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VhostEndTimeTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(VhostEndTimeTaskRunner.class);
   @Resource
   private MemberVhostApi memberVhostApi;

   public void action() {
      log.info("虚拟主机到期停用定时任务");
      List<BaseMemberVhost> hosts = this.memberVhostApi.listExpiredToStop();
      log.info("虚拟主机到期停用定时任务: 待处理数量={}", hosts.size());

      for (BaseMemberVhost host : hosts) {
         try {
            this.memberVhostApi.expireStop(host.getId());
            LogUtil.executeSystemOperationLog("虚拟主机到期停用", this.getClass(), "action", host, "{\"msg\":\"定时任务已执行虚拟主机到期停用\"}");
         } catch (Exception var5) {
            LogUtil.executeSystemExceptionLog("虚拟主机到期停用", this.getClass(), "action", host, var5);
            log.error("虚拟主机到期停用失败, hostId={}", host.getId(), var5);
         }
      }
   }
}
