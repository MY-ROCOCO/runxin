package com.rxinyun.admin.production.modular.dns.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.dns.entity.DnsLineGroup;
import com.rxinyun.admin.production.modular.dns.entity.DnsNode;
import com.rxinyun.admin.production.modular.dns.entity.DnsNodeCallbackLog;
import com.rxinyun.admin.production.modular.dns.entity.DnsNodeTask;
import com.rxinyun.admin.production.modular.dns.entity.DnsPackage;
import com.rxinyun.admin.production.modular.dns.entity.DnsPackageInstance;
import com.rxinyun.admin.production.modular.dns.param.DnsAdminParam;
import com.rxinyun.admin.production.modular.dns.param.DnsCallbackParam;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeInstallCommandResult;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeTokenResult;
import com.rxinyun.production.api.dto.DnsPackageInstanceCreateParam;
import com.rxinyun.production.api.dto.DnsPackageInstancePageParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceRenewParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceUpgradeQuotaParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import com.rxinyun.production.api.dto.DnsPackageVO;
import com.rxinyun.production.api.dto.DnsRecordCommandParam;
import com.rxinyun.production.api.dto.DnsZoneUpsertParam;
import java.util.List;
import java.util.Map;

public interface DnsProductionService {
   Page<DnsNode> nodePage(DnsAdminParam.BasePageParam pageParam);

   DnsNodeTokenResult addNode(DnsAdminParam.NodeAddParam addParam);

   void editNode(DnsAdminParam.NodeEditParam editParam);

   DnsNode nodeDetail(DnsAdminParam.IdParam idParam);

   void deleteNode(List<DnsAdminParam.IdParam> idParams);

   void updateNodeStatus(DnsAdminParam.StatusParam statusParam);

   DnsNodeTokenResult resetApiToken(DnsAdminParam.TokenResetParam resetParam);

   DnsNodeTokenResult resetCallbackToken(DnsAdminParam.TokenResetParam resetParam);

   DnsNodeInstallCommandResult installCommand(DnsAdminParam.IdParam idParam);

   void handleNodeCallback(DnsCallbackParam callbackParam, String authorization, String eventIdHeader, Map<String, String> requestHeaders, String requestBody);

   Page<DnsNodeTask> nodeTaskPage(DnsAdminParam.NodeTaskPageParam pageParam);

   Page<DnsNodeCallbackLog> nodeCallbackLogPage(DnsAdminParam.NodeCallbackLogPageParam pageParam);

   Page<DnsLineGroup> lineGroupPage(DnsAdminParam.BasePageParam pageParam);

   void addLineGroup(DnsAdminParam.LineGroupAddParam addParam);

   void editLineGroup(DnsAdminParam.LineGroupEditParam editParam);

   DnsLineGroup lineGroupDetail(DnsAdminParam.IdParam idParam);

   void deleteLineGroup(List<DnsAdminParam.IdParam> idParams);

   Page<DnsPackage> packagePage(DnsAdminParam.BasePageParam pageParam);

   void addPackage(DnsAdminParam.PackageAddParam addParam);

   void editPackage(DnsAdminParam.PackageEditParam editParam);

   DnsPackage packageDetail(DnsAdminParam.IdParam idParam);

   void deletePackage(List<DnsAdminParam.IdParam> idParams);

   void updatePackageStatus(DnsAdminParam.StatusParam statusParam);

   Page<DnsPackageInstance> packageInstancePage(DnsAdminParam.PackageInstancePageParam pageParam);

   void addPackageInstance(DnsAdminParam.PackageInstanceAddParam addParam);

   void editPackageInstance(DnsAdminParam.PackageInstanceEditParam editParam);

   DnsPackageInstance packageInstanceDetail(DnsAdminParam.IdParam idParam);

   void deletePackageInstance(List<DnsAdminParam.IdParam> idParams);

   DnsPackageInstanceVO getActiveInstance(String instanceId);

   DnsPackageVO getActivePackage(String packageId);

   Page<DnsPackageVO> activePackagePage(DnsPackageInstancePageParam pageParam);

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
