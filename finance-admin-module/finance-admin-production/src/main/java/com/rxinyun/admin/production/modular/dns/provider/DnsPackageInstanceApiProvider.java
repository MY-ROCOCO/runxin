package com.rxinyun.admin.production.modular.dns.provider;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.dns.service.DnsProductionService;
import com.rxinyun.production.api.DnsPackageInstanceApi;
import com.rxinyun.production.api.dto.DnsPackageInstanceCreateParam;
import com.rxinyun.production.api.dto.DnsPackageInstancePageParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceRenewParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceUpgradeQuotaParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import com.rxinyun.production.api.dto.DnsPackageVO;
import com.rxinyun.production.api.dto.DnsRecordCommandParam;
import com.rxinyun.production.api.dto.DnsZoneUpsertParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class DnsPackageInstanceApiProvider implements DnsPackageInstanceApi {
   @Resource
   private DnsProductionService dnsProductionService;

   public Page<DnsPackageVO> activePackagePage(DnsPackageInstancePageParam pageParam) {
      return this.dnsProductionService.activePackagePage(pageParam);
   }

   public DnsPackageVO getActivePackage(String packageId) {
      return this.dnsProductionService.getActivePackage(packageId);
   }

   public DnsPackageInstanceVO getActiveInstance(String instanceId) {
      return this.dnsProductionService.getActiveInstance(instanceId);
   }

   public DnsPackageInstanceVO getMemberInstance(String instanceId, String memberId) {
      return this.dnsProductionService.getMemberInstance(instanceId, memberId);
   }

   public Page<DnsPackageInstanceVO> memberInstancePage(DnsPackageInstancePageParam pageParam) {
      return this.dnsProductionService.memberInstancePage(pageParam);
   }

   public DnsPackageInstanceVO createInstanceFromOrder(DnsPackageInstanceCreateParam createParam) {
      return this.dnsProductionService.createInstanceFromOrder(createParam);
   }

   public DnsPackageInstanceVO renewInstanceFromOrder(DnsPackageInstanceRenewParam renewParam) {
      return this.dnsProductionService.renewInstanceFromOrder(renewParam);
   }

   public DnsPackageInstanceVO upgradeInstanceQuotaFromOrder(DnsPackageInstanceUpgradeQuotaParam upgradeParam) {
      return this.dnsProductionService.upgradeInstanceQuotaFromOrder(upgradeParam);
   }

   public DnsPackageInstanceVO getInstanceBySourceOrderNo(String sourceOrderNo) {
      return this.dnsProductionService.getInstanceBySourceOrderNo(sourceOrderNo);
   }

   public void occupyQuota(String instanceId, String memberId) {
      this.dnsProductionService.occupyQuota(instanceId, memberId);
   }

   public void releaseQuota(String instanceId) {
      this.dnsProductionService.releaseQuota(instanceId);
   }

   public void upsertZone(DnsZoneUpsertParam upsertParam) {
      this.dnsProductionService.upsertZone(upsertParam);
   }

   public void deleteZone(DnsZoneUpsertParam deleteParam) {
      this.dnsProductionService.deleteZone(deleteParam);
   }

   public void upsertRecord(DnsRecordCommandParam recordParam) {
      this.dnsProductionService.upsertRecord(recordParam);
   }

   public void updateRecord(DnsRecordCommandParam recordParam) {
      this.dnsProductionService.updateRecord(recordParam);
   }

   public void deleteRecord(DnsRecordCommandParam recordParam) {
      this.dnsProductionService.deleteRecord(recordParam);
   }

   public void setRecordEnabled(DnsRecordCommandParam recordParam) {
      this.dnsProductionService.setRecordEnabled(recordParam);
   }
}
