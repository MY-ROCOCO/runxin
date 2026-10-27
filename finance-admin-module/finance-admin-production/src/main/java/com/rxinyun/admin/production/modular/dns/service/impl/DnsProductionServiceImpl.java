package com.rxinyun.admin.production.modular.dns.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.dns.entity.DnsLineGroup;
import com.rxinyun.admin.production.modular.dns.entity.DnsLineGroupNode;
import com.rxinyun.admin.production.modular.dns.entity.DnsNode;
import com.rxinyun.admin.production.modular.dns.entity.DnsNodeCallbackLog;
import com.rxinyun.admin.production.modular.dns.entity.DnsNodeTask;
import com.rxinyun.admin.production.modular.dns.entity.DnsPackage;
import com.rxinyun.admin.production.modular.dns.entity.DnsPackageInstance;
import com.rxinyun.admin.production.modular.dns.mapper.DnsLineGroupMapper;
import com.rxinyun.admin.production.modular.dns.mapper.DnsLineGroupNodeMapper;
import com.rxinyun.admin.production.modular.dns.mapper.DnsNodeCallbackLogMapper;
import com.rxinyun.admin.production.modular.dns.mapper.DnsNodeMapper;
import com.rxinyun.admin.production.modular.dns.mapper.DnsNodeTaskMapper;
import com.rxinyun.admin.production.modular.dns.mapper.DnsPackageInstanceMapper;
import com.rxinyun.admin.production.modular.dns.mapper.DnsPackageMapper;
import com.rxinyun.admin.production.modular.dns.param.DnsAdminParam;
import com.rxinyun.admin.production.modular.dns.param.DnsCallbackParam;
import com.rxinyun.admin.production.modular.dns.service.DnsProductionService;
import com.rxinyun.admin.production.modular.dns.util.DnsTokenUtil;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeInstallCommandResult;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeReplaceConfigRecord;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeReplaceConfigZone;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeTokenResult;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.member.api.MemberApi;
import com.rxinyun.member.member.pojo.BaseMember;
import com.rxinyun.production.api.dto.DnsBoundDomainVO;
import com.rxinyun.production.api.dto.DnsPackageInstanceCreateParam;
import com.rxinyun.production.api.dto.DnsPackageInstancePageParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceRenewParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceUpgradeQuotaParam;
import com.rxinyun.production.api.dto.DnsPackageInstanceVO;
import com.rxinyun.production.api.dto.DnsPackageVO;
import com.rxinyun.production.api.dto.DnsRecordCommandParam;
import com.rxinyun.production.api.dto.DnsZoneUpsertParam;
import jakarta.annotation.Resource;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DnsProductionServiceImpl implements DnsProductionService {
   private static final Logger log = LoggerFactory.getLogger(DnsProductionServiceImpl.class);
   private static final int ENABLED = 1;
   private static final int INSTALLED = 1;
   private static final int TASK_SUCCEEDED = 3;
   private static final int TASK_FAILED = -1;
   private static final String NODE_COMMAND_PATH = "/api/v1/commands";
   private static final String BIZ_TYPE_CONFIG = "CONFIG";
   private static final String BIZ_TYPE_DOMAIN = "DOMAIN";
   private static final String BIZ_TYPE_RECORD = "RECORD";
   private static final String ACTION_REPLACE_CONFIG = "REPLACE_CONFIG";
   private static final String ACTION_UPSERT_ZONE = "UPSERT_ZONE";
   private static final String ACTION_DELETE_ZONE = "DELETE_ZONE";
   private static final String ACTION_UPSERT_RECORD = "UPSERT_RECORD";
   private static final String ACTION_UPDATE_RECORD = "UPDATE_RECORD";
   private static final String ACTION_DELETE_RECORD = "DELETE_RECORD";
   private static final String ACTION_SET_RECORD_ENABLED = "SET_RECORD_ENABLED";
   private static final String EVENT_NODE_INSTALLED = "NODE_INSTALLED";
   private static final String EVENT_TASK_RESULT = "TASK_RESULT";
   private static final String CALLBACK_STATUS_SUCCEEDED = "SUCCEEDED";
   private static final String INSTALL_SCRIPT_URL = "https://agentdns.reckonkvm.com/install.sh";
   @Resource
   private DnsNodeMapper nodeMapper;
   @Resource
   private DnsNodeTaskMapper nodeTaskMapper;
   @Resource
   private DnsNodeCallbackLogMapper nodeCallbackLogMapper;
   @Resource
   private DnsLineGroupMapper lineGroupMapper;
   @Resource
   private DnsLineGroupNodeMapper lineGroupNodeMapper;
   @Resource
   private DnsPackageMapper packageMapper;
   @Resource
   private DnsPackageInstanceMapper packageInstanceMapper;
   @Resource
   private MemberApi memberApi;

   @Override
   public Page<DnsNode> nodePage(DnsAdminParam.BasePageParam pageParam) {
      QueryWrapper<DnsNode> wrapper = this.baseWrapper(pageParam, CommonEntity::getCreateTime);
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<DnsNode>)((LambdaQueryWrapper<DnsNode>)q.like(DnsNode::getName, pageParam.getSearchKey())).or())
                  .like(DnsNode::getApiUrl, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         wrapper.lambda().eq(DnsNode::getStatus, pageParam.getStatus());
      }

      Page<DnsNode> page = (Page<DnsNode>)this.nodeMapper.selectPage(CommonPageRequest.defaultPage(), wrapper);
      page.getRecords().forEach(this::maskNode);
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public DnsNodeTokenResult addNode(DnsAdminParam.NodeAddParam addParam) {
      this.validateNodeApiAddress(addParam.getApiUrl());
      this.validateHttpUrl(addParam.getCallbackUrl(), "Java服务回调地址");
      String apiToken = this.generateUniqueApiToken(null);
      String callbackToken = DnsTokenUtil.generateToken();
      DnsNode node = (DnsNode)BeanUtil.toBean(addParam, DnsNode.class);
      node.setId(IdUtil.getSnowflakeNextIdStr());
      node.setApiUrl(StrUtil.trim(addParam.getApiUrl()));
      node.setCallbackUrl(StrUtil.trim(addParam.getCallbackUrl()));
      node.setStatus(this.defaultStatus(node.getStatus()));
      node.setApiToken(DnsTokenUtil.encryptApiToken(apiToken));
      node.setCallbackTokenHash(DnsTokenUtil.hashCallbackToken(callbackToken));
      this.nodeMapper.insert(node);
      return new DnsNodeTokenResult(node.getId(), apiToken, callbackToken);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void editNode(DnsAdminParam.NodeEditParam editParam) {
      DnsNode node = this.queryNode(editParam.getId());
      this.validateNodeApiAddress(editParam.getApiUrl());
      this.validateHttpUrl(editParam.getCallbackUrl(), "Java服务回调地址");
      BeanUtil.copyProperties(editParam, node, new String[0]);
      node.setApiUrl(StrUtil.trim(editParam.getApiUrl()));
      node.setCallbackUrl(StrUtil.trim(editParam.getCallbackUrl()));
      node.setStatus(this.defaultStatus(node.getStatus()));
      this.nodeMapper.updateById(node);
   }

   @Override
   public DnsNode nodeDetail(DnsAdminParam.IdParam idParam) {
      return this.maskNode(this.queryNode(idParam.getId()));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteNode(List<DnsAdminParam.IdParam> idParams) {
      List<String> ids = CollStreamUtil.toList(idParams, DnsAdminParam.IdParam::getId);
      if (!CollUtil.isEmpty(ids)) {
         for (String id : ids) {
            long count = this.lineGroupNodeMapper.selectCount((Wrapper<DnsLineGroupNode>)new LambdaQueryWrapper<DnsLineGroupNode>().eq(DnsLineGroupNode::getNodeId, id));
            if (count > 0L) {
               throw new CommonException("DNS节点已绑定线路分组，无法删除：{}", new Object[]{id});
            }
         }

         this.nodeMapper.physicalDeleteByIds(ids);
      }
   }

   @Override
   public void updateNodeStatus(DnsAdminParam.StatusParam statusParam) {
      DnsNode node = this.queryNode(statusParam.getId());
      node.setStatus(statusParam.getStatus());
      this.nodeMapper.updateById(node);
   }

   @Override
   public DnsNodeTokenResult resetApiToken(DnsAdminParam.TokenResetParam resetParam) {
      DnsNode node = this.queryNode(resetParam.getId());
      String token = StrUtil.blankToDefault(resetParam.getToken(), DnsTokenUtil.generateToken());
      this.validateToken(token);
      String encryptedToken = DnsTokenUtil.encryptApiToken(token);
      this.validateUniqueApiToken(encryptedToken, node.getId());
      node.setApiToken(encryptedToken);
      this.nodeMapper.updateById(node);
      return new DnsNodeTokenResult(node.getId(), token, null);
   }

   @Override
   public DnsNodeTokenResult resetCallbackToken(DnsAdminParam.TokenResetParam resetParam) {
      DnsNode node = this.queryNode(resetParam.getId());
      String token = StrUtil.blankToDefault(resetParam.getToken(), DnsTokenUtil.generateToken());
      this.validateToken(token);
      node.setCallbackTokenHash(DnsTokenUtil.hashCallbackToken(token));
      this.nodeMapper.updateById(node);
      return new DnsNodeTokenResult(node.getId(), null, token);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public DnsNodeInstallCommandResult installCommand(DnsAdminParam.IdParam idParam) {
      DnsNode node = this.queryNode(idParam.getId());
      String callbackToken = DnsTokenUtil.generateToken();
      node.setCallbackTokenHash(DnsTokenUtil.hashCallbackToken(callbackToken));
      this.nodeMapper.updateById(node);
      String apiToken = DnsTokenUtil.decryptApiToken(node.getApiToken());
      String command = "curl -fsSL https://agentdns.reckonkvm.com/install.sh | sudo bash -s -- --node-id "
         + this.shellQuote(node.getId())
         + " --api-token "
         + this.shellQuote(apiToken)
         + " --callback-url "
         + this.shellQuote(node.getCallbackUrl())
         + " --callback-token "
         + this.shellQuote(callbackToken);
      return new DnsNodeInstallCommandResult(node.getId(), command, callbackToken);
   }

   @Transactional(
      noRollbackFor = {Exception.class}
   )
   @Override
   public void handleNodeCallback(
      DnsCallbackParam callbackParam, String authorization, String eventIdHeader, Map<String, String> requestHeaders, String requestBody
   ) {
      String eventId = StrUtil.blankToDefault(callbackParam.getEventId(), eventIdHeader);
      if (StrUtil.isBlank(eventId)) {
         throw new CommonException(400, "DNS节点回调缺少eventId", new Object[0]);
      } else {
         callbackParam.setEventId(eventId);
         DnsNodeCallbackLog callbackLog = (DnsNodeCallbackLog)this.nodeCallbackLogMapper
            .selectOne((Wrapper<DnsNodeCallbackLog>)new LambdaQueryWrapper<DnsNodeCallbackLog>().eq(DnsNodeCallbackLog::getEventId, eventId));
         if (callbackLog == null
            || !Objects.equals(callbackLog.getProcessStatus(), 1)
            || this.shouldReprocessInstallCallback(callbackParam, callbackLog, requestBody)) {
            if (callbackLog == null) {
               callbackLog = this.buildCallbackLog(callbackParam, requestHeaders, requestBody);
            } else {
               this.refreshCallbackLog(callbackLog, callbackParam, requestHeaders, requestBody);
            }

            DnsNode node = this.queryNode(callbackParam.getNodeId());
            String callbackToken = this.resolveBearerToken(authorization);
            if (!DnsTokenUtil.matchesCallbackToken(callbackToken, node.getCallbackTokenHash())) {
               callbackLog.setSignStatus(0);
               callbackLog.setProcessStatus(0);
               callbackLog.setResponseBody("验签失败");
               callbackLog.setErrorMessage("回调Token校验失败");
               this.saveCallbackLog(callbackLog);
               throw new CommonException(401, "DNS节点回调验签失败", new Object[0]);
            } else {
               try {
                  DnsNodeTask task = null;
                  if ("NODE_INSTALLED".equals(callbackParam.getEventType())) {
                     this.handleNodeInstalled(node, callbackParam);
                  } else {
                     if (!"TASK_RESULT".equals(callbackParam.getEventType())) {
                        throw new CommonException("不支持的DNS节点回调事件：{}", new Object[]{callbackParam.getEventType()});
                     }

                     task = this.handleTaskResult(node, callbackParam, requestBody);
                  }

                  if (task != null) {
                     callbackLog.setTaskId(task.getId());
                     callbackLog.setRequestId(task.getRequestId());
                     callbackLog.setNodeTaskId(task.getNodeTaskId());
                  }

                  callbackLog.setSignStatus(1);
                  callbackLog.setProcessStatus(1);
                  callbackLog.setResponseBody("OK");
                  callbackLog.setErrorMessage(null);
                  this.saveCallbackLog(callbackLog);
               } catch (Exception var11) {
                  callbackLog.setSignStatus(1);
                  callbackLog.setProcessStatus(0);
                  callbackLog.setResponseBody("处理失败");
                  callbackLog.setErrorMessage(StrUtil.maxLength(var11.getMessage(), 1000));
                  this.saveCallbackLog(callbackLog);
                  throw var11;
               }
            }
         }
      }
   }

   @Override
   public Page<DnsNodeTask> nodeTaskPage(DnsAdminParam.NodeTaskPageParam pageParam) {
      QueryWrapper<DnsNodeTask> wrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getNodeId())) {
         wrapper.lambda().eq(DnsNodeTask::getNodeId, pageParam.getNodeId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getBizType())) {
         wrapper.lambda().eq(DnsNodeTask::getBizType, pageParam.getBizType());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getBizId())) {
         wrapper.lambda().eq(DnsNodeTask::getBizId, pageParam.getBizId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getAction())) {
         wrapper.lambda().eq(DnsNodeTask::getAction, pageParam.getAction());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getTaskStatus())) {
         wrapper.lambda().eq(DnsNodeTask::getTaskStatus, pageParam.getTaskStatus());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)((LambdaQueryWrapper<DnsNodeTask>)q.like(
                                    DnsNodeTask::getRequestId, pageParam.getSearchKey()
                                 ))
                                 .or())
                              .like(DnsNodeTask::getNodeTaskId, pageParam.getSearchKey()))
                           .or())
                        .like(DnsNodeTask::getBizId, pageParam.getSearchKey()))
                     .or())
                  .like(DnsNodeTask::getAction, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         wrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<DnsNodeTask> page = (Page<DnsNodeTask>)this.nodeTaskMapper.selectPage(CommonPageRequest.defaultPage(), wrapper);
      this.fillLatestCallbackInfo(page.getRecords());
      return page;
   }

   @Override
   public Page<DnsNodeCallbackLog> nodeCallbackLogPage(DnsAdminParam.NodeCallbackLogPageParam pageParam) {
      QueryWrapper<DnsNodeCallbackLog> wrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getTaskId())) {
         wrapper.lambda().eq(DnsNodeCallbackLog::getTaskId, pageParam.getTaskId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getRequestId())) {
         wrapper.lambda().eq(DnsNodeCallbackLog::getRequestId, pageParam.getRequestId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getNodeId())) {
         wrapper.lambda().eq(DnsNodeCallbackLog::getNodeId, pageParam.getNodeId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getNodeTaskId())) {
         wrapper.lambda().eq(DnsNodeCallbackLog::getNodeTaskId, pageParam.getNodeTaskId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getEventType())) {
         wrapper.lambda().eq(DnsNodeCallbackLog::getEventType, pageParam.getEventType());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getProcessStatus())) {
         wrapper.lambda().eq(DnsNodeCallbackLog::getProcessStatus, pageParam.getProcessStatus());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<DnsNodeCallbackLog>)((LambdaQueryWrapper<DnsNodeCallbackLog>)((LambdaQueryWrapper<DnsNodeCallbackLog>)((LambdaQueryWrapper<DnsNodeCallbackLog>)((LambdaQueryWrapper<DnsNodeCallbackLog>)((LambdaQueryWrapper<DnsNodeCallbackLog>)q.like(
                                    DnsNodeCallbackLog::getEventId, pageParam.getSearchKey()
                                 ))
                                 .or())
                              .like(DnsNodeCallbackLog::getRequestId, pageParam.getSearchKey()))
                           .or())
                        .like(DnsNodeCallbackLog::getNodeTaskId, pageParam.getSearchKey()))
                     .or())
                  .like(DnsNodeCallbackLog::getAction, pageParam.getSearchKey())
            );
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         wrapper.lambda().orderByDesc(DnsNodeCallbackLog::getCallbackTime);
      }

      return (Page<DnsNodeCallbackLog>)this.nodeCallbackLogMapper.selectPage(CommonPageRequest.defaultPage(), wrapper);
   }

   @Override
   public Page<DnsLineGroup> lineGroupPage(DnsAdminParam.BasePageParam pageParam) {
      QueryWrapper<DnsLineGroup> wrapper = this.baseWrapper(pageParam, CommonEntity::getCreateTime);
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda().like(DnsLineGroup::getName, pageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         wrapper.lambda().eq(DnsLineGroup::getStatus, pageParam.getStatus());
      }

      Page<DnsLineGroup> page = (Page<DnsLineGroup>)this.lineGroupMapper.selectPage(CommonPageRequest.defaultPage(), wrapper);
      page.getRecords().forEach(this::fillLineGroupNodes);
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void addLineGroup(DnsAdminParam.LineGroupAddParam addParam) {
      DnsLineGroup group = (DnsLineGroup)BeanUtil.toBean(addParam, DnsLineGroup.class);
      group.setId(IdUtil.getSnowflakeNextIdStr());
      group.setStatus(this.defaultStatus(group.getStatus()));
      this.lineGroupMapper.insert(group);
      this.replaceLineGroupNodes(group.getId(), addParam.getNodeIds());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void editLineGroup(DnsAdminParam.LineGroupEditParam editParam) {
      DnsLineGroup group = this.queryLineGroup(editParam.getId());
      BeanUtil.copyProperties(editParam, group, new String[0]);
      group.setStatus(this.defaultStatus(group.getStatus()));
      this.lineGroupMapper.updateById(group);
      this.replaceLineGroupNodes(group.getId(), editParam.getNodeIds());
   }

   @Override
   public DnsLineGroup lineGroupDetail(DnsAdminParam.IdParam idParam) {
      return this.fillLineGroupNodes(this.queryLineGroup(idParam.getId()));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteLineGroup(List<DnsAdminParam.IdParam> idParams) {
      List<String> ids = CollStreamUtil.toList(idParams, DnsAdminParam.IdParam::getId);
      if (!CollUtil.isEmpty(ids)) {
         for (String id : ids) {
            long count = this.packageMapper.selectCount((Wrapper<DnsPackage>)new LambdaQueryWrapper<DnsPackage>().eq(DnsPackage::getLineGroupId, id));
            if (count > 0L) {
               throw new CommonException("DNS线路分组已被套餐引用，无法删除：{}", new Object[]{id});
            }

            this.lineGroupNodeMapper.physicalDeleteByGroupId(id);
         }

         this.lineGroupMapper.physicalDeleteByIds(ids);
      }
   }

   @Override
   public Page<DnsPackage> packagePage(DnsAdminParam.BasePageParam pageParam) {
      QueryWrapper<DnsPackage> wrapper = this.baseWrapper(pageParam, CommonEntity::getCreateTime);
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda().like(DnsPackage::getName, pageParam.getSearchKey());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         wrapper.lambda().eq(DnsPackage::getStatus, pageParam.getStatus());
      }

      return (Page<DnsPackage>)this.packageMapper.selectPage(CommonPageRequest.defaultPage(), wrapper);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void addPackage(DnsAdminParam.PackageAddParam addParam) {
      this.validateActiveLineGroup(addParam.getLineGroupId());
      DnsPackage dnsPackage = (DnsPackage)BeanUtil.toBean(addParam, DnsPackage.class);
      dnsPackage.setId(IdUtil.getSnowflakeNextIdStr());
      dnsPackage.setStatus(this.defaultStatus(dnsPackage.getStatus()));
      this.packageMapper.insert(dnsPackage);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void editPackage(DnsAdminParam.PackageEditParam editParam) {
      DnsPackage dnsPackage = this.queryPackage(editParam.getId());
      this.validateActiveLineGroup(editParam.getLineGroupId());
      BeanUtil.copyProperties(editParam, dnsPackage, new String[0]);
      dnsPackage.setStatus(this.defaultStatus(dnsPackage.getStatus()));
      this.packageMapper.updateById(dnsPackage);
   }

   @Override
   public DnsPackage packageDetail(DnsAdminParam.IdParam idParam) {
      return this.queryPackage(idParam.getId());
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deletePackage(List<DnsAdminParam.IdParam> idParams) {
      List<String> ids = CollStreamUtil.toList(idParams, DnsAdminParam.IdParam::getId);
      if (!CollUtil.isEmpty(ids)) {
         for (String id : ids) {
            long count = this.packageInstanceMapper.selectCount((Wrapper<DnsPackageInstance>)new LambdaQueryWrapper<DnsPackageInstance>().eq(DnsPackageInstance::getPackageId, id));
            if (count > 0L) {
               throw new CommonException("DNS套餐已开通实例，无法删除：{}", new Object[]{id});
            }
         }

         this.packageMapper.physicalDeleteByIds(ids);
      }
   }

   @Override
   public void updatePackageStatus(DnsAdminParam.StatusParam statusParam) {
      DnsPackage dnsPackage = this.queryPackage(statusParam.getId());
      dnsPackage.setStatus(statusParam.getStatus());
      this.packageMapper.updateById(dnsPackage);
   }

   @Override
   public Page<DnsPackageInstance> packageInstancePage(DnsAdminParam.PackageInstancePageParam pageParam) {
      QueryWrapper<DnsPackageInstance> wrapper = this.baseWrapper(pageParam, CommonEntity::getCreateTime);
      if (ObjectUtil.isNotEmpty(pageParam.getMemberId())) {
         wrapper.lambda().eq(DnsPackageInstance::getMemberId, pageParam.getMemberId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getPackageId())) {
         wrapper.lambda().eq(DnsPackageInstance::getPackageId, pageParam.getPackageId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         wrapper.lambda().eq(DnsPackageInstance::getStatus, pageParam.getStatus());
      }

      Page<DnsPackageInstance> page = (Page<DnsPackageInstance>)this.packageInstanceMapper.selectPage(CommonPageRequest.defaultPage(), wrapper);
      this.fillPackageInstanceNameServers(page.getRecords());
      this.fillPackageInstanceDomains(page.getRecords());
      return page;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void addPackageInstance(DnsAdminParam.PackageInstanceAddParam addParam) {
      DnsPackage dnsPackage = this.queryPackage(addParam.getPackageId());
      if (!Objects.equals(dnsPackage.getStatus(), 1)) {
         throw new CommonException("DNS套餐未启用，无法开通实例：{}", new Object[]{addParam.getPackageId()});
      } else {
         this.validateMemberExists(addParam.getMemberId());
         DnsPackageInstance instance = this.buildPackageInstance(
            dnsPackage,
            addParam.getMemberId(),
            addParam.getDomainQuota(),
            addParam.getPeriodMonths(),
            addParam.getStartTime(),
            addParam.getSourceOrderNo(),
            addParam.getRemark()
         );
         instance.setStatus(this.defaultStatus(addParam.getStatus()));
         this.packageInstanceMapper.insert(instance);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void editPackageInstance(DnsAdminParam.PackageInstanceEditParam editParam) {
      DnsPackageInstance instance = this.queryPackageInstance(editParam.getId());
      if (editParam.getDomainQuota() != null && editParam.getDomainQuota() < instance.getUsedQuota()) {
         throw new CommonException("域名配额不能小于已用配额", new Object[0]);
      } else {
         if (editParam.getDomainQuota() != null) {
            instance.setDomainQuota(editParam.getDomainQuota());
         }

         if (editParam.getPeriodMonths() != null) {
            instance.setPeriodMonths(editParam.getPeriodMonths());
         }

         if (editParam.getNextDueTime() != null) {
            instance.setNextDueTime(editParam.getNextDueTime());
         }

         if (editParam.getStatus() != null) {
            instance.setStatus(editParam.getStatus());
         }

         instance.setRemark(editParam.getRemark());
         this.packageInstanceMapper.updateById(instance);
      }
   }

   @Override
   public DnsPackageInstance packageInstanceDetail(DnsAdminParam.IdParam idParam) {
      DnsPackageInstance instance = this.queryPackageInstance(idParam.getId());
      this.fillPackageInstanceNameServers(Collections.singletonList(instance));
      this.fillPackageInstanceDomains(Collections.singletonList(instance));
      return instance;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deletePackageInstance(List<DnsAdminParam.IdParam> idParams) {
      List<String> ids = CollStreamUtil.toList(idParams, DnsAdminParam.IdParam::getId);
      if (!CollUtil.isEmpty(ids)) {
         for (String id : ids) {
            DnsPackageInstance instance = this.queryPackageInstance(id);
            if ((Integer)ObjectUtil.defaultIfNull(instance.getUsedQuota(), 0) > 0) {
               throw new CommonException("DNS套餐实例已有域名占用，无法删除：{}", new Object[]{id});
            }
         }

         this.packageInstanceMapper.physicalDeleteByIds(ids);
      }
   }

   @Override
   public DnsPackageInstanceVO getActiveInstance(String instanceId) {
      DnsPackageInstance instance = this.queryPackageInstance(instanceId);
      if (!Objects.equals(instance.getStatus(), 1)) {
         throw new CommonException("DNS套餐实例不可用：{}", new Object[]{instanceId});
      } else {
         DnsPackageInstanceVO instanceVO = (DnsPackageInstanceVO)BeanUtil.toBean(instance, DnsPackageInstanceVO.class);
         this.fillPackageInstanceVoNameServers(Collections.singletonList(instanceVO));
         return instanceVO;
      }
   }

   @Override
   public DnsPackageVO getActivePackage(String packageId) {
      DnsPackage dnsPackage = this.queryPackage(packageId);
      if (!Objects.equals(dnsPackage.getStatus(), 1)) {
         throw new CommonException("DNS套餐未启用：{}", new Object[]{packageId});
      } else {
         return (DnsPackageVO)BeanUtil.toBean(dnsPackage, DnsPackageVO.class);
      }
   }

   @Override
   public Page<DnsPackageVO> activePackagePage(DnsPackageInstancePageParam pageParam) {
      QueryWrapper<DnsPackage> wrapper = new QueryWrapper().checkSqlInjection();
      wrapper.lambda().eq(DnsPackage::getStatus, 1);
      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda().like(DnsPackage::getName, pageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         wrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<DnsPackage> page = (Page<DnsPackage>)this.packageMapper.selectPage(CommonPageRequest.defaultPage(), wrapper);
      Page<DnsPackageVO> voPage = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      voPage.setRecords(
         page.getRecords().stream().map(dnsPackage -> (DnsPackageVO)BeanUtil.toBean(dnsPackage, DnsPackageVO.class)).collect(Collectors.toList())
      );
      return voPage;
   }

   @Override
   public DnsPackageInstanceVO getMemberInstance(String instanceId, String memberId) {
      DnsPackageInstance instance = this.queryPackageInstance(instanceId);
      if (!Objects.equals(instance.getMemberId(), memberId)) {
         throw new CommonException("DNS套餐实例不属于该会员", new Object[0]);
      } else {
         DnsPackageInstanceVO instanceVO = (DnsPackageInstanceVO)BeanUtil.toBean(instance, DnsPackageInstanceVO.class);
         this.fillPackageInstanceVoNameServers(Collections.singletonList(instanceVO));
         return instanceVO;
      }
   }

   @Override
   public Page<DnsPackageInstanceVO> memberInstancePage(DnsPackageInstancePageParam pageParam) {
      QueryWrapper<DnsPackageInstance> wrapper = new QueryWrapper().checkSqlInjection();
      wrapper.lambda().eq(DnsPackageInstance::getMemberId, pageParam.getMemberId());
      if (ObjectUtil.isNotEmpty(pageParam.getPackageId())) {
         wrapper.lambda().eq(DnsPackageInstance::getPackageId, pageParam.getPackageId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         wrapper.lambda().eq(DnsPackageInstance::getStatus, pageParam.getStatus());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         wrapper.lambda().like(DnsPackageInstance::getPackageName, pageParam.getSearchKey());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         wrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<DnsPackageInstance> page = (Page<DnsPackageInstance>)this.packageInstanceMapper.selectPage(CommonPageRequest.defaultPage(), wrapper);
      Page<DnsPackageInstanceVO> voPage = new Page(page.getCurrent(), page.getSize(), page.getTotal());
      List<DnsPackageInstanceVO> records = page.getRecords()
         .stream()
         .map(instance -> (DnsPackageInstanceVO)BeanUtil.toBean(instance, DnsPackageInstanceVO.class))
         .collect(Collectors.toList());
      this.fillPackageInstanceVoNameServers(records);
      this.fillPackageInstanceVoDomains(records);
      voPage.setRecords(records);
      return voPage;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public DnsPackageInstanceVO createInstanceFromOrder(DnsPackageInstanceCreateParam createParam) {
      DnsPackage dnsPackage = this.queryPackage(createParam.getPackageId());
      if (!Objects.equals(dnsPackage.getStatus(), 1)) {
         throw new CommonException("DNS套餐未启用：{}", new Object[]{createParam.getPackageId()});
      } else {
         this.validateMemberExists(createParam.getMemberId());
         DnsPackageInstance existed = (DnsPackageInstance)this.packageInstanceMapper
            .selectOne((Wrapper<DnsPackageInstance>)new LambdaQueryWrapper<DnsPackageInstance>().eq(DnsPackageInstance::getSourceOrderNo, createParam.getSourceOrderNo()));
         if (existed != null) {
            return (DnsPackageInstanceVO)BeanUtil.toBean(existed, DnsPackageInstanceVO.class);
         } else {
            DnsPackageInstance instance = this.buildPackageInstance(
               dnsPackage,
               createParam.getMemberId(),
               createParam.getDomainQuota(),
               createParam.getPeriodMonths(),
               createParam.getStartTime(),
               createParam.getSourceOrderNo(),
               createParam.getRemark()
            );
            this.packageInstanceMapper.insert(instance);
            return (DnsPackageInstanceVO)BeanUtil.toBean(instance, DnsPackageInstanceVO.class);
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public DnsPackageInstanceVO renewInstanceFromOrder(DnsPackageInstanceRenewParam renewParam) {
      DnsPackageInstance instance = this.queryPackageInstance(renewParam.getInstanceId());
      this.validateInstanceMember(instance, renewParam.getMemberId());
      int periodMonths = (Integer)ObjectUtil.defaultIfNull(renewParam.getPeriodMonths(), 1);
      Date baseTime = (Date)ObjectUtil.defaultIfNull(instance.getNextDueTime(), new Date());
      if (baseTime.before(new Date())) {
         baseTime = new Date();
      }

      instance.setNextDueTime(DateUtil.offsetMonth(baseTime, periodMonths));
      instance.setPeriodMonths(periodMonths);
      instance.setStatus(1);
      this.packageInstanceMapper.updateById(instance);
      this.enableDomainsAfterRenew(instance);
      return (DnsPackageInstanceVO)BeanUtil.toBean(instance, DnsPackageInstanceVO.class);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public DnsPackageInstanceVO upgradeInstanceQuotaFromOrder(DnsPackageInstanceUpgradeQuotaParam upgradeParam) {
      DnsPackageInstance instance = this.queryPackageInstance(upgradeParam.getInstanceId());
      this.validateInstanceMember(instance, upgradeParam.getMemberId());
      int upgradeQuota = (Integer)ObjectUtil.defaultIfNull(upgradeParam.getUpgradeDomainQuota(), 0);
      if (upgradeQuota <= 0) {
         throw new CommonException("升级域名数量必须大于0", new Object[0]);
      } else {
         instance.setDomainQuota((Integer)ObjectUtil.defaultIfNull(instance.getDomainQuota(), 0) + upgradeQuota);
         this.packageInstanceMapper.updateById(instance);
         return (DnsPackageInstanceVO)BeanUtil.toBean(instance, DnsPackageInstanceVO.class);
      }
   }

   @Override
   public DnsPackageInstanceVO getInstanceBySourceOrderNo(String sourceOrderNo) {
      if (StrUtil.isBlank(sourceOrderNo)) {
         return null;
      } else {
         DnsPackageInstance instance = (DnsPackageInstance)this.packageInstanceMapper
            .selectOne((Wrapper<DnsPackageInstance>)new LambdaQueryWrapper<DnsPackageInstance>().eq(DnsPackageInstance::getSourceOrderNo, sourceOrderNo));
         return instance == null ? null : (DnsPackageInstanceVO)BeanUtil.toBean(instance, DnsPackageInstanceVO.class);
      }
   }

   @Override
   public void occupyQuota(String instanceId, String memberId) {
      int updated = this.packageInstanceMapper.occupyQuota(instanceId, memberId);
      if (updated <= 0) {
         throw new CommonException("DNS套餐实例不可用或域名配额不足", new Object[0]);
      }
   }

   @Override
   public void releaseQuota(String instanceId) {
      this.packageInstanceMapper.releaseQuota(instanceId);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void upsertZone(DnsZoneUpsertParam upsertParam) {
      DnsPackageInstance instance = this.queryPackageInstance(upsertParam.getPackageInstanceId());
      DnsPackage dnsPackage = this.queryPackage(instance.getPackageId());
      this.validateZoneNameServers(dnsPackage);
      this.dispatchZoneCommand(dnsPackage, node -> this.sendUpsertZoneCommand(node, dnsPackage, instance, upsertParam));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteZone(DnsZoneUpsertParam deleteParam) {
      DnsPackageInstance instance = this.queryPackageInstance(deleteParam.getPackageInstanceId());
      DnsPackage dnsPackage = this.queryPackage(instance.getPackageId());
      this.dispatchZoneCommand(dnsPackage, node -> this.sendDeleteZoneCommand(node, deleteParam));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void upsertRecord(DnsRecordCommandParam recordParam) {
      this.dispatchRecordCommand(recordParam, "UPSERT_RECORD");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void updateRecord(DnsRecordCommandParam recordParam) {
      this.dispatchRecordCommand(recordParam, "UPDATE_RECORD");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void deleteRecord(DnsRecordCommandParam recordParam) {
      this.dispatchRecordCommand(recordParam, "DELETE_RECORD");
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void setRecordEnabled(DnsRecordCommandParam recordParam) {
      this.dispatchRecordCommand(recordParam, "SET_RECORD_ENABLED");
   }

   private DnsPackageInstance buildPackageInstance(
      DnsPackage dnsPackage, String memberId, Integer domainQuota, Integer periodMonths, Date startTime, String sourceOrderNo, String remark
   ) {
      Date actualStartTime = (Date)ObjectUtil.defaultIfNull(startTime, new Date());
      int actualPeriodMonths = (Integer)ObjectUtil.defaultIfNull(periodMonths, 1);
      DnsPackageInstance instance = new DnsPackageInstance();
      instance.setId(IdUtil.getSnowflakeNextIdStr());
      instance.setMemberId(memberId);
      instance.setPackageId(dnsPackage.getId());
      instance.setPackageName(dnsPackage.getName());
      instance.setLineGroupId(dnsPackage.getLineGroupId());
      instance.setSingleDomainPrice(dnsPackage.getSingleDomainPrice());
      instance.setMinTtl(dnsPackage.getMinTtl());
      instance.setDomainQuota((Integer)ObjectUtil.defaultIfNull(domainQuota, dnsPackage.getDefaultDomainQuota()));
      instance.setUsedQuota(0);
      instance.setPeriodMonths(actualPeriodMonths);
      instance.setStartTime(actualStartTime);
      instance.setNextDueTime(DateUtil.offsetMonth(actualStartTime, actualPeriodMonths));
      instance.setSourceOrderNo(sourceOrderNo);
      instance.setStatus(1);
      instance.setRemark(remark);
      return instance;
   }

   private void fillPackageInstanceDomains(List<DnsPackageInstance> instances) {
      if (!CollUtil.isEmpty(instances)) {
         List<String> instanceIds = CollStreamUtil.toList(instances, DnsPackageInstance::getId);
         Map<String, List<DnsBoundDomainVO>> domainMap = this.selectBoundDomainMap(instanceIds);
         instances.forEach(instance -> instance.setDomains(domainMap.getOrDefault(instance.getId(), Collections.emptyList())));
      }
   }

   private void fillPackageInstanceNameServers(List<DnsPackageInstance> instances) {
      if (!CollUtil.isEmpty(instances)) {
         List<String> packageIds = instances.stream()
            .map(DnsPackageInstance::getPackageId)
            .filter(CharSequenceUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
         if (!CollUtil.isEmpty(packageIds)) {
            Map<String, DnsPackage> packageMap = this.packageMapper
               .selectBatchIds(packageIds)
               .stream()
               .collect(Collectors.toMap(DnsPackage::getId, dnsPackage -> (DnsPackage)dnsPackage));
            instances.forEach(instance -> {
               DnsPackage dnsPackage = packageMap.get(instance.getPackageId());
               if (dnsPackage != null) {
                  instance.setPrimaryNs(dnsPackage.getPrimaryNs());
                  instance.setSecondaryNs(dnsPackage.getSecondaryNs());
               }
            });
         }
      }
   }

   private void fillPackageInstanceVoDomains(List<DnsPackageInstanceVO> instances) {
      if (!CollUtil.isEmpty(instances)) {
         List<String> instanceIds = CollStreamUtil.toList(instances, DnsPackageInstanceVO::getId);
         Map<String, List<DnsBoundDomainVO>> domainMap = this.selectBoundDomainMap(instanceIds);
         instances.forEach(instance -> instance.setDomains(domainMap.getOrDefault(instance.getId(), Collections.emptyList())));
      }
   }

   private void fillPackageInstanceVoNameServers(List<DnsPackageInstanceVO> instances) {
      if (!CollUtil.isEmpty(instances)) {
         List<String> packageIds = instances.stream()
            .<String>map(DnsPackageInstanceVO::getPackageId)
            .filter(CharSequenceUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
         if (!CollUtil.isEmpty(packageIds)) {
            Map<String, DnsPackage> packageMap = this.packageMapper
               .selectBatchIds(packageIds)
               .stream()
               .collect(Collectors.toMap(DnsPackage::getId, dnsPackage -> (DnsPackage)dnsPackage));
            instances.forEach(instance -> {
               DnsPackage dnsPackage = packageMap.get(instance.getPackageId());
               if (dnsPackage != null) {
                  instance.setPrimaryNs(dnsPackage.getPrimaryNs());
                  instance.setSecondaryNs(dnsPackage.getSecondaryNs());
               }
            });
         }
      }
   }

   private Map<String, List<DnsBoundDomainVO>> selectBoundDomainMap(List<String> instanceIds) {
      if (CollUtil.isEmpty(instanceIds)) {
         return Collections.emptyMap();
      } else {
         List<DnsBoundDomainVO> domains = this.packageInstanceMapper.selectBoundDomainsByInstanceIds(instanceIds);
         return CollUtil.isEmpty(domains) ? Collections.emptyMap() : domains.stream().collect(Collectors.groupingBy(DnsBoundDomainVO::getPackageInstanceId));
      }
   }

   private void validateInstanceMember(DnsPackageInstance instance, String memberId) {
      if (!Objects.equals(instance.getMemberId(), memberId)) {
         throw new CommonException("DNS套餐实例不属于该会员", new Object[0]);
      }
   }

   private void enableDomainsAfterRenew(DnsPackageInstance instance) {
      List<DnsBoundDomainVO> domains = this.packageInstanceMapper.selectBoundDomainsByInstanceIds(List.of(instance.getId()));
      if (!CollUtil.isEmpty(domains)) {
         this.packageInstanceMapper.enableDomainsByInstanceId(instance.getId());

         for (DnsBoundDomainVO domain : domains) {
            try {
               this.upsertZone(this.buildZoneUpsertParam(domain, true));
            } catch (Exception var6) {
               log.warn("DNS套餐续费恢复域名任务创建失败，instanceId={}, domainId={}, error={}", new Object[]{instance.getId(), domain.getId(), var6.getMessage()});
            }
         }
      }
   }

   private DnsZoneUpsertParam buildZoneUpsertParam(DnsBoundDomainVO domain, boolean enabled) {
      DnsZoneUpsertParam param = new DnsZoneUpsertParam();
      param.setZoneId(domain.getId());
      param.setPackageInstanceId(domain.getPackageInstanceId());
      param.setDomainName(domain.getDomainName());
      param.setAdminEmail(domain.getEmail());
      param.setEnabled(enabled);
      return param;
   }

   private void validateMemberExists(String memberId) {
      if (StrUtil.isBlank(memberId)) {
         throw new CommonException("会员ID不能为空", new Object[0]);
      } else {
         try {
            BaseMember member = this.memberApi.detail(Long.valueOf(memberId));
            if (member == null) {
               throw new CommonException("会员不存在：{}", new Object[]{memberId});
            }
         } catch (NumberFormatException var3) {
            throw new CommonException("会员ID格式不正确：{}", new Object[]{memberId});
         }
      }
   }

   private void replaceLineGroupNodes(String groupId, List<String> nodeIds) {
      this.lineGroupNodeMapper.physicalDeleteByGroupId(groupId);
      if (!CollUtil.isEmpty(nodeIds)) {
         List<DnsLineGroupNode> relations = nodeIds.stream().filter(CharSequenceUtil::isNotBlank).distinct().map(nodeId -> {
            this.queryNode(nodeId);
            DnsLineGroupNode relation = new DnsLineGroupNode();
            relation.setId(IdUtil.getSnowflakeNextIdStr());
            relation.setGroupId(groupId);
            relation.setNodeId(nodeId);
            return relation;
         }).collect(Collectors.toList());
         relations.forEach(this.lineGroupNodeMapper::insert);
      }
   }

   private void fillLatestCallbackInfo(List<DnsNodeTask> tasks) {
      if (!CollUtil.isEmpty(tasks)) {
         for (DnsNodeTask task : tasks) {
            DnsNodeCallbackLog callbackLog = (DnsNodeCallbackLog)this.nodeCallbackLogMapper
               .selectOne(
                  (Wrapper<DnsNodeCallbackLog>)((LambdaQueryWrapper<DnsNodeCallbackLog>)((LambdaQueryWrapper<DnsNodeCallbackLog>)((LambdaQueryWrapper<DnsNodeCallbackLog>)((LambdaQueryWrapper<DnsNodeCallbackLog>)new LambdaQueryWrapper<DnsNodeCallbackLog>()
                                 .eq(DnsNodeCallbackLog::getTaskId, task.getId()))
                              .or())
                           .eq(DnsNodeCallbackLog::getRequestId, task.getRequestId()))
                        .orderByDesc(DnsNodeCallbackLog::getCallbackTime))
                     .last("LIMIT 1")
               );
            if (callbackLog != null) {
               task.setCallbackEventId(callbackLog.getEventId());
               task.setCallbackEventType(callbackLog.getEventType());
               task.setCallbackTime(callbackLog.getCallbackTime());
               task.setCallbackProcessStatus(callbackLog.getProcessStatus());
               task.setCallbackErrorMessage(callbackLog.getErrorMessage());
               task.setCallbackRequestBody(callbackLog.getRequestBody());
            }
         }
      }
   }

   private void validateZoneNameServers(DnsPackage dnsPackage) {
      if (StrUtil.hasBlank(new CharSequence[]{dnsPackage.getPrimaryNs(), dnsPackage.getSecondaryNs()})) {
         throw new CommonException("DNS套餐未配置主备NS：{}", new Object[]{dnsPackage.getId()});
      }
   }

   private List<DnsNode> selectActiveNodesByLineGroup(String lineGroupId) {
      List<String> nodeIds = this.lineGroupNodeMapper
         .selectList((Wrapper<DnsLineGroupNode>)new LambdaQueryWrapper<DnsLineGroupNode>().eq(DnsLineGroupNode::getGroupId, lineGroupId))
         .stream()
         .map(DnsLineGroupNode::getNodeId)
         .collect(Collectors.toList());
      return CollUtil.isEmpty(nodeIds)
         ? Collections.emptyList()
         : this.nodeMapper
            .selectList(
               (Wrapper<DnsNode>)((LambdaQueryWrapper<DnsNode>)((LambdaQueryWrapper<DnsNode>)new LambdaQueryWrapper<DnsNode>().in(DnsNode::getId, nodeIds)).eq(DnsNode::getStatus, 1))
                  .eq(DnsNode::getInstallStatus, 1)
            );
   }

   private void dispatchZoneCommand(DnsPackage dnsPackage, Consumer<DnsNode> sender) {
      List<DnsNode> nodes = this.selectActiveNodesByLineGroup(dnsPackage.getLineGroupId());
      if (!CollUtil.isEmpty(nodes)) {
         nodes.forEach(sender);
      }
   }

   private void sendUpsertZoneCommand(DnsNode node, DnsPackage dnsPackage, DnsPackageInstance instance, DnsZoneUpsertParam upsertParam) {
      String commandId = "zone-" + upsertParam.getZoneId() + "-" + node.getId() + "-" + IdUtil.getSnowflakeNextIdStr();
      JSONObject requestBody = this.buildUpsertZoneCommand(commandId, dnsPackage, instance, upsertParam);
      this.sendNodeCommand(node, "DOMAIN", upsertParam.getZoneId(), commandId, "UPSERT_ZONE", requestBody);
   }

   private void sendDeleteZoneCommand(DnsNode node, DnsZoneUpsertParam deleteParam) {
      String commandId = "zone-delete-" + deleteParam.getZoneId() + "-" + node.getId() + "-" + IdUtil.getSnowflakeNextIdStr();
      JSONObject requestBody = this.buildDeleteZoneCommand(commandId, deleteParam);
      this.sendNodeCommand(node, "DOMAIN", deleteParam.getZoneId(), commandId, "DELETE_ZONE", requestBody);
   }

   private void dispatchRecordCommand(DnsRecordCommandParam recordParam, String action) {
      DnsPackageInstance instance = this.queryPackageInstance(recordParam.getPackageInstanceId());
      DnsPackage dnsPackage = this.queryPackage(instance.getPackageId());
      this.dispatchZoneCommand(dnsPackage, node -> this.sendRecordCommand(node, recordParam, action));
   }

   private void sendRecordCommand(DnsNode node, DnsRecordCommandParam recordParam, String action) {
      String commandId = "record-" + recordParam.getRecordId() + "-" + node.getId() + "-" + IdUtil.getSnowflakeNextIdStr();
      JSONObject requestBody = this.buildRecordCommand(commandId, recordParam, action);
      this.sendNodeCommand(node, "RECORD", recordParam.getRecordId(), commandId, action, requestBody);
   }

   private void sendReplaceConfigCommand(DnsNode node) {
      String commandId = "replace-config-" + node.getId() + "-" + IdUtil.getSnowflakeNextIdStr();
      JSONObject requestBody = this.buildReplaceConfigCommand(commandId, node.getId());
      this.sendNodeCommand(node, "CONFIG", node.getId(), commandId, "REPLACE_CONFIG", requestBody);
   }

   private void sendNodeCommand(DnsNode node, String bizType, String bizId, String commandId, String action, JSONObject requestBody) {
      DnsNodeTask task = this.buildNodeTask(node, bizType, bizId, commandId, action, requestBody);
      this.nodeTaskMapper.insert(task);
   }

   private JSONObject buildUpsertZoneCommand(String commandId, DnsPackage dnsPackage, DnsPackageInstance instance, DnsZoneUpsertParam upsertParam) {
      JSONObject zone = new JSONObject();
      zone.put("zoneId", upsertParam.getZoneId());
      zone.put("enabled", ObjectUtil.defaultIfNull(upsertParam.getEnabled(), true));
      zone.put("name", upsertParam.getDomainName());
      JSONArray nameServers = new JSONArray();
      nameServers.add(dnsPackage.getPrimaryNs());
      nameServers.add(dnsPackage.getSecondaryNs());
      zone.put("nameServers", nameServers);
      zone.put("adminEmail", upsertParam.getAdminEmail());
      zone.put("soaTtl", ObjectUtil.defaultIfNull(instance.getMinTtl(), 300));
      zone.put("negativeTtl", 60);
      JSONObject payload = new JSONObject();
      payload.put("zone", zone);
      JSONObject command = new JSONObject();
      command.put("commandId", commandId);
      command.put("type", "UPSERT_ZONE");
      command.put("payload", payload);
      return command;
   }

   private JSONObject buildDeleteZoneCommand(String commandId, DnsZoneUpsertParam deleteParam) {
      JSONObject payload = new JSONObject();
      payload.put("zoneId", deleteParam.getZoneId());
      JSONObject command = new JSONObject();
      command.put("commandId", commandId);
      command.put("type", "DELETE_ZONE");
      command.put("payload", payload);
      return command;
   }

   private JSONObject buildRecordCommand(String commandId, DnsRecordCommandParam recordParam, String action) {
      JSONObject payload = new JSONObject();
      if ("UPSERT_RECORD".equals(action)) {
         payload.put("record", this.buildRecordPayload(recordParam, true));
      } else if ("UPDATE_RECORD".equals(action)) {
         payload.put("recordId", recordParam.getRecordId());
         payload.put("record", this.buildRecordPayload(recordParam, false));
      } else if ("DELETE_RECORD".equals(action)) {
         payload.put("recordId", recordParam.getRecordId());
      } else if ("SET_RECORD_ENABLED".equals(action)) {
         payload.put("recordId", recordParam.getRecordId());
         payload.put("enabled", ObjectUtil.defaultIfNull(recordParam.getEnabled(), true));
      }

      JSONObject command = new JSONObject();
      command.put("commandId", commandId);
      command.put("type", action);
      command.put("payload", payload);
      return command;
   }

   private JSONObject buildReplaceConfigCommand(String commandId, String nodeId) {
      List<DnsNodeReplaceConfigZone> zones = this.packageInstanceMapper.selectReplaceConfigZonesByNodeId(nodeId);
      List<String> zoneIds = CollStreamUtil.toList(zones, DnsNodeReplaceConfigZone::getZoneId)
         .stream()
         .filter(CharSequenceUtil::isNotBlank)
         .distinct()
         .collect(Collectors.toList());
      List<DnsNodeReplaceConfigRecord> records = CollUtil.isEmpty(zoneIds)
         ? Collections.emptyList()
         : this.packageInstanceMapper.selectReplaceConfigRecordsByZoneIds(zoneIds);
      JSONObject payload = new JSONObject();
      payload.put("zones", this.buildReplaceConfigZoneArray(zones));
      payload.put("records", this.buildReplaceConfigRecordArray(records));
      JSONObject command = new JSONObject();
      command.put("commandId", commandId);
      command.put("type", "REPLACE_CONFIG");
      command.put("payload", payload);
      return command;
   }

   private JSONArray buildReplaceConfigZoneArray(List<DnsNodeReplaceConfigZone> zones) {
      JSONArray zoneArray = new JSONArray();
      if (CollUtil.isEmpty(zones)) {
         return zoneArray;
      } else {
         zones.forEach(zone -> {
            JSONObject zoneJson = new JSONObject();
            zoneJson.put("zoneId", zone.getZoneId());
            zoneJson.put("name", zone.getName());
            JSONArray nameServers = new JSONArray();
            if (StrUtil.isNotBlank(zone.getPrimaryNs())) {
               nameServers.add(zone.getPrimaryNs());
            }

            if (StrUtil.isNotBlank(zone.getSecondaryNs())) {
               nameServers.add(zone.getSecondaryNs());
            }

            zoneJson.put("nameServers", nameServers);
            zoneJson.put("adminEmail", zone.getAdminEmail());
            zoneJson.put("soaTtl", ObjectUtil.defaultIfNull(zone.getSoaTtl(), 300));
            zoneJson.put("negativeTtl", ObjectUtil.defaultIfNull(zone.getNegativeTtl(), 60));
            zoneArray.add(zoneJson);
         });
         return zoneArray;
      }
   }

   private JSONArray buildReplaceConfigRecordArray(List<DnsNodeReplaceConfigRecord> records) {
      JSONArray recordArray = new JSONArray();
      if (CollUtil.isEmpty(records)) {
         return recordArray;
      } else {
         records.forEach(record -> {
            JSONObject recordJson = new JSONObject();
            recordJson.put("recordId", record.getRecordId());
            recordJson.put("name", record.getName());
            recordJson.put("type", record.getType());
            recordJson.put("value", record.getValue());
            recordJson.put("ttl", ObjectUtil.defaultIfNull(record.getTtl(), 60));
            recordJson.put("line", record.getLine());
            recordJson.put("province", record.getProvince());
            recordJson.put("enabled", ObjectUtil.defaultIfNull(record.getEnabled(), true));
            if (record.getPriority() != null) {
               recordJson.put("priority", record.getPriority());
            }

            if (StrUtil.isNotBlank(record.getCountry())) {
               recordJson.put("country", record.getCountry());
            }

            if (StrUtil.isNotBlank(record.getRegion())) {
               recordJson.put("region", record.getRegion());
            }

            if (StrUtil.isNotBlank(record.getCity())) {
               recordJson.put("city", record.getCity());
            }

            if (StrUtil.isNotBlank(record.getIsp())) {
               recordJson.put("isp", record.getIsp());
            }

            recordArray.add(recordJson);
         });
         return recordArray;
      }
   }

   private JSONObject buildRecordPayload(DnsRecordCommandParam recordParam, boolean includeRecordId) {
      JSONObject record = new JSONObject();
      if (includeRecordId) {
         record.put("recordId", recordParam.getRecordId());
      }

      record.put("name", recordParam.getName());
      record.put("type", recordParam.getType());
      record.put("value", recordParam.getValue());
      record.put("ttl", ObjectUtil.defaultIfNull(recordParam.getTtl(), 60));
      record.put("line", recordParam.getLine());
      record.put("province", recordParam.getProvince());
      if (recordParam.getPriority() != null) {
         record.put("priority", recordParam.getPriority());
      }

      if (StrUtil.isNotBlank(recordParam.getCountry())) {
         record.put("country", recordParam.getCountry());
      }

      if (StrUtil.isNotBlank(recordParam.getRegion())) {
         record.put("region", recordParam.getRegion());
      }

      if (StrUtil.isNotBlank(recordParam.getCity())) {
         record.put("city", recordParam.getCity());
      }

      if (StrUtil.isNotBlank(recordParam.getIsp())) {
         record.put("isp", recordParam.getIsp());
      }

      return record;
   }

   private DnsNodeTask buildNodeTask(DnsNode node, String bizType, String bizId, String commandId, String action, JSONObject requestBody) {
      DnsNodeTask task = new DnsNodeTask();
      task.setId(IdUtil.getSnowflakeNextIdStr());
      task.setRequestId(commandId);
      task.setNodeId(node.getId());
      task.setNodeTaskId(commandId);
      task.setBizType(bizType);
      task.setBizId(bizId);
      task.setAction(action);
      task.setRequestMethod("POST");
      task.setRequestPath("/api/v1/commands");
      task.setRequestBody(requestBody.toJSONString());
      task.setTaskStatus(1);
      task.setRetryCount(0);
      task.setMaxRetry(3);
      return task;
   }

   private void handleNodeInstalled(DnsNode node, DnsCallbackParam callbackParam) {
      if (!"SUCCEEDED".equals(callbackParam.getStatus())) {
         throw new CommonException("DNS节点安装失败：{}", new Object[]{callbackParam.getErrorMessage()});
      } else {
         node.setInstallStatus(1);
         node.setInstallTime(this.parseCallbackTime(callbackParam.getTimestamp()));
         node.setStatus(1);
         String apiUrl = this.buildNodeApiUrl(node.getApiUrl(), callbackParam.getApiAddress());
         if (StrUtil.isNotBlank(apiUrl)) {
            node.setApiUrl(apiUrl);
         }

         this.nodeMapper.updateById(node);
         this.sendReplaceConfigCommand(node);
      }
   }

   private String buildNodeApiUrl(String currentApiUrl, String apiAddress) {
      if (StrUtil.isBlank(apiAddress)) {
         return null;
      } else {
         String address = StrUtil.trim(apiAddress);
         if (StrUtil.startWithIgnoreCase(address, "http://") || StrUtil.startWithIgnoreCase(address, "https://")) {
            return StrUtil.removeSuffix(address, "/");
         } else if (StrUtil.contains(address, ":") && !StrUtil.startWith(address, ":")) {
            return "http://" + StrUtil.removeSuffix(address, "/");
         } else {
            String host = this.extractApiHost(currentApiUrl);
            if (StrUtil.isBlank(host)) {
               log.warn("DNS节点安装回调无法回写API地址，当前apiUrl缺少主机：{}", currentApiUrl);
               return null;
            } else {
               String port = StrUtil.removePrefix(address, ":");
               return StrUtil.isBlank(port) ? null : "http://" + host + ":" + port;
            }
         }
      }
   }

   private String extractApiHost(String apiUrl) {
      if (StrUtil.isBlank(apiUrl)) {
         return null;
      } else {
         String value = StrUtil.trim(apiUrl);

         try {
            String withScheme = value;
            if (!StrUtil.startWithIgnoreCase(value, "http://") && !StrUtil.startWithIgnoreCase(value, "https://")) {
               withScheme = "http://" + value;
            }

            String host = URI.create(withScheme).getHost();
            if (StrUtil.isNotBlank(host)) {
               return host;
            }
         } catch (Exception var5) {
         }

         String hostPart = StrUtil.subBefore(StrUtil.removePrefix(StrUtil.removePrefix(value, "http://"), "https://"), "/", false);
         return StrUtil.subBefore(hostPart, ":", false);
      }
   }

   private DnsNodeTask handleTaskResult(DnsNode node, DnsCallbackParam callbackParam, String requestBody) {
      DnsNodeTask task = this.resolveNodeTask(callbackParam);
      if (task == null) {
         throw new CommonException("未匹配到DNS节点任务：{}", new Object[]{callbackParam.getEventId()});
      } else {
         boolean succeeded = "SUCCEEDED".equals(callbackParam.getStatus());
         task.setTaskStatus(succeeded ? 3 : -1);
         task.setEndTime(this.parseCallbackTime(this.firstNonBlank(callbackParam.getFinishedAt(), callbackParam.getTimestamp())));
         task.setResponseBody(requestBody);
         task.setErrorMessage(succeeded ? null : this.resolveCallbackErrorMessage(callbackParam));
         this.nodeTaskMapper.updateById(task);
         if (succeeded && callbackParam.getVersion() != null) {
            node.setVersion(String.valueOf(callbackParam.getVersion()));
            this.nodeMapper.updateById(node);
         }

         return task;
      }
   }

   private DnsNodeTask resolveNodeTask(DnsCallbackParam callbackParam) {
      if (StrUtil.isNotBlank(callbackParam.getTaskId())) {
         DnsNodeTask task = (DnsNodeTask)this.nodeTaskMapper.selectById(callbackParam.getTaskId());
         if (task != null) {
            return task;
         }
      }

      LambdaQueryWrapper<DnsNodeTask> wrapper = (LambdaQueryWrapper<DnsNodeTask>)new LambdaQueryWrapper<DnsNodeTask>().eq(DnsNodeTask::getNodeId, callbackParam.getNodeId());
      if (StrUtil.isNotBlank(callbackParam.getRequestId())) {
         wrapper.eq(DnsNodeTask::getRequestId, callbackParam.getRequestId());
      } else {
         String nodeTaskId = this.resolveCallbackNodeTaskId(callbackParam);
         if (StrUtil.isBlank(nodeTaskId)) {
            return null;
         }

         wrapper.eq(DnsNodeTask::getNodeTaskId, nodeTaskId);
      }

      return (DnsNodeTask)this.nodeTaskMapper.selectOne((Wrapper)wrapper.last("LIMIT 1"));
   }

   private String resolveCallbackNodeTaskId(DnsCallbackParam callbackParam) {
      return this.firstNonBlank(callbackParam.getNodeTaskId(), callbackParam.getCommandId(), callbackParam.getTaskId());
   }

   private String resolveCallbackErrorMessage(DnsCallbackParam callbackParam) {
      if (callbackParam.getError() != null) {
         String message = callbackParam.getError().getString("message");
         if (StrUtil.isNotBlank(message)) {
            return message;
         }

         String code = callbackParam.getError().getString("code");
         if (StrUtil.isNotBlank(code)) {
            return code;
         }
      }

      return StrUtil.blankToDefault(callbackParam.getErrorMessage(), "DNS节点任务执行失败");
   }

   private String firstNonBlank(String... values) {
      if (values == null) {
         return null;
      } else {
         for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
               return value;
            }
         }

         return null;
      }
   }

   private DnsNodeCallbackLog buildCallbackLog(DnsCallbackParam callbackParam, Map<String, String> requestHeaders, String requestBody) {
      DnsNodeCallbackLog log = new DnsNodeCallbackLog();
      log.setId(IdUtil.getSnowflakeNextIdStr());
      log.setRequestId(callbackParam.getRequestId());
      log.setNodeId(callbackParam.getNodeId());
      log.setNodeTaskId(this.resolveCallbackNodeTaskId(callbackParam));
      log.setEventId(callbackParam.getEventId());
      log.setEventType(callbackParam.getEventType());
      log.setAction(callbackParam.getAction());
      log.setCallbackTime(this.parseCallbackTime(this.firstNonBlank(callbackParam.getFinishedAt(), callbackParam.getTimestamp())));
      log.setRequestHeaders(JSON.toJSONString(requestHeaders));
      log.setRequestBody(requestBody);
      log.setSignStatus(1);
      log.setProcessStatus(0);
      return log;
   }

   private boolean shouldReprocessInstallCallback(DnsCallbackParam callbackParam, DnsNodeCallbackLog callbackLog, String requestBody) {
      return !"NODE_INSTALLED".equals(callbackParam.getEventType())
         ? false
         : !StrUtil.equals(StrUtil.nullToEmpty(callbackLog.getRequestBody()), StrUtil.nullToEmpty(requestBody));
   }

   private void refreshCallbackLog(DnsNodeCallbackLog log, DnsCallbackParam callbackParam, Map<String, String> requestHeaders, String requestBody) {
      log.setRequestId(callbackParam.getRequestId());
      log.setNodeId(callbackParam.getNodeId());
      log.setNodeTaskId(this.resolveCallbackNodeTaskId(callbackParam));
      log.setEventType(callbackParam.getEventType());
      log.setAction(callbackParam.getAction());
      log.setCallbackTime(this.parseCallbackTime(this.firstNonBlank(callbackParam.getFinishedAt(), callbackParam.getTimestamp())));
      log.setRequestHeaders(JSON.toJSONString(requestHeaders));
      log.setRequestBody(requestBody);
      log.setSignStatus(1);
      log.setProcessStatus(0);
   }

   private void saveCallbackLog(DnsNodeCallbackLog callbackLog) {
      if (this.nodeCallbackLogMapper.selectById(callbackLog.getId()) == null) {
         this.nodeCallbackLogMapper.insert(callbackLog);
      } else {
         this.nodeCallbackLogMapper.updateById(callbackLog);
      }
   }

   private String resolveBearerToken(String authorization) {
      if (StrUtil.isBlank(authorization)) {
         return null;
      } else {
         String prefix = "Bearer ";
         return StrUtil.startWithIgnoreCase(authorization, prefix) ? StrUtil.trim(authorization.substring(prefix.length())) : StrUtil.trim(authorization);
      }
   }

   private Date parseCallbackTime(String timestamp) {
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

   private DnsLineGroup fillLineGroupNodes(DnsLineGroup group) {
      List<String> nodeIds = this.lineGroupNodeMapper
         .selectList((Wrapper<DnsLineGroupNode>)new LambdaQueryWrapper<DnsLineGroupNode>().eq(DnsLineGroupNode::getGroupId, group.getId()))
         .stream()
         .map(DnsLineGroupNode::getNodeId)
         .collect(Collectors.toList());
      group.setNodeIds(nodeIds);
      return group;
   }

   private DnsNode maskNode(DnsNode node) {
      node.setApiTokenMasked("****");
      node.setCallbackTokenMasked("****");
      return node;
   }

   private DnsNode queryNode(String id) {
      DnsNode node = (DnsNode)this.nodeMapper.selectById(id);
      if (node == null) {
         throw new CommonException("DNS节点不存在：{}", new Object[]{id});
      } else {
         return node;
      }
   }

   private DnsLineGroup queryLineGroup(String id) {
      DnsLineGroup group = (DnsLineGroup)this.lineGroupMapper.selectById(id);
      if (group == null) {
         throw new CommonException("DNS线路分组不存在：{}", new Object[]{id});
      } else {
         return group;
      }
   }

   private DnsPackage queryPackage(String id) {
      DnsPackage dnsPackage = (DnsPackage)this.packageMapper.selectById(id);
      if (dnsPackage == null) {
         throw new CommonException("DNS套餐不存在：{}", new Object[]{id});
      } else {
         return dnsPackage;
      }
   }

   private DnsPackageInstance queryPackageInstance(String id) {
      DnsPackageInstance instance = (DnsPackageInstance)this.packageInstanceMapper.selectById(id);
      if (instance == null) {
         throw new CommonException("DNS套餐实例不存在：{}", new Object[]{id});
      } else {
         return instance;
      }
   }

   private void validateActiveLineGroup(String lineGroupId) {
      DnsLineGroup group = this.queryLineGroup(lineGroupId);
      if (!Objects.equals(group.getStatus(), 1)) {
         throw new CommonException("DNS线路分组未启用：{}", new Object[]{lineGroupId});
      }
   }

   private void validateHttpUrl(String url, String fieldName) {
      try {
         URI uri = URI.create(StrUtil.trim(url));
         String scheme = uri.getScheme();
         if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException();
         } else {
            uri.toURL();
         }
      } catch (Exception var5) {
         throw new CommonException("{}格式不正确：{}", new Object[]{fieldName, url});
      }
   }

   private void validateNodeApiAddress(String address) {
      String value = StrUtil.trim(address);

      try {
         if (!StrUtil.startWithIgnoreCase(value, "http://") && !StrUtil.startWithIgnoreCase(value, "https://")) {
            if (StrUtil.containsAny(value, new CharSequence[]{" ", "\t", "\r", "\n", "/", "?", "#"})) {
               throw new IllegalArgumentException();
            } else {
               URI uri = URI.create("//" + value);
               if (StrUtil.isBlank(uri.getHost()) || uri.getUserInfo() != null || uri.getPort() > 65535) {
                  throw new IllegalArgumentException();
               }
            }
         } else {
            this.validateHttpUrl(value, "节点地址");
         }
      } catch (Exception var4) {
         throw new CommonException("节点地址格式不正确：{}", new Object[]{address});
      }
   }

   private void validateToken(String token) {
      if (StrUtil.length(token) < 16) {
         throw new CommonException("Token至少16位", new Object[0]);
      }
   }

   private String shellQuote(String value) {
      return "'" + StrUtil.nullToEmpty(value).replace("'", "'\"'\"'") + "'";
   }

   private String generateUniqueApiToken(String excludeId) {
      for (int i = 0; i < 5; i++) {
         String token = DnsTokenUtil.generateToken();
         if (this.isApiTokenUnique(DnsTokenUtil.encryptApiToken(token), excludeId)) {
            return token;
         }
      }

      throw new CommonException("生成节点API Token失败，请重试", new Object[0]);
   }

   private void validateUniqueApiToken(String encryptedToken, String excludeId) {
      if (!this.isApiTokenUnique(encryptedToken, excludeId)) {
         throw new CommonException("节点API Token已存在", new Object[0]);
      }
   }

   private boolean isApiTokenUnique(String encryptedToken, String excludeId) {
      Long count = this.nodeMapper
         .selectCount(
            ((LambdaQueryWrapper<DnsNode>)new LambdaQueryWrapper<DnsNode>().eq(DnsNode::getApiToken, encryptedToken))
               .ne(StrUtil.isNotBlank(excludeId), DnsNode::getId, excludeId)
         );
      return count == null || count == 0L;
   }

   private int defaultStatus(Integer status) {
      return (Integer)ObjectUtil.defaultIfNull(status, 1);
   }

   private <T> QueryWrapper<T> baseWrapper(DnsAdminParam.BasePageParam pageParam, SFunction<T, ?> defaultSort) {
      QueryWrapper<T> wrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         wrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         wrapper.lambda().orderByDesc(defaultSort);
      }

      return wrapper;
   }
}
