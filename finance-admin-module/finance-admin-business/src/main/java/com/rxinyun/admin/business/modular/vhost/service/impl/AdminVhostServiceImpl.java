package com.rxinyun.admin.business.modular.vhost.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.vhost.entity.AdminVhost;
import com.rxinyun.admin.business.modular.vhost.mapper.AdminVhostMapper;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostDomainParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostIdParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostPageParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostPasswordParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostPhpParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostRemarkParam;
import com.rxinyun.admin.business.modular.vhost.service.AdminVhostService;
import com.rxinyun.admin.business.modular.vhost.task.mapper.VhostAutoTaskMapper;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.vhost.VhostOrderOptionSupport;
import com.rxinyun.common.vhost.VhostPanelAdapter;
import com.rxinyun.common.vhost.VhostPanelRouter;
import com.rxinyun.common.vhost.param.VhostPasswordCommand;
import com.rxinyun.common.vhost.param.VhostPowerCommand;
import com.rxinyun.common.vhost.result.VhostPhpVersion;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AdminVhostServiceImpl extends ServiceImpl<AdminVhostMapper, AdminVhost> implements AdminVhostService {
   @Resource
   private VhostAutoTaskMapper vhostAutoTaskMapper;
   @Resource
   private VhostPanelRouter vhostPanelRouter;

   @Override
   public Page<AdminVhost> page(AdminVhostPageParam pageParam) {
      QueryWrapper<AdminVhost> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (pageParam != null && pageParam.getMemberNo() != null) {
         queryWrapper.lambda().eq(AdminVhost::getMemberNo, String.valueOf(pageParam.getMemberNo()));
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getOrderNo())) {
         queryWrapper.lambda().eq(AdminVhost::getOrderNo, pageParam.getOrderNo());
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getMasterId())) {
         queryWrapper.lambda().eq(AdminVhost::getMasterId, pageParam.getMasterId());
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getHostStatus())) {
         queryWrapper.lambda().eq(AdminVhost::getHostStatus, pageParam.getHostStatus());
      }

      if (pageParam != null && pageParam.getBusinessStatus() != null) {
         queryWrapper.lambda().eq(AdminVhost::getBusinessStatus, pageParam.getBusinessStatus());
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getHostName())) {
         queryWrapper.lambda().like(AdminVhost::getHostName, pageParam.getHostName());
      } else if (pageParam != null && StrUtil.isNotBlank(pageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<AdminVhost>)((LambdaQueryWrapper<AdminVhost>)((LambdaQueryWrapper<AdminVhost>)((LambdaQueryWrapper<AdminVhost>)q.like(
                              AdminVhost::getHostName, pageParam.getSearchKey()
                           ))
                           .or())
                        .like(AdminVhost::getOrderNo, pageParam.getSearchKey()))
                     .or())
                  .like(AdminVhost::getMemberNo, pageParam.getSearchKey())
            );
      }

      if (pageParam != null && ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), this.resolveSortField(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<AdminVhost> page = (Page<AdminVhost>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillPackage(page.getRecords());
      this.fillMasterNames(page.getRecords());
      return page;
   }

   @Override
   public AdminVhost detail(AdminVhostIdParam idParam) {
      AdminVhost host = this.requireHost(idParam);
      this.fillPackage(host);
      this.fillMasterNames(List.of(host));
      return host;
   }

   @Override
   public CommonResult<String> start(AdminVhostIdParam idParam) {
      return this.submitPower(idParam, "START", ServerStatusEnum.STOP.getCode(), "启动");
   }

   @Override
   public CommonResult<String> stop(AdminVhostIdParam idParam) {
      return this.submitPower(idParam, "STOP", ServerStatusEnum.RUNNING.getCode(), "停止");
   }

   private CommonResult<String> submitPower(AdminVhostIdParam idParam, String taskType, String requiredStatus, String label) {
      AdminVhost host = this.requireHost(idParam);
      if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         return CommonResult.error("虚拟主机已删除");
      } else if (BusinessStatusEnum.EXPIRED.getCode().equals(host.getBusinessStatus())) {
         return CommonResult.error("虚拟主机已到期");
      } else if (StrUtil.isBlank(host.getPanelHostId()) || host.getBindDomains() == null || host.getBindDomains().isEmpty()) {
         return CommonResult.error("网站尚未开通完成");
      } else if (!requiredStatus.equals(host.getHostStatus())) {
         return CommonResult.error(ServerStatusEnum.STOP.getCode().equals(requiredStatus) ? "仅已停止的网站可以启动" : "仅运行中的网站可以停止");
      } else {
         int inserted = this.vhostAutoTaskMapper
            .insertOperateTask(
               IdWorker.getIdStr(), host.getId(), host.getMasterId(), taskType, label + "虚拟主机 " + StrUtil.blankToDefault(host.getHostName(), host.getId()), "B"
            );
         return inserted < 1 ? CommonResult.error("存在未完成的任务，请稍后重试") : CommonResult.ok(label + "任务已提交");
      }
   }

   @Override
   public List<VhostPhpVersion> phpVersions(AdminVhostIdParam idParam) {
      AdminVhost host = this.requireHost(idParam);
      this.fillPackage(host);
      Map<String, Object> master = this.requireMaster(host);
      List<VhostPhpVersion> versions = this.adapter(master).listPhpVersions(this.text(master, "panelUrl"), this.text(master, "apiKey"));
      this.markPhpVersions(versions, host);
      return versions;
   }

   @Override
   public CommonResult<String> changePhp(AdminVhostPhpParam param) {
      AdminVhost host = this.requireHost(param);
      if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         return CommonResult.error("虚拟主机已删除");
      } else {
         String domain = this.firstDomain(host);
         if (!StrUtil.isBlank(host.getPanelHostId()) && !StrUtil.isBlank(domain)) {
            String phpVersion = VhostOrderOptionSupport.requirePhpVersion(param.getPhpVersion(), this.packagePhpVersions(host.getVersionId()));
            Map<String, Object> master = this.requireMaster(host);
            VhostPowerCommand command = new VhostPowerCommand();
            command.setPanelUrl(this.text(master, "panelUrl"));
            command.setApiKey(this.text(master, "apiKey"));
            command.setDomain(domain);
            command.setPhpVersion(phpVersion);
            String error = this.adapter(master).setPhpVersion(command);
            if (StrUtil.isNotBlank(error)) {
               return CommonResult.error(error);
            } else {
               host.setPhpVersion(phpVersion);
               this.updateById(host);
               return CommonResult.ok("PHP版本已切换");
            }
         } else {
            return CommonResult.error("网站尚未开通完成");
         }
      }
   }

   @Override
   public CommonResult<String> addDomain(AdminVhostDomainParam param) {
      AdminVhost host = this.requireHost(param);
      if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         return CommonResult.error("虚拟主机已删除");
      } else {
         String primary = this.firstDomain(host);
         if (!StrUtil.isBlank(host.getPanelHostId()) && !StrUtil.isBlank(primary)) {
            String domain = VhostOrderOptionSupport.normalizeDomain(param.getDomain());
            if (VhostOrderOptionSupport.containsDomain(host.getBindDomains(), domain)) {
               return CommonResult.error("该域名已绑定");
            } else if (this.domainUsedOnMaster(host.getMasterId(), domain, host.getId())) {
               return CommonResult.error("同一主控下该域名已被其他虚拟主机使用");
            } else {
               Map<String, Object> master = this.requireMaster(host);
               VhostPowerCommand command = new VhostPowerCommand();
               command.setPanelUrl(this.text(master, "panelUrl"));
               command.setApiKey(this.text(master, "apiKey"));
               command.setPanelHostId(host.getPanelHostId());
               command.setDomain(primary);
               command.setBindDomain(domain);
               this.adapter(master).addDomain(command);
               host.setBindDomains(VhostOrderOptionSupport.mergeBindDomains(host.getBindDomains(), null, domain));
               this.updateById(host);
               return CommonResult.ok("域名已添加");
            }
         } else {
            return CommonResult.error("网站尚未开通完成");
         }
      }
   }

   @Override
   public CommonResult<String> deleteDomain(AdminVhostDomainParam param) {
      AdminVhost host = this.requireHost(param);
      if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         return CommonResult.error("虚拟主机已删除");
      } else {
         String primary = this.firstDomain(host);
         if (!StrUtil.isBlank(host.getPanelHostId()) && !StrUtil.isBlank(primary)) {
            String domain = VhostOrderOptionSupport.normalizeDomain(param.getDomain());
            VhostOrderOptionSupport.assertCanRemoveDomain(host.getBindDomains(), domain);
            Map<String, Object> master = this.requireMaster(host);
            VhostPowerCommand command = new VhostPowerCommand();
            command.setPanelUrl(this.text(master, "panelUrl"));
            command.setApiKey(this.text(master, "apiKey"));
            command.setPanelHostId(host.getPanelHostId());
            command.setDomain(primary);
            command.setBindDomain(domain);
            this.adapter(master).deleteDomain(command);
            host.setBindDomains(VhostOrderOptionSupport.removeBindDomain(host.getBindDomains(), null, domain));
            this.updateById(host);
            return CommonResult.ok("域名已删除");
         } else {
            return CommonResult.error("网站尚未开通完成");
         }
      }
   }

   @Override
   public CommonResult<String> changeFtpPassword(AdminVhostPasswordParam param) {
      AdminVhost host = this.requireHost(param);
      CommonResult<String> blocked = this.validatePasswordHost(host, host.getFtpUser(), "FTP");
      if (blocked != null) {
         return blocked;
      } else {
         String error = this.adapter(this.requireMaster(host)).changeFtpPassword(this.passwordCommand(host, host.getFtpUser(), param.getPassword()));
         if (StrUtil.isNotBlank(error)) {
            return CommonResult.error(error);
         } else {
            host.setFtpPassword(param.getPassword());
            this.updateById(host);
            return CommonResult.ok("FTP密码已修改");
         }
      }
   }

   @Override
   public CommonResult<String> changeDbPassword(AdminVhostPasswordParam param) {
      AdminVhost host = this.requireHost(param);
      CommonResult<String> blocked = this.validatePasswordHost(host, host.getDbUser(), "数据库");
      if (blocked != null) {
         return blocked;
      } else {
         String error = this.adapter(this.requireMaster(host)).changeDbPassword(this.passwordCommand(host, host.getDbUser(), param.getPassword()));
         if (StrUtil.isNotBlank(error)) {
            return CommonResult.error(error);
         } else {
            host.setDbPassword(param.getPassword());
            this.updateById(host);
            return CommonResult.ok("数据库密码已修改");
         }
      }
   }

   @Override
   public void editRemark(AdminVhostRemarkParam param) {
      AdminVhost host = this.requireHost(param);
      host.setRemark(param.getRemark());
      this.updateById(host);
   }

   private CommonResult<String> validatePasswordHost(AdminVhost host, String username, String label) {
      if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         return CommonResult.error("虚拟主机已删除");
      } else if (StrUtil.isBlank(host.getPanelHostId()) || StrUtil.isBlank(host.getMasterId())) {
         return CommonResult.error("网站尚未开通完成");
      } else {
         return StrUtil.isBlank(username) ? CommonResult.error(label + "账号尚未开通完成") : null;
      }
   }

   private VhostPasswordCommand passwordCommand(AdminVhost host, String username, String password) {
      Map<String, Object> master = this.requireMaster(host);
      VhostPasswordCommand command = new VhostPasswordCommand();
      command.setPanelUrl(this.text(master, "panelUrl"));
      command.setApiKey(this.text(master, "apiKey"));
      command.setPanelHostId(host.getPanelHostId());
      command.setHostName(host.getHostName());
      command.setDomain(this.firstDomain(host));
      command.setUsername(username);
      command.setPassword(password);
      return command;
   }

   private AdminVhost requireHost(AdminVhostIdParam idParam) {
      AdminVhost host = (AdminVhost)this.getById(idParam.getId());
      if (host == null) {
         throw new CommonException("虚拟主机不存在", new Object[0]);
      } else {
         return host;
      }
   }

   private void fillPackage(List<AdminVhost> hosts) {
      if (hosts != null && !hosts.isEmpty()) {
         List<String> versionIds = hosts.stream().map(AdminVhost::getVersionId).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
         if (!versionIds.isEmpty()) {
            List<Map<String, Object>> rows = ((AdminVhostMapper)this.baseMapper).selectVersionByIds(versionIds);
            if (rows != null && !rows.isEmpty()) {
               Map<String, Map<String, Object>> versions = rows.stream()
                  .filter(Objects::nonNull)
                  .filter(item -> StrUtil.isNotBlank(this.text((Map<String, Object>)item, "id")))
                  .collect(Collectors.toMap(item -> this.text((Map<String, Object>)item, "id"), item -> item, (left, right) -> left));

               for (AdminVhost host : hosts) {
                  this.fillPackage(host, versions.get(host.getVersionId()));
               }
            }
         }
      }
   }

   private void fillPackage(AdminVhost host) {
      if (host != null && !StrUtil.isBlank(host.getVersionId())) {
         this.fillPackage(host, ((AdminVhostMapper)this.baseMapper).selectVersionById(host.getVersionId()));
      }
   }

   private void fillMasterNames(List<AdminVhost> hosts) {
      if (hosts != null && !hosts.isEmpty()) {
         List<String> masterIds = hosts.stream().map(AdminVhost::getMasterId).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
         if (!masterIds.isEmpty()) {
            List<Map<String, Object>> rows = ((AdminVhostMapper)this.baseMapper).selectMasterByIds(masterIds);
            if (rows != null && !rows.isEmpty()) {
               Map<String, String> names = rows.stream()
                  .filter(Objects::nonNull)
                  .filter(item -> StrUtil.isNotBlank(this.text((Map<String, Object>)item, "id")))
                  .collect(
                     Collectors.toMap(
                        item -> this.text((Map<String, Object>)item, "id"),
                        item -> StrUtil.nullToEmpty(this.text((Map<String, Object>)item, "name")),
                        (left, right) -> left
                     )
                  );

               for (AdminVhost host : hosts) {
                  if (StrUtil.isNotBlank(host.getMasterId())) {
                     host.setMasterName(names.get(host.getMasterId()));
                  }
               }
            }
         }
      }
   }

   private void fillPackage(AdminVhost host, Map<String, Object> version) {
      if (host != null && version != null && !version.isEmpty()) {
         String phpVersions = this.text(version, "phpVersions");
         if (phpVersions != null) {
            host.setPhpVersions(phpVersions);
         }

         String mysqlVersions = this.text(version, "mysqlVersions");
         if (mysqlVersions != null) {
            host.setMysqlVersions(mysqlVersions);
         }

         BigDecimal trafficPrice = this.decimal(version, "trafficPrice");
         if (trafficPrice != null) {
            host.setTrafficPrice(trafficPrice);
         }
      }
   }

   private String packagePhpVersions(String versionId) {
      return StrUtil.isBlank(versionId) ? null : this.text(((AdminVhostMapper)this.baseMapper).selectVersionById(versionId), "phpVersions");
   }

   private BigDecimal decimal(Map<String, Object> row, String key) {
      if (row != null && !row.isEmpty()) {
         for (Entry<String, Object> entry : row.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key) && entry.getValue() != null) {
               if (entry.getValue() instanceof BigDecimal value) {
                  return value;
               }

               String text = String.valueOf(entry.getValue()).trim();
               return text.isEmpty() ? null : new BigDecimal(text);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private Map<String, Object> requireMaster(AdminVhost host) {
      if (StrUtil.isBlank(host.getMasterId())) {
         throw new CommonException("网站尚未开通完成", new Object[0]);
      } else {
         Map<String, Object> master = this.vhostAutoTaskMapper.selectMasterById(host.getMasterId());
         if (master != null && !master.isEmpty()) {
            return master;
         } else {
            throw new CommonException("主控不存在", new Object[0]);
         }
      }
   }

   private VhostPanelAdapter adapter(Map<String, Object> master) {
      return this.vhostPanelRouter.getAdapter(this.text(master, "controlledVersion"));
   }

   private void markPhpVersions(List<VhostPhpVersion> versions, AdminVhost host) {
      if (versions != null) {
         for (VhostPhpVersion item : versions) {
            item.setAllowed(VhostOrderOptionSupport.allowedByPackage(item.getVersion(), host.getPhpVersions()));
            item.setCurrent(VhostOrderOptionSupport.matchesPhpVersion(item.getVersion(), host.getPhpVersion()));
         }
      }
   }

   private boolean domainUsedOnMaster(String masterId, String domain, String excludeHostId) {
      return !StrUtil.isBlank(masterId) && !StrUtil.isBlank(domain)
         ? this.list(
               (Wrapper<AdminVhost>)((LambdaQueryWrapper<AdminVhost>)((LambdaQueryWrapper<AdminVhost>)new LambdaQueryWrapper<AdminVhost>().eq(AdminVhost::getMasterId, masterId))
                     .ne(StrUtil.isNotBlank(excludeHostId), AdminVhost::getId, excludeHostId))
                  .ne(AdminVhost::getBusinessStatus, BusinessStatusEnum.DELETE.getCode())
            )
            .stream()
            .anyMatch(item -> VhostOrderOptionSupport.containsDomain(item.getBindDomains(), domain))
         : false;
   }

   private String firstDomain(AdminVhost host) {
      return host.getBindDomains() != null && !host.getBindDomains().isEmpty() ? host.getBindDomains().get(0) : null;
   }

   private String text(Map<String, Object> row, String key) {
      if (row != null && !row.isEmpty()) {
         for (Entry<String, Object> entry : row.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key) && entry.getValue() != null) {
               String value = String.valueOf(entry.getValue()).trim();
               return value.isEmpty() ? null : value;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private String resolveSortField(String sortField) {
      return StrUtil.equalsAny(sortField, new CharSequence[]{"endTime", "hostName", "createTime"}) ? StrUtil.toUnderlineCase(sortField) : "create_time";
   }
}
