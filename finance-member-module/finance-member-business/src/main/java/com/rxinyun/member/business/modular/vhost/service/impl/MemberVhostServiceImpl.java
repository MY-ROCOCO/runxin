package com.rxinyun.member.business.modular.vhost.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.business.core.pojo.BaseMemberVhost;
import com.rxinyun.business.core.pojo.BaseMemberVhostCreate;
import com.rxinyun.business.core.pojo.BaseMemberVhostProvision;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.UniqueHostnameGenerator;
import com.rxinyun.common.vhost.VhostOrderOptionSupport;
import com.rxinyun.common.vhost.VhostPanelAdapter;
import com.rxinyun.common.vhost.VhostPanelRouter;
import com.rxinyun.common.vhost.param.VhostPasswordCommand;
import com.rxinyun.common.vhost.param.VhostPowerCommand;
import com.rxinyun.common.vhost.result.VhostPhpVersion;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.business.modular.vhost.entity.MemberVhost;
import com.rxinyun.member.business.modular.vhost.mapper.MemberVhostMapper;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostDomainParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostIdParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostPageParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostPasswordParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostPhpParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostRemarkParam;
import com.rxinyun.member.business.modular.vhost.service.MemberVhostService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberVhostServiceImpl extends ServiceImpl<MemberVhostMapper, MemberVhost> implements MemberVhostService {
   private static final Logger log = LoggerFactory.getLogger(MemberVhostServiceImpl.class);
   @Resource
   private VhostPanelRouter vhostPanelRouter;

   @Override
   public Page<MemberVhost> page(MemberVhostPageParam pageParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      QueryWrapper<MemberVhost> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberVhost::getMemberNo, String.valueOf(clientLoginUser.getId()));
      if (pageParam != null && StrUtil.isNotBlank(pageParam.getHostStatus())) {
         queryWrapper.lambda().eq(MemberVhost::getHostStatus, pageParam.getHostStatus());
      }

      if (pageParam != null && pageParam.getBusinessStatus() != null) {
         queryWrapper.lambda().eq(MemberVhost::getBusinessStatus, pageParam.getBusinessStatus());
      }

      if (pageParam != null && StrUtil.isNotBlank(pageParam.getHostName())) {
         queryWrapper.lambda().like(MemberVhost::getHostName, pageParam.getHostName());
      } else if (pageParam != null && StrUtil.isNotBlank(pageParam.getSearchKey())) {
         queryWrapper.lambda()
            .and(
               q -> ((LambdaQueryWrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)q.like(MemberVhost::getHostName, pageParam.getSearchKey())).or())
                  .like(MemberVhost::getOrderNo, pageParam.getSearchKey())
            );
      }

      if (pageParam != null && ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), this.resolveSortField(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<MemberVhost> page = (Page<MemberVhost>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillPackage(page.getRecords());
      return page;
   }

   @Override
   public MemberVhost detail(MemberVhostIdParam idParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberVhost host = (MemberVhost)this.getOne(
         (Wrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)new LambdaQueryWrapper<MemberVhost>().eq(MemberVhost::getId, idParam.getId()))
            .eq(MemberVhost::getMemberNo, String.valueOf(clientLoginUser.getId()))
      );
      if (host == null) {
         throw new CommonException("虚拟主机不存在", new Object[0]);
      } else {
         this.fillPackage(host);
         return host;
      }
   }

   @Override
   public void create(BaseMemberVhostCreate param) {
      if (param != null && !StrUtil.isBlank(param.getOrderNo()) && !StrUtil.isBlank(param.getMemberNo())) {
         int number = param.getNumber() != null && param.getNumber() >= 1 ? param.getNumber() : 1;
         long existing = this.count((Wrapper<MemberVhost>)new LambdaQueryWrapper<MemberVhost>().eq(MemberVhost::getOrderNo, param.getOrderNo()));

         for (int i = (int)existing; i < number; i++) {
            MemberVhost host = new MemberVhost();
            BeanUtil.copyProperties(param, host, new String[]{"id", "hostName", "bindDomains", "domains"});
            host.setId(null);
            host.setHostName("vh_" + UniqueHostnameGenerator.generateUniqueHostname(10));
            host.setPhpVersion(param.getPhpVersion());
            host.setBindDomains(List.of(VhostOrderOptionSupport.randomComDomain()));
            host.setHostStatus(ServerStatusEnum.CREATING.getCode());
            host.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
            host.setLastOrderNo(param.getOrderNo());
            host.setDeleteTime(this.resolveDeleteTime(host.getEndTime()));
            this.save(host);
         }

         String platform = StrUtil.blankToDefault(param.getPlatform(), "C");
         this.list((Wrapper<MemberVhost>)new LambdaQueryWrapper<MemberVhost>().eq(MemberVhost::getOrderNo, param.getOrderNo()))
            .forEach(
               hostx -> ((MemberVhostMapper)this.baseMapper).insertCreateTask(IdWorker.getIdStr(), hostx.getId(), "创建虚拟主机 " + hostx.getHostName(), platform)
            );
      } else {
         throw new CommonException("虚拟主机开通参数不完整", new Object[0]);
      }
   }

   @Override
   public BaseMemberVhost selectById(String hostId) {
      if (StrUtil.isBlank(hostId)) {
         return null;
      } else {
         MemberVhost host = (MemberVhost)this.getById(hostId);
         if (host == null) {
            return null;
         } else {
            this.fillPackage(host);
            return (BaseMemberVhost)BeanUtil.toBean(host, BaseMemberVhost.class);
         }
      }
   }

   @Override
   public BaseMemberVhost selectByIdAndMemberNo(String hostId, String memberNo) {
      MemberVhost host = this.findOwned(hostId, memberNo);
      if (host == null) {
         return null;
      } else {
         this.fillPackage(host);
         return (BaseMemberVhost)BeanUtil.toBean(host, BaseMemberVhost.class);
      }
   }

   @Override
   public List<BaseMemberVhost> listByOrderNo(String orderNo) {
      if (StrUtil.isBlank(orderNo)) {
         return List.of();
      } else {
         List<MemberVhost> hosts = this.list(
            (Wrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)new LambdaQueryWrapper<MemberVhost>().eq(MemberVhost::getOrderNo, orderNo)).orderByAsc(CommonEntity::getCreateTime)
         );
         this.fillPackage(hosts);
         return hosts.stream().map(host -> (BaseMemberVhost)BeanUtil.toBean(host, BaseMemberVhost.class)).collect(Collectors.toList());
      }
   }

   @Override
   public void renew(String hostId, String memberNo, Integer months, String orderNo, String platform) {
      MemberVhost host = this.requireOwned(hostId, memberNo);
      if (months != null && months >= 1 && !StrUtil.isBlank(orderNo)) {
         if (!StrUtil.equals(orderNo, host.getLastOrderNo())) {
            Date baseTime = host.getEndTime() == null ? new Date() : host.getEndTime();
            boolean expired = host.getEndTime() != null && host.getEndTime().before(new Date());
            host.setEndTime(DateUtil.offsetMonth(baseTime, months));
            host.setMonths(months);
            host.setDeleteTime(this.resolveDeleteTime(host.getEndTime()));
            if (expired || BusinessStatusEnum.EXPIRED.getCode().equals(host.getBusinessStatus())) {
               host.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
            }

            host.setLastOrderNo(orderNo);
            this.updateById(host);
         }

         ((MemberVhostMapper)this.baseMapper)
            .insertRenewTask(
               IdWorker.getIdStr(),
               host.getId(),
               host.getMasterId(),
               "续费虚拟主机 " + StrUtil.blankToDefault(host.getHostName(), host.getId()),
               StrUtil.blankToDefault(platform, "C")
            );
      } else {
         throw new CommonException("虚拟主机续费参数不完整", new Object[0]);
      }
   }

   @Override
   public void upgradeTraffic(String hostId, String memberNo, Integer upgradeTraffic, String orderNo) {
      MemberVhost host = this.requireOwned(hostId, memberNo);
      if (upgradeTraffic == null || upgradeTraffic < 1 || StrUtil.isBlank(orderNo)) {
         throw new CommonException("虚拟主机升级流量参数不完整", new Object[0]);
      } else if (!StrUtil.equals(orderNo, host.getLastOrderNo())) {
         int currentTraffic = host.getTrafficSize() == null ? 0 : host.getTrafficSize();
         int currentBillable = host.getBillableTraffic() == null ? 0 : host.getBillableTraffic();
         host.setTrafficSize(currentTraffic + upgradeTraffic);
         host.setBillableTraffic(currentBillable + upgradeTraffic);
         host.setLastOrderNo(orderNo);
         this.updateById(host);
      }
   }

   @Override
   public void provision(BaseMemberVhostProvision param) {
      if (param != null && !StrUtil.isBlank(param.getHostId())) {
         MemberVhost host = (MemberVhost)this.getById(param.getHostId());
         if (host == null) {
            throw new CommonException("虚拟主机不存在", new Object[0]);
         } else if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
            throw new CommonException("虚拟主机已删除", new Object[0]);
         } else {
            if (StrUtil.isNotBlank(param.getMasterId())) {
               host.setMasterId(param.getMasterId());
            }

            if (StrUtil.isNotBlank(param.getPanelHostId())) {
               host.setPanelHostId(param.getPanelHostId());
            }

            if (StrUtil.isNotBlank(param.getFtpAddress())) {
               host.setFtpAddress(param.getFtpAddress());
            }

            if (StrUtil.isNotBlank(param.getFtpUser())) {
               host.setFtpUser(param.getFtpUser());
            }

            if (StrUtil.isNotBlank(param.getFtpPassword())) {
               host.setFtpPassword(param.getFtpPassword());
            }

            if (StrUtil.isNotBlank(param.getDbName())) {
               host.setDbName(param.getDbName());
            }

            if (StrUtil.isNotBlank(param.getDbUser())) {
               host.setDbUser(param.getDbUser());
            }

            if (StrUtil.isNotBlank(param.getDbPassword())) {
               host.setDbPassword(param.getDbPassword());
            }

            if (param.getBindDomains() != null && !param.getBindDomains().isEmpty()) {
               host.setBindDomains(param.getBindDomains());
            }

            if (Boolean.TRUE.equals(param.getRunning())) {
               host.setHostStatus(ServerStatusEnum.RUNNING.getCode());
            }

            this.updateById(host);
         }
      } else {
         throw new CommonException("虚拟主机开通回写参数不完整", new Object[0]);
      }
   }

   @Override
   public CommonResult<String> start(MemberVhostIdParam idParam) {
      return this.submitPower(idParam, "START", ServerStatusEnum.STOP.getCode(), ServerStatusEnum.STARTING.getCode(), "启动");
   }

   @Override
   public CommonResult<String> stop(MemberVhostIdParam idParam) {
      return this.submitPower(idParam, "STOP", ServerStatusEnum.RUNNING.getCode(), ServerStatusEnum.STOPPING.getCode(), "停止");
   }

   private CommonResult<String> submitPower(MemberVhostIdParam idParam, String taskType, String requiredStatus, String pendingStatus, String label) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberVhost host = this.requireOwned(idParam.getId(), String.valueOf(clientLoginUser.getId()));
      String error = this.validatePower(host, requiredStatus);
      if (error != null) {
         return CommonResult.error(error);
      } else {
         int inserted = ((MemberVhostMapper)this.baseMapper)
            .insertOperateTask(
               IdWorker.getIdStr(), host.getId(), host.getMasterId(), taskType, label + "虚拟主机 " + StrUtil.blankToDefault(host.getHostName(), host.getId()), "C"
            );
         if (inserted < 1) {
            return CommonResult.error("存在未完成的任务，请稍后重试");
         } else {
            host.setHostStatus(pendingStatus);
            this.updateById(host);
            return CommonResult.ok(label + "任务已提交");
         }
      }
   }

   @Override
   public List<VhostPhpVersion> phpVersions(MemberVhostIdParam idParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberVhost host = this.requireOwned(idParam.getId(), String.valueOf(clientLoginUser.getId()));
      this.fillPackage(host);
      Map<String, Object> master = this.requireMaster(host);
      List<VhostPhpVersion> versions = this.adapter(master).listPhpVersions(this.text(master, "panelUrl"), this.text(master, "apiKey"));
      this.markPhpVersions(versions, host);
      return versions;
   }

   @Override
   public CommonResult<String> changePhp(MemberVhostPhpParam param) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberVhost host = this.requireOwned(param.getId(), String.valueOf(clientLoginUser.getId()));
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

   @Override
   public CommonResult<String> addDomain(MemberVhostDomainParam param) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberVhost host = this.requireOwned(param.getId(), String.valueOf(clientLoginUser.getId()));
      String primary = this.firstDomain(host);
      if (!StrUtil.isBlank(host.getPanelHostId()) && !StrUtil.isBlank(primary)) {
         String domain = VhostOrderOptionSupport.normalizeDomain(param.getDomain());
         if (VhostOrderOptionSupport.containsDomain(host.getBindDomains(), domain)) {
            return CommonResult.error("该域名已绑定");
         } else {
            Map<String, Object> master = this.requireMaster(host);
            if (this.domainUsedOnMaster(host.getMasterId(), domain, host.getId())) {
               return CommonResult.error("同一主控下该域名已被其他虚拟主机使用");
            } else {
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
         }
      } else {
         return CommonResult.error("网站尚未开通完成");
      }
   }

   @Override
   public CommonResult<String> deleteDomain(MemberVhostDomainParam param) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberVhost host = this.requireOwned(param.getId(), String.valueOf(clientLoginUser.getId()));
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

   @Override
   public CommonResult<String> changeFtpPassword(MemberVhostPasswordParam param) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberVhost host = this.requireOwned(param.getId(), String.valueOf(clientLoginUser.getId()));
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
   public CommonResult<String> changeDbPassword(MemberVhostPasswordParam param) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberVhost host = this.requireOwned(param.getId(), String.valueOf(clientLoginUser.getId()));
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
   public void editRemark(MemberVhostRemarkParam param) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberVhost host = this.requireOwned(param.getId(), String.valueOf(clientLoginUser.getId()));
      host.setMemberRemark(param.getMemberRemark());
      this.updateById(host);
   }

   private CommonResult<String> validatePasswordHost(MemberVhost host, String username, String label) {
      if (StrUtil.isBlank(host.getPanelHostId()) || StrUtil.isBlank(host.getMasterId())) {
         return CommonResult.error("网站尚未开通完成");
      } else {
         return StrUtil.isBlank(username) ? CommonResult.error(label + "账号尚未开通完成") : null;
      }
   }

   private VhostPasswordCommand passwordCommand(MemberVhost host, String username, String password) {
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

   @Override
   public List<BaseMemberVhost> listExpiredToStop() {
      return this.list(
            (Wrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)new LambdaQueryWrapper<MemberVhost>().eq(MemberVhost::getBusinessStatus, BusinessStatusEnum.NORMAL.getCode()))
                  .le(MemberVhost::getEndTime, new Date()))
               .last("limit 50")
         )
         .stream()
         .map(host -> (BaseMemberVhost)BeanUtil.toBean(host, BaseMemberVhost.class))
         .collect(Collectors.toList());
   }

   @Override
   public void expireStop(String hostId) {
      MemberVhost host = (MemberVhost)this.getById(hostId);
      if (host != null && !BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         if (!BusinessStatusEnum.EXPIRED.getCode().equals(host.getBusinessStatus())
            || !ServerStatusEnum.STOP.getCode().equals(host.getHostStatus()) && !ServerStatusEnum.STOPPING.getCode().equals(host.getHostStatus())) {
            boolean needStopTask = StrUtil.isNotBlank(host.getPanelHostId())
               && StrUtil.isNotBlank(this.firstDomain(host))
               && !ServerStatusEnum.STOP.getCode().equals(host.getHostStatus());
            if (needStopTask) {
               int inserted = ((MemberVhostMapper)this.baseMapper)
                  .insertOperateTask(
                     IdWorker.getIdStr(), host.getId(), host.getMasterId(), "STOP", "到期停用虚拟主机 " + StrUtil.blankToDefault(host.getHostName(), host.getId()), "B"
                  );
               if (inserted < 1) {
                  return;
               }

               host.setHostStatus(ServerStatusEnum.STOPPING.getCode());
            } else {
               host.setHostStatus(ServerStatusEnum.STOP.getCode());
            }

            host.setBusinessStatus(BusinessStatusEnum.EXPIRED.getCode());
            if (host.getDeleteTime() == null) {
               host.setDeleteTime(this.resolveDeleteTime(host.getEndTime()));
            }

            this.updateById(host);
         }
      }
   }

   @Override
   public List<BaseMemberVhost> listExpiredToDelete() {
      return this.list(
            (Wrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)new LambdaQueryWrapper<MemberVhost>()
                     .eq(MemberVhost::getBusinessStatus, BusinessStatusEnum.EXPIRED.getCode()))
                  .le(MemberVhost::getDeleteTime, new Date()))
               .last("limit 50")
         )
         .stream()
         .map(host -> (BaseMemberVhost)BeanUtil.toBean(host, BaseMemberVhost.class))
         .collect(Collectors.toList());
   }

   @Override
   public void expireDelete(String hostId) {
      this.enqueueDelete(hostId, "到期删除虚拟主机 ");
   }

   @Override
   public void refundDelete(String hostId) {
      this.enqueueDelete(hostId, "退款删除虚拟主机 ");
   }

   private void enqueueDelete(String hostId, String taskDescPrefix) {
      MemberVhost host = (MemberVhost)this.getById(hostId);
      if (host != null) {
         if (!BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
            host.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
            host.setHostStatus(ServerStatusEnum.STOP.getCode());
            this.updateById(host);
         }

         ((MemberVhostMapper)this.baseMapper)
            .insertDeleteTask(
               IdWorker.getIdStr(), host.getId(), host.getMasterId(), taskDescPrefix + StrUtil.blankToDefault(host.getHostName(), host.getId()), "B"
            );
      }
   }

   private String validatePower(MemberVhost host, String requiredStatus) {
      if (BusinessStatusEnum.EXPIRED.getCode().equals(host.getBusinessStatus())) {
         return "虚拟主机已到期";
      } else if (!StrUtil.isBlank(host.getPanelHostId()) && host.getBindDomains() != null && !host.getBindDomains().isEmpty()) {
         if (ServerStatusEnum.STARTING.getCode().equals(host.getHostStatus()) || ServerStatusEnum.STOPPING.getCode().equals(host.getHostStatus())) {
            return "存在进行中的启动或停止操作";
         } else if (!requiredStatus.equals(host.getHostStatus())) {
            return ServerStatusEnum.STOP.getCode().equals(requiredStatus) ? "仅已停止的网站可以启动" : "仅运行中的网站可以停止";
         } else {
            return null;
         }
      } else {
         return "网站尚未开通完成";
      }
   }

   private MemberVhost findOwned(String hostId, String memberNo) {
      return !StrUtil.isBlank(hostId) && !StrUtil.isBlank(memberNo)
         ? (MemberVhost)this.getOne(
            (Wrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)new LambdaQueryWrapper<MemberVhost>().eq(MemberVhost::getId, hostId)).eq(MemberVhost::getMemberNo, memberNo))
               .last("limit 1")
         )
         : null;
   }

   private MemberVhost requireOwned(String hostId, String memberNo) {
      MemberVhost host = this.findOwned(hostId, memberNo);
      if (host == null) {
         throw new CommonException("虚拟主机不存在", new Object[0]);
      } else if (BusinessStatusEnum.DELETE.getCode().equals(host.getBusinessStatus())) {
         throw new CommonException("虚拟主机已删除", new Object[0]);
      } else {
         return host;
      }
   }

   private String resolveSortField(String sortField) {
      return StrUtil.equalsAny(sortField, new CharSequence[]{"endTime", "hostName", "createTime"}) ? StrUtil.toUnderlineCase(sortField) : "create_time";
   }

   private Map<String, Object> requireMaster(MemberVhost host) {
      if (StrUtil.isBlank(host.getMasterId())) {
         throw new CommonException("网站尚未开通完成", new Object[0]);
      } else {
         Map<String, Object> master = ((MemberVhostMapper)this.baseMapper).selectMasterById(host.getMasterId());
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

   private void fillPackage(List<MemberVhost> hosts) {
      if (hosts != null && !hosts.isEmpty()) {
         List<String> versionIds = hosts.stream().map(MemberVhost::getVersionId).filter(CharSequenceUtil::isNotBlank).distinct().collect(Collectors.toList());
         if (!versionIds.isEmpty()) {
            List<Map<String, Object>> rows = ((MemberVhostMapper)this.baseMapper).selectVersionByIds(versionIds);
            if (rows != null && !rows.isEmpty()) {
               Map<String, Map<String, Object>> versions = rows.stream()
                  .filter(Objects::nonNull)
                  .filter(item -> StrUtil.isNotBlank(this.text((Map<String, Object>)item, "id")))
                  .collect(Collectors.toMap(item -> this.text((Map<String, Object>)item, "id"), item -> item, (left, right) -> left));

               for (MemberVhost host : hosts) {
                  this.fillPackage(host, versions.get(host.getVersionId()));
               }
            }
         }
      }
   }

   private void fillPackage(MemberVhost host) {
      if (host != null && !StrUtil.isBlank(host.getVersionId())) {
         this.fillPackage(host, ((MemberVhostMapper)this.baseMapper).selectVersionById(host.getVersionId()));
      }
   }

   private void fillPackage(MemberVhost host, Map<String, Object> version) {
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
      return StrUtil.isBlank(versionId) ? null : this.text(((MemberVhostMapper)this.baseMapper).selectVersionById(versionId), "phpVersions");
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

   private void markPhpVersions(List<VhostPhpVersion> versions, MemberVhost host) {
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
               (Wrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)((LambdaQueryWrapper<MemberVhost>)new LambdaQueryWrapper<MemberVhost>().eq(MemberVhost::getMasterId, masterId))
                     .ne(StrUtil.isNotBlank(excludeHostId), MemberVhost::getId, excludeHostId))
                  .ne(MemberVhost::getBusinessStatus, BusinessStatusEnum.DELETE.getCode())
            )
            .stream()
            .anyMatch(item -> VhostOrderOptionSupport.containsDomain(item.getBindDomains(), domain))
         : false;
   }

   private Date resolveDeleteTime(Date endTime) {
      if (endTime == null) {
         return null;
      } else {
         String retention = ((DevConfigApi)SpringUtil.getBean(DevConfigApi.class)).getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
         int days = 3;
         if (StrUtil.isNotBlank(retention)) {
            days = Integer.parseInt(retention.trim());
         }

         return DateUtil.offsetDay(endTime, days);
      }
   }

   private String firstDomain(MemberVhost host) {
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
}
