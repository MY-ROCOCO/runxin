package com.rxinyun.admin.business.modular.vhost.task.runner;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rxinyun.admin.business.modular.vhost.entity.AdminVhost;
import com.rxinyun.admin.business.modular.vhost.mapper.AdminVhostMapper;
import com.rxinyun.admin.business.modular.vhost.task.entity.VhostAutoTask;
import com.rxinyun.admin.business.modular.vhost.task.mapper.VhostAutoTaskMapper;
import com.rxinyun.admin.business.modular.vhost.task.service.VhostAutoTaskService;
import com.rxinyun.business.api.MemberVhostApi;
import com.rxinyun.business.core.pojo.BaseMemberVhostProvision;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.timer.CommonTimerTaskRunner;
import com.rxinyun.common.vhost.VhostOrderOptionSupport;
import com.rxinyun.common.vhost.VhostPanelAdapter;
import com.rxinyun.common.vhost.VhostPanelRouter;
import com.rxinyun.common.vhost.param.VhostCreateCommand;
import com.rxinyun.common.vhost.param.VhostExpireCommand;
import com.rxinyun.common.vhost.param.VhostPowerCommand;
import com.rxinyun.common.vhost.result.VhostCreateResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VhostAutoTaskRunner implements CommonTimerTaskRunner {
   private static final Logger log = LoggerFactory.getLogger(VhostAutoTaskRunner.class);
   @Resource
   private VhostAutoTaskService vhostAutoTaskService;
   @Resource
   private VhostAutoTaskMapper vhostAutoTaskMapper;
   @Resource
   private AdminVhostMapper adminVhostMapper;
   @Resource
   private MemberVhostApi memberVhostApi;
   @Resource
   private VhostPanelRouter vhostPanelRouter;

   public void action() {
      this.vhostAutoTaskService.recoverStuckTasks();

      for (VhostAutoTask task : this.vhostAutoTaskService.listPending(20)) {
         try {
            if ("RENEW".equals(task.getTaskType())) {
               this.processRenew(task);
            } else if ("START".equals(task.getTaskType()) || "STOP".equals(task.getTaskType())) {
               this.processPower(task);
            } else if ("DELETE".equals(task.getTaskType())) {
               this.processDelete(task);
            } else {
               this.processCreate(task);
            }
         } catch (Exception var5) {
            log.error("虚拟主机任务失败, taskId={}, type={}", new Object[]{task.getId(), task.getTaskType(), var5});
            this.vhostAutoTaskService.markFailure(task, "执行失败：" + var5.getMessage(), true);
         }
      }
   }

   private void processCreate(VhostAutoTask task) {
      if (this.vhostAutoTaskService.claim(task.getId())) {
         AdminVhost host = (AdminVhost)this.adminVhostMapper.selectById(task.getHostId());
         if (host == null) {
            this.vhostAutoTaskService.markFailure(task, "虚拟主机不存在", false);
         } else if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
            this.vhostAutoTaskService.markFailure(task, "虚拟主机已删除", false);
         } else if (this.isProvisioned(host)) {
            this.vhostAutoTaskService.markSuccess(task.getId(), host.getMasterId(), "实例已开通");
         } else {
            Map<String, Object> master = this.resolveMaster(task, host, true);
            if (master == null) {
               this.vhostAutoTaskService.markFailure(task, this.domainUsedOnLineMasters(host) ? "同一主控下该域名已被其他虚拟主机使用" : "线路未绑定可用主控", true);
            } else {
               String masterId = this.text(master, "id");
               if (StrUtil.isBlank(task.getMasterId())) {
                  this.vhostAutoTaskService.bindMaster(task.getId(), masterId);
                  task.setMasterId(masterId);
               }

               VhostPanelAdapter adapter = this.vhostPanelRouter.getAdapter(this.text(master, "controlledVersion"));
               String panelUrl = this.text(master, "panelUrl");
               String apiKey = this.text(master, "apiKey");
               String domain = this.firstDomain(host);
               if (StrUtil.isBlank(domain)) {
                  domain = VhostOrderOptionSupport.randomComDomain();
                  host.setBindDomains(List.of(domain));
                  this.adminVhostMapper.updateById(host);
               }

               VhostCreateCommand command = new VhostCreateCommand();
               command.setPanelUrl(panelUrl);
               command.setApiKey(apiKey);
               command.setHostName(host.getHostName());
               command.setDomain(domain);
               command.setPhpVersion(host.getPhpVersion());
               command.setRemark(host.getHostName());
               command.setFtpUser(host.getFtpUser());
               command.setFtpPassword(host.getFtpPassword());
               command.setDbUser(host.getDbUser());
               command.setDbPassword(host.getDbPassword());
               VhostCreateResult result = adapter.findExistingHost(command);
               if ((result == null || !result.accountsReady()) && (result == null || StrUtil.isBlank(result.getPanelHostId()))) {
                  result = adapter.createHost(command);
               }

               if (result != null) {
                  if (StrUtil.isBlank(result.getFtpUser())) {
                     result.setFtpUser(StrUtil.blankToDefault(command.getFtpUser(), host.getFtpUser()));
                     result.setFtpPassword(StrUtil.blankToDefault(command.getFtpPassword(), host.getFtpPassword()));
                  }

                  if (StrUtil.isBlank(result.getDbUser())) {
                     result.setDbUser(StrUtil.blankToDefault(command.getDbUser(), host.getDbUser()));
                     result.setDbPassword(StrUtil.blankToDefault(command.getDbPassword(), host.getDbPassword()));
                     result.setDbName(StrUtil.blankToDefault(result.getDbUser(), host.getDbName()));
                  }

                  this.writeBack(host.getId(), masterId, this.text(master, "ftpAddress"), result, false);
               }

               if (result != null && !StrUtil.isBlank(result.getPanelHostId())) {
                  if (!result.accountsReady()) {
                     this.vhostAutoTaskService.markFailure(task, "网站已存在，正在回写FTP和数据库账号", true);
                  } else if (host.getEndTime() == null) {
                     this.vhostAutoTaskService.markFailure(task, "到期时间不能为空，网站已创建，请核对后续开通", false);
                  } else {
                     VhostExpireCommand expireCommand = new VhostExpireCommand();
                     expireCommand.setPanelUrl(panelUrl);
                     expireCommand.setApiKey(apiKey);
                     expireCommand.setPanelHostId(result.getPanelHostId());
                     expireCommand.setEndTime(host.getEndTime());
                     String expireError = adapter.setExpireTime(expireCommand);
                     if (StrUtil.isNotBlank(expireError)) {
                        this.vhostAutoTaskService.markFailure(task, "网站已创建，设置到期时间失败：" + expireError, true);
                     } else if (!this.writeBack(host.getId(), masterId, this.text(master, "ftpAddress"), result, true)) {
                        this.vhostAutoTaskService.markFailure(task, "主控已创建，回写产品列表失败", false);
                     } else {
                        this.vhostAutoTaskService.markSuccess(task.getId(), masterId, result.getMessage());
                     }
                  }
               } else {
                  this.vhostAutoTaskService.markFailure(task, result == null ? "创建网站失败" : result.getMessage(), true);
               }
            }
         }
      }
   }

   private void processRenew(VhostAutoTask task) {
      if (this.vhostAutoTaskService.claim(task.getId())) {
         AdminVhost host = (AdminVhost)this.adminVhostMapper.selectById(task.getHostId());
         if (host == null) {
            this.vhostAutoTaskService.markFailure(task, "虚拟主机不存在", false);
         } else if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
            this.vhostAutoTaskService.markFailure(task, "虚拟主机已删除", false);
         } else if (StrUtil.isBlank(host.getPanelHostId())) {
            this.vhostAutoTaskService.markFailure(task, "主控网站尚未创建，无法同步到期时间", true);
         } else if (host.getEndTime() == null) {
            this.vhostAutoTaskService.markFailure(task, "到期时间不能为空", false);
         } else {
            Map<String, Object> master = this.resolveMaster(task, host, false);
            if (master == null) {
               this.vhostAutoTaskService.markFailure(task, "线路未绑定可用主控", true);
            } else {
               String masterId = this.text(master, "id");
               if (StrUtil.isBlank(task.getMasterId())) {
                  this.vhostAutoTaskService.bindMaster(task.getId(), masterId);
               }

               VhostExpireCommand expireCommand = new VhostExpireCommand();
               expireCommand.setPanelUrl(this.text(master, "panelUrl"));
               expireCommand.setApiKey(this.text(master, "apiKey"));
               expireCommand.setPanelHostId(host.getPanelHostId());
               expireCommand.setEndTime(host.getEndTime());
               VhostPanelAdapter adapter = this.vhostPanelRouter.getAdapter(this.text(master, "controlledVersion"));
               String expireError = adapter.setExpireTime(expireCommand);
               if (StrUtil.isNotBlank(expireError)) {
                  this.vhostAutoTaskService.markFailure(task, expireError, true);
               } else {
                  this.vhostAutoTaskService.markSuccess(task.getId(), masterId, "到期时间已同步到主控");
                  host = (AdminVhost)this.adminVhostMapper.selectById(task.getHostId());
                  if (host != null && ServerStatusEnum.STOP.getCode().equals(host.getHostStatus()) && StrUtil.isNotBlank(host.getPanelHostId())) {
                     int inserted = this.vhostAutoTaskMapper
                        .insertOperateTask(
                           IdWorker.getIdStr(),
                           host.getId(),
                           masterId,
                           "START",
                           "续费启用虚拟主机 " + StrUtil.blankToDefault(host.getHostName(), host.getId()),
                           StrUtil.blankToDefault(task.getPlatform(), "B")
                        );
                     if (inserted > 0) {
                        host.setHostStatus(ServerStatusEnum.STARTING.getCode());
                        this.adminVhostMapper.updateById(host);
                     }
                  }
               }
            }
         }
      }
   }

   private void processPower(VhostAutoTask task) {
      if (this.vhostAutoTaskService.claim(task.getId())) {
         AdminVhost host = (AdminVhost)this.adminVhostMapper.selectById(task.getHostId());
         if (host == null) {
            this.vhostAutoTaskService.markFailure(task, "虚拟主机不存在", false);
         } else if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
            this.vhostAutoTaskService.markFailure(task, "虚拟主机已删除", false);
         } else {
            String domain = this.firstDomain(host);
            if (!StrUtil.isBlank(host.getPanelHostId()) && !StrUtil.isBlank(domain)) {
               Map<String, Object> master = this.resolveMaster(task, host, false);
               if (master == null) {
                  this.vhostAutoTaskService.markFailure(task, "线路未绑定可用主控", true);
               } else {
                  String masterId = this.text(master, "id");
                  if (StrUtil.isBlank(task.getMasterId())) {
                     this.vhostAutoTaskService.bindMaster(task.getId(), masterId);
                  }

                  VhostPowerCommand command = new VhostPowerCommand();
                  command.setPanelUrl(this.text(master, "panelUrl"));
                  command.setApiKey(this.text(master, "apiKey"));
                  command.setPanelHostId(host.getPanelHostId());
                  command.setDomain(domain);
                  VhostPanelAdapter adapter = this.vhostPanelRouter.getAdapter(this.text(master, "controlledVersion"));
                  boolean start = "START".equals(task.getTaskType());
                  String error = start ? adapter.startSite(command) : adapter.stopSite(command);
                  if (StrUtil.isNotBlank(error)) {
                     this.vhostAutoTaskService.markFailure(task, error, true);
                  } else {
                     host.setHostStatus(start ? ServerStatusEnum.RUNNING.getCode() : ServerStatusEnum.STOP.getCode());
                     this.adminVhostMapper.updateById(host);
                     this.vhostAutoTaskService.markSuccess(task.getId(), masterId, start ? "站点已启用" : "站点已停用");
                  }
               }
            } else {
               this.vhostAutoTaskService.markFailure(task, "网站尚未开通完成", false);
            }
         }
      }
   }

   private void processDelete(VhostAutoTask task) {
      if (this.vhostAutoTaskService.claim(task.getId())) {
         AdminVhost host = (AdminVhost)this.adminVhostMapper.selectById(task.getHostId());
         if (host == null) {
            this.vhostAutoTaskService.markFailure(task, "虚拟主机不存在", false);
         } else {
            String domain = this.firstDomain(host);
            if (!StrUtil.isBlank(host.getPanelHostId()) && !StrUtil.isBlank(domain)) {
               Map<String, Object> master = this.resolveMaster(task, host, false);
               if (master == null) {
                  this.vhostAutoTaskService.markFailure(task, "线路未绑定可用主控", true);
               } else {
                  String masterId = this.text(master, "id");
                  if (StrUtil.isBlank(task.getMasterId())) {
                     this.vhostAutoTaskService.bindMaster(task.getId(), masterId);
                  }

                  VhostPowerCommand command = new VhostPowerCommand();
                  command.setPanelUrl(this.text(master, "panelUrl"));
                  command.setApiKey(this.text(master, "apiKey"));
                  command.setPanelHostId(host.getPanelHostId());
                  command.setDomain(VhostOrderOptionSupport.stripPort(domain));
                  VhostPanelAdapter adapter = this.vhostPanelRouter.getAdapter(this.text(master, "controlledVersion"));
                  String error = adapter.deleteSite(command);
                  if (StrUtil.isNotBlank(error)) {
                     this.vhostAutoTaskService.markFailure(task, error, true);
                  } else {
                     host.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
                     host.setHostStatus(ServerStatusEnum.STOP.getCode());
                     this.adminVhostMapper.updateById(host);
                     this.vhostAutoTaskService.markSuccess(task.getId(), masterId, "面板网站已删除");
                  }
               }
            } else {
               host.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
               host.setHostStatus(ServerStatusEnum.STOP.getCode());
               this.adminVhostMapper.updateById(host);
               this.vhostAutoTaskService.markSuccess(task.getId(), host.getMasterId(), "面板网站尚未开通，已标记删除");
            }
         }
      }
   }

   private boolean writeBack(String hostId, String masterId, String ftpAddress, VhostCreateResult result, boolean running) {
      BaseMemberVhostProvision provision = new BaseMemberVhostProvision();
      provision.setHostId(hostId);
      provision.setMasterId(masterId);
      provision.setPanelHostId(result.getPanelHostId());
      provision.setFtpAddress(ftpAddress);
      provision.setFtpUser(result.getFtpUser());
      provision.setFtpPassword(result.getFtpPassword());
      provision.setDbName(result.getDbName());
      provision.setDbUser(result.getDbUser());
      provision.setDbPassword(result.getDbPassword());
      provision.setRunning(running);
      return Boolean.TRUE.equals(this.memberVhostApi.provision(provision));
   }

   private String firstDomain(AdminVhost host) {
      return host.getBindDomains() != null && !host.getBindDomains().isEmpty() ? host.getBindDomains().get(0) : null;
   }

   private boolean isProvisioned(AdminVhost host) {
      return ServerStatusEnum.RUNNING.getCode().equals(host.getHostStatus())
         && StrUtil.isNotBlank(host.getPanelHostId())
         && StrUtil.isNotBlank(host.getFtpUser())
         && StrUtil.isNotBlank(host.getDbUser());
   }

   private Map<String, Object> resolveMaster(VhostAutoTask task, AdminVhost host, boolean checkDomain) {
      if (StrUtil.isNotBlank(task.getMasterId())) {
         Map<String, Object> bound = this.vhostAutoTaskMapper.selectMasterById(task.getMasterId());
         return checkDomain && !this.masterAllowsDomains(bound, host) ? null : bound;
      } else if (StrUtil.isNotBlank(host.getMasterId())) {
         Map<String, Object> bound = this.vhostAutoTaskMapper.selectMasterById(host.getMasterId());
         return checkDomain && !this.masterAllowsDomains(bound, host) ? null : bound;
      } else if (StrUtil.isBlank(host.getLineId())) {
         return null;
      } else {
         List<Map<String, Object>> masters = this.vhostAutoTaskMapper.selectEnabledMastersByLine(host.getLineId());
         if (masters != null && !masters.isEmpty()) {
            if (checkDomain) {
               masters = masters.stream().filter(itemx -> this.masterAllowsDomains(itemx, host)).toList();
               if (masters.isEmpty()) {
                  return null;
               }
            }

            int total = 0;

            for (Map<String, Object> item : masters) {
               total += this.weightOf(this.value(item, "weight"));
            }

            int point = ThreadLocalRandom.current().nextInt(Math.max(total, 1));
            int cursor = 0;
            Map<String, Object> selected = masters.get(0);

            for (Map<String, Object> item : masters) {
               cursor += this.weightOf(this.value(item, "weight"));
               if (point < cursor) {
                  selected = item;
                  break;
               }
            }

            return selected;
         } else {
            return null;
         }
      }
   }

   private boolean masterAllowsDomains(Map<String, Object> master, AdminVhost host) {
      String masterId = this.text(master, "id");
      if (!StrUtil.isBlank(masterId) && host.getBindDomains() != null) {
         for (String domain : host.getBindDomains()) {
            if (StrUtil.isNotBlank(domain) && this.domainUsedOnMaster(masterId, domain, host.getId())) {
               return false;
            }
         }

         return true;
      } else {
         return master != null;
      }
   }

   private boolean domainUsedOnLineMasters(AdminVhost host) {
      if (!StrUtil.isBlank(host.getLineId()) && host.getBindDomains() != null) {
         List<Map<String, Object>> masters = this.vhostAutoTaskMapper.selectEnabledMastersByLine(host.getLineId());
         return masters != null && !masters.isEmpty() ? masters.stream().noneMatch(item -> this.masterAllowsDomains((Map<String, Object>)item, host)) : false;
      } else {
         return false;
      }
   }

   private boolean domainUsedOnMaster(String masterId, String domain, String excludeHostId) {
      return !StrUtil.isBlank(masterId) && !StrUtil.isBlank(domain)
         ? this.adminVhostMapper
            .selectList(
               (Wrapper<AdminVhost>)((LambdaQueryWrapper<AdminVhost>)((LambdaQueryWrapper<AdminVhost>)new LambdaQueryWrapper<AdminVhost>().eq(AdminVhost::getMasterId, masterId))
                     .ne(StrUtil.isNotBlank(excludeHostId), AdminVhost::getId, excludeHostId))
                  .ne(AdminVhost::getBusinessStatus, BusinessStatusEnum.DELETE.getCode())
            )
            .stream()
            .anyMatch(item -> VhostOrderOptionSupport.containsDomain(item.getBindDomains(), domain))
         : false;
   }

   private int weightOf(Object value) {
      return value instanceof Number number && number.intValue() > 0 ? number.intValue() : 1;
   }

   private String text(Map<String, Object> row, String key) {
      Object value = this.value(row, key);
      if (value == null) {
         return null;
      } else {
         String text = String.valueOf(value).trim();
         return text.isEmpty() ? null : text;
      }
   }

   private Object value(Map<String, Object> row, String key) {
      if (row != null && !row.isEmpty()) {
         for (Entry<String, Object> entry : row.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(key)) {
               return entry.getValue();
            }
         }

         return null;
      } else {
         return null;
      }
   }
}
