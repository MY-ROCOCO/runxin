package com.rxinyun.admin.production.modular.dns.service;

import cn.hutool.core.collection.CollUtil;
import com.rxinyun.admin.production.modular.dns.entity.DnsPackageInstance;
import com.rxinyun.admin.production.modular.dns.mapper.DnsPackageInstanceMapper;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.production.api.dto.DnsBoundDomainVO;
import com.rxinyun.production.api.dto.DnsZoneUpsertParam;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DnsPackageInstanceExpireService implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(DnsPackageInstanceExpireService.class);
   private static final int BATCH_SIZE = 100;
   private final AtomicBoolean running = new AtomicBoolean(false);
   @Resource
   private DnsPackageInstanceMapper packageInstanceMapper;
   @Resource
   private DnsProductionService dnsProductionService;

   public void action() {
      if (this.running.compareAndSet(false, true)) {
         try {
            for (DnsPackageInstance instance : this.packageInstanceMapper.selectExpiredActiveInstances(new Date(), 100)) {
               this.pauseExpiredInstance(instance);
            }
         } finally {
            this.running.set(false);
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void pauseExpiredInstance(DnsPackageInstance instance) {
      int updated = this.packageInstanceMapper.markExpired(instance.getId());
      if (updated > 0) {
         List<DnsBoundDomainVO> domains = this.packageInstanceMapper.selectBoundDomainsByInstanceIds(List.of(instance.getId()));
         if (!CollUtil.isEmpty(domains)) {
            this.packageInstanceMapper.pauseDomainsByInstanceId(instance.getId());

            for (DnsBoundDomainVO domain : domains) {
               try {
                  this.dnsProductionService.upsertZone(this.buildPausedZoneParam(domain));
               } catch (Exception var7) {
                  log.warn("DNS套餐实例到期暂停任务创建失败，instanceId={}, domainId={}, error={}", new Object[]{instance.getId(), domain.getId(), var7.getMessage()});
               }
            }

            log.info("DNS套餐实例到期暂停完成，instanceId={}, domainCount={}", instance.getId(), domains.size());
         }
      }
   }

   private DnsZoneUpsertParam buildPausedZoneParam(DnsBoundDomainVO domain) {
      DnsZoneUpsertParam param = new DnsZoneUpsertParam();
      param.setZoneId(domain.getId());
      param.setPackageInstanceId(domain.getPackageInstanceId());
      param.setDomainName(domain.getDomainName());
      param.setAdminEmail(domain.getEmail());
      param.setEnabled(false);
      return param;
   }
}
