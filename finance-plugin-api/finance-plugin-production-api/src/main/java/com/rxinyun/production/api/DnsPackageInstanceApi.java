package com.rxinyun.production.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.production.api.dto.DnsPackageInstanceCreateParam;
import com.rxinyun.production.api.dto.DnsPackageInstancePageParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceRenewParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceUpgradeQuotaParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import com.rxinyun.production.api.dto.DnsPackageVO;
import com.rxinyun.production.api.dto.DnsRecordCommandParam;
import com.rxinyun.production.api.dto.DnsZoneUpsertParam;

public interface DnsPackageInstanceApi {
   Page<DnsPackageVO> activePackagePage(DnsPackageInstancePageParam pageParam);

   DnsPackageVO getActivePackage(String packageId);

   DnsPackageInstanceVO getActiveInstance(String instanceId);

   DnsPackageInstanceVO getMemberInstance(String instanceId, String memberId);

   Page<DnsPackageInstanceVO> memberInstancePage(DnsPackageInstancePageParam pageParam);

   DnsPackageInstanceVO createInstanceFromOrder(DnsPackageInstanceCreateParam createParam);

   DnsPackageInstanceVO renewInstanceFromOrder(DnsPackageInstanceRenewParam renewParam);

   DnsPackageInstanceVO upgradeInstanceQuotaFromOrder(DnsPackageInstanceUpgradeQuotaParam upgradeParam);

   DnsPackageInstanceVO getInstanceBySourceOrderNo(String sourceOrderNo);

   void occupyQuota(String instanceId, String memberId);

   void releaseQuota(String instanceId);

   void upsertZone(DnsZoneUpsertParam upsertParam);

   void deleteZone(DnsZoneUpsertParam deleteParam);

   void upsertRecord(DnsRecordCommandParam recordParam);

   void updateRecord(DnsRecordCommandParam recordParam);

   void deleteRecord(DnsRecordCommandParam recordParam);

   void setRecordEnabled(DnsRecordCommandParam recordParam);
}
