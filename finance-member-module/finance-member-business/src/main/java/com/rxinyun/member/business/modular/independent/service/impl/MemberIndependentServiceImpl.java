package com.rxinyun.member.business.modular.independent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.business.api.IndependentApi;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.OperateTypeEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.OsInstancePasswordValidator;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordIdParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerTransferRecordService;
import com.rxinyun.member.business.modular.independent.entity.MemberIndependent;
import com.rxinyun.member.business.modular.independent.mapper.MemberIndependentMapper;
import com.rxinyun.member.business.modular.independent.param.IndependentWorkAddParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentAddParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIdListParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentIdParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentPageParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentPasswordEditParam;
import com.rxinyun.member.business.modular.independent.param.MemberIndependentRenewAutoParam;
import com.rxinyun.member.business.modular.independent.param.MemberRemarkEditParam;
import com.rxinyun.member.business.modular.independent.param.ReinstallEditParam;
import com.rxinyun.member.business.modular.independent.response.MemberIndependentResponse;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentIpService;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentService;
import com.rxinyun.member.business.modular.independent.service.MemberIndependentWorkService;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.api.IndependentRegionApi;
import com.rxinyun.production.api.MemberIndependentSystemVersionApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import com.rxinyun.production.core.pojo.BaseIndependentRegion;
import com.rxinyun.production.core.pojo.BaseMemberIndependentSystemVersion;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberIndependentServiceImpl extends ServiceImpl<MemberIndependentMapper, MemberIndependent> implements MemberIndependentService {
   private static final Logger log = LoggerFactory.getLogger(MemberIndependentServiceImpl.class);
   private static final String LOCKED_OPERATION_MESSAGE = "独立服务器已锁定，请先解锁后再操作";
   @Resource
   private AdminMachineApi adminMachineApi;
   @Resource
   private MemberIndependentWorkService memberIndependentWorkService;
   @Resource
   private MemberIndependentSystemVersionApi memberIndependentSystemVersionApi;
   @Resource
   private IndependentLineApi independentLineApi;
   @Resource
   private IndependentRegionApi independentRegionApi;
   @Resource
   private MemberServerTransferRecordService memberServerTransferRecordService;
   @Resource
   private MemberIndependentIpService memberIndependentIpService;
   @Resource
   private IndependentApi independentApi;

   private boolean isResourcePoolIndependent(MemberIndependent s) {
      if (s == null) {
         return false;
      } else {
         return Integer.valueOf(1).equals(s.getType()) ? true : StrUtil.isNotBlank(s.getResourcePoolId());
      }
   }

   private void assertIndependentNotLocked(MemberIndependent server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("独立服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   @Override
   public Page<MemberIndependentResponse> page(MemberIndependentPageParam pageParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      QueryWrapper<MemberIndependent> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(MemberIndependent::getMemberNo, clientLoginUser.getId());
      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().and(q -> q.eq(MemberIndependent::getServerStatus, pageParam.getStatus()));
      }

      if (StrUtil.isNotBlank(pageParam.getServerName())) {
         queryWrapper.lambda().and(q -> q.like(MemberIndependent::getServerName, pageParam.getServerName()));
      } else if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(MemberIndependent::getServerName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getBusinessStatus())) {
         queryWrapper.lambda().and(q -> q.eq(MemberIndependent::getBusinessStatus, pageParam.getBusinessStatus()));
      }

      if (StrUtil.isNotBlank(pageParam.getServerIp())) {
         List<String> serverIds = this.memberIndependentIpService.listServerIdByIp(pageParam.getServerIp());
         if (serverIds.isEmpty()) {
            queryWrapper.lambda().and(q -> q.eq(MemberIndependent::getId, ""));
         } else {
            queryWrapper.lambda().and(q -> q.in(MemberIndependent::getId, serverIds));
         }
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<MemberIndependent> page = (Page<MemberIndependent>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<MemberIndependentResponse> pageResultResponse = new Page();
      if (CollectionUtils.isNotEmpty(page.getRecords())) {
         List<MemberIndependentResponse> responseList = page.getRecords().stream().map(memberIndependent -> {
            MemberIndependentResponse memberIndependentResponse = new MemberIndependentResponse();
            BeanUtils.copyProperties(memberIndependent, memberIndependentResponse);
            if (memberIndependent.getRegionId() != null) {
               BaseIndependentRegion memberIndependentRegion = this.independentRegionApi.selectByRegionId(memberIndependent.getRegionId());
               memberIndependentResponse.setRegionName(memberIndependentRegion.getName());
            }

            if (memberIndependent.getLineId() != null) {
               BaseIndependentLine baseIndependentLine = this.independentLineApi.selectByLineId(memberIndependent.getLineId());
               if (baseIndependentLine != null) {
                  memberIndependentResponse.setLineName(baseIndependentLine.getName());
               }
            }

            return memberIndependentResponse;
         }).collect(Collectors.toList());
         pageResultResponse.setRecords(responseList);
      }

      pageResultResponse.setCurrent(page.getCurrent());
      pageResultResponse.setSize(page.getSize());
      pageResultResponse.setTotal(page.getTotal());
      pageResultResponse.setPages(page.getPages());
      return pageResultResponse;
   }

   @Override
   public List<MemberIndependent> list(MemberIndependentIdListParam memberIndependentIdListParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      return this.list(
         (Wrapper<MemberIndependent>)((LambdaQueryWrapper<MemberIndependent>)new QueryWrapper<MemberIndependent>().lambda().eq(MemberIndependent::getMemberNo, clientLoginUser.getId()))
            .in(MemberIndependent::getId, memberIndependentIdListParam.getServerIds())
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(MemberIndependentAddParam memberServerAddParam) {
      try {
         OsInstancePasswordValidator.validateOrThrow(memberServerAddParam.getSystemPassword());
         BaseAdminMachine machine = this.adminMachineApi.findMachineById(memberServerAddParam.getMachineId());
         if (machine == null) {
            throw new CommonException("该机型不存在", new Object[0]);
         } else {
            MemberIndependent memberIndependent = (MemberIndependent)BeanUtil.toBean(memberServerAddParam, MemberIndependent.class);
            memberIndependent.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
            memberIndependent.setServerStatus(ServerStatusEnum.CREATING.getCode());
            DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
            String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
            memberIndependent.setDeleteTime(DateUtil.offsetDay(memberServerAddParam.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
            this.saveOrUpdate(memberIndependent);
            if (memberIndependent.getId() == null) {
               throw new CommonException("ID 未生成", new Object[0]);
            } else {
               memberServerAddParam.setId(memberIndependent.getId());
               log.info("Saved memberServer with ID: {}", memberIndependent.getId());
               log.info("创建独立服务器开始，HOST_NAME:{}", memberServerAddParam.getServerName());
               IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
               independentWorkAddParam.setLineId(memberServerAddParam.getLineId());
               independentWorkAddParam.setRegionId(memberServerAddParam.getRegionId());
               independentWorkAddParam.setMemberNo(memberServerAddParam.getMemberNo());
               independentWorkAddParam.setIndependentId(memberIndependent.getId());
               independentWorkAddParam.setOperateType(OperateTypeEnum.CREATE.getCode());
               independentWorkAddParam.setBusinessStatus(0);
               independentWorkAddParam.setSubmitTime(new Date());
               independentWorkAddParam.setExtJson(JSON.toJSONString(memberServerAddParam));
               log.info("创建独立服务器任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
               this.memberIndependentWorkService.add(independentWorkAddParam);
            }
         }
      } catch (CommonException var7) {
         log.error("创建独立服务器失败: {}", var7.getMessage(), var7);
         throw var7;
      } catch (Exception var8) {
         log.error("创建独立服务器时发生未知错误: {}", var8.getMessage(), var8);
         throw new CommonException("创建独立服务器失败", new Object[]{var8.getMessage()});
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void savePoolPendingServer(MemberIndependentAddParam memberServerAddParam) {
      OsInstancePasswordValidator.validateOrThrow(memberServerAddParam.getSystemPassword());
      BaseAdminMachine machine = this.adminMachineApi.findMachineById(memberServerAddParam.getMachineId());
      if (machine == null) {
         throw new CommonException("该机型不存在", new Object[0]);
      } else {
         MemberIndependent memberIndependent = (MemberIndependent)BeanUtil.toBean(memberServerAddParam, MemberIndependent.class);
         if (memberIndependent.getType() == null) {
            memberIndependent.setType(1);
         }

         if (StrUtil.isBlank(memberIndependent.getResourcePoolId()) && machine.getResourcePoolId() != null) {
            memberIndependent.setResourcePoolId(machine.getResourcePoolId());
         }

         memberIndependent.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
         memberIndependent.setServerStatus(ServerStatusEnum.CREATING.getCode());
         DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
         String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
         memberIndependent.setDeleteTime(DateUtil.offsetDay(memberServerAddParam.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
         this.saveOrUpdate(memberIndependent);
         if (memberIndependent.getId() == null) {
            throw new CommonException("ID 未生成", new Object[0]);
         } else {
            log.info("资源池独立服务器占位记录已保存，id={} name={}", memberIndependent.getId(), memberIndependent.getServerName());
         }
      }
   }

   @Override
   public MemberIndependentResponse detail(MemberIndependentIdParam serverIdParam) {
      MemberIndependent memberIndependent = this.queryEntity(serverIdParam.getId());
      if (null == memberIndependent) {
         throw new CommonException("该独立服务器不存在", new Object[0]);
      } else {
         MemberIndependentResponse memberIndependentResponse = (MemberIndependentResponse)BeanUtil.toBean(memberIndependent, MemberIndependentResponse.class);
         if (memberIndependent.getLineId() != null) {
            BaseIndependentLine baseIndependentLine = this.independentLineApi.selectByLineId(memberIndependent.getLineId());
            if (baseIndependentLine != null) {
               memberIndependentResponse.setLineName(baseIndependentLine.getName());
            }
         }

         if (memberIndependent.getRegionId() != null) {
            BaseIndependentRegion baseIndependentRegion = this.independentRegionApi.selectByRegionId(memberIndependent.getRegionId());
            if (baseIndependentRegion != null) {
               memberIndependentResponse.setRegionName(baseIndependentRegion.getName());
            }
         }

         return memberIndependentResponse;
      }
   }

   @Override
   public MemberIndependent queryEntity(String id) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberIndependent memberIndependent = (MemberIndependent)this.getOne(
         (Wrapper<MemberIndependent>)((LambdaQueryWrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getMemberNo, clientLoginUser.getId())).eq(MemberIndependent::getId, id)
      );
      if (ObjectUtil.isEmpty(memberIndependent)) {
         throw new CommonException("云服务器不存在，id值为：{}", new Object[]{id});
      } else {
         return memberIndependent;
      }
   }

   @Override
   public void editRemark(MemberRemarkEditParam memberRemarkEditParam) {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      MemberIndependent memberServer = (MemberIndependent)this.getOne(
         (Wrapper<MemberIndependent>)((LambdaQueryWrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getMemberNo, clientLoginUser.getId()))
            .eq(MemberIndependent::getId, memberRemarkEditParam.getId())
      );
      if (ObjectUtil.isEmpty(memberServer)) {
         throw new CommonException("云服务器不存在，id值为：{}", new Object[]{memberRemarkEditParam.getId()});
      } else {
         memberServer.setMemberRemark(memberRemarkEditParam.getMemberRemark());
         this.updateById(memberServer);
      }
   }

   @Override
   public CommonResult updatePasswordServer(MemberIndependentPasswordEditParam passwordEditParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         String pwdErr = OsInstancePasswordValidator.validate(passwordEditParam.getOsPassword());
         if (pwdErr != null) {
            return CommonResult.error(pwdErr);
         } else {
            MemberIndependent server = this.queryEntity(passwordEditParam.getId());
            if (null == server) {
               return CommonResult.error("服务器不存在");
            } else if (!server.getMemberNo().equals(clientLoginUser.getId())) {
               throw new CommonException("您没有权限操作该服务器", new Object[0]);
            } else {
               BaseIndependentLine baseIndependentLine = null;
               if (server.getLineId() != null) {
                  baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
               }

               if (this.isResourcePoolIndependent(server)) {
                  return this.independentApi.forwardPoolIndependentUpdatePassword(clientLoginUser.getId(), server.getId(), passwordEditParam.getOsPassword());
               } else {
                  server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
                  server.setSystemPassword(passwordEditParam.getOsPassword());
                  this.updateById(server);
                  IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
                  independentWorkAddParam.setLineId(server.getLineId());
                  independentWorkAddParam.setRegionId(server.getRegionId());
                  independentWorkAddParam.setMemberNo(server.getMemberNo());
                  independentWorkAddParam.setIndependentId(server.getId());
                  independentWorkAddParam.setOperateType(OperateTypeEnum.RESET_PASSWORD.getCode());
                  independentWorkAddParam.setBusinessStatus(0);
                  independentWorkAddParam.setSubmitTime(new Date());
                  Map<String, Object> extJson = new HashMap<>();
                  extJson.put("serverId", server.getId());
                  extJson.put("serverIp", server.getServerIp());
                  extJson.put("lineId", server.getLineId());
                  if (baseIndependentLine != null) {
                     extJson.put("lineName", baseIndependentLine.getName());
                  }

                  extJson.put("osPassword", passwordEditParam.getOsPassword());
                  independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
                  log.info("独立服务器升级带宽任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
                  this.memberIndependentWorkService.add(independentWorkAddParam);
                  return CommonResult.ok("密码修改任务已创建");
               }
            }
         }
      } catch (Exception var8) {
         log.error("修改服务器密码失败", var8);
         return CommonResult.error(var8.getMessage());
      }
   }

   @Override
   public CommonResult upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      try {
         MemberIndependent server = (MemberIndependent)this.getOne((Wrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(server);
            BaseIndependentLine baseIndependentLine = null;
            if (server.getLineId() != null) {
               baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
            }

            Integer oldBandwidth = server.getBandwidth();
            server.setServerStatus(ServerStatusEnum.UPGRADE.getCode());
            server.setBandwidth(bandwidth);
            server.setRenewPrice(renewPrice);
            this.updateById(server);
            IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
            independentWorkAddParam.setLineId(server.getLineId());
            independentWorkAddParam.setRegionId(server.getRegionId());
            independentWorkAddParam.setMemberNo(server.getMemberNo());
            independentWorkAddParam.setIndependentId(server.getId());
            independentWorkAddParam.setOperateType(OperateTypeEnum.UPGRADE.getCode());
            independentWorkAddParam.setBusinessStatus(0);
            independentWorkAddParam.setSubmitTime(new Date());
            Map<String, Object> extJson = new HashMap<>();
            extJson.put("orderNo", orderNo);
            extJson.put("serverId", serverId);
            extJson.put("serverIp", server.getServerIp());
            extJson.put("lineId", server.getLineId());
            if (baseIndependentLine != null) {
               extJson.put("lineName", baseIndependentLine.getName());
            }

            extJson.put("bandwidth", bandwidth - oldBandwidth);
            extJson.put("renewPrice", renewPrice);
            independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
            log.info("独立服务器升级带宽任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
            this.memberIndependentWorkService.add(independentWorkAddParam);
            return CommonResult.ok("升级带宽任务已创建");
         }
      } catch (Exception var10) {
         log.error("升级服务器带宽失败", var10);
         return CommonResult.error(var10.getMessage());
      }
   }

   @Override
   public CommonResult upgradeIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice) {
      try {
         MemberIndependent server = (MemberIndependent)this.getOne((Wrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(server);
            BaseIndependentLine baseIndependentLine = null;
            if (server.getLineId() != null) {
               baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
            }

            Integer oldIpSize = server.getIpSize();
            server.setServerStatus(ServerStatusEnum.UPGRADE.getCode());
            server.setIpSize(ipSize);
            server.setRenewPrice(renewPrice);
            this.updateById(server);
            IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
            independentWorkAddParam.setLineId(server.getLineId());
            independentWorkAddParam.setRegionId(server.getRegionId());
            independentWorkAddParam.setMemberNo(server.getMemberNo());
            independentWorkAddParam.setIndependentId(server.getId());
            independentWorkAddParam.setOperateType(OperateTypeEnum.UPGRADE.getCode());
            independentWorkAddParam.setBusinessStatus(0);
            independentWorkAddParam.setSubmitTime(new Date());
            Map<String, Object> extJson = new HashMap<>();
            extJson.put("orderNo", orderNo);
            extJson.put("serverId", serverId);
            extJson.put("serverIp", server.getServerIp());
            extJson.put("lineId", server.getLineId());
            if (baseIndependentLine != null) {
               extJson.put("lineName", baseIndependentLine.getName());
            }

            extJson.put("ipSize", ipSize - oldIpSize);
            extJson.put("renewPrice", renewPrice);
            independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
            log.info("独立服务器新增IP任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
            this.memberIndependentWorkService.add(independentWorkAddParam);
            return CommonResult.ok("升级IP任务已创建");
         }
      } catch (Exception var10) {
         log.error("升级服务器核心失败", var10);
         return CommonResult.error(var10.getMessage());
      }
   }

   @Override
   public CommonResult renewServer(String orderNo, String serverId, Integer months, String unit) {
      try {
         MemberIndependent server = (MemberIndependent)this.getOne((Wrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getId, serverId));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(server);
            BaseIndependentLine baseIndependentLine = null;
            if (server.getLineId() != null) {
               baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
            }

            server.setEndTime(DateUtil.offsetMonth(server.getEndTime(), months));
            DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
            String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
            log.info("云服务器保留天数：{}", cloudServerRetentionPeriod);
            server.setDeleteTime(DateUtil.offsetDay(server.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
            if (BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus())) {
               server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
            }

            this.updateById(server);
            IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
            independentWorkAddParam.setLineId(server.getLineId());
            independentWorkAddParam.setRegionId(server.getRegionId());
            independentWorkAddParam.setMemberNo(server.getMemberNo());
            independentWorkAddParam.setIndependentId(server.getId());
            independentWorkAddParam.setOperateType(OperateTypeEnum.RECHARGE.getCode());
            independentWorkAddParam.setBusinessStatus(0);
            independentWorkAddParam.setSubmitTime(new Date());
            Map<String, Object> extJson = new HashMap<>();
            extJson.put("orderNo", orderNo);
            extJson.put("serverId", serverId);
            extJson.put("serverIp", server.getServerIp());
            extJson.put("lineId", server.getLineId());
            if (baseIndependentLine != null) {
               extJson.put("lineName", baseIndependentLine.getName());
            }

            extJson.put("duration", months);
            extJson.put("unit", unit);
            extJson.put("renewPrice", server.getRenewPrice());
            independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
            log.info("独立服务器续费任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
            this.memberIndependentWorkService.add(independentWorkAddParam);
            this.memberIndependentWorkService.closePendingExpireStopWorks(server.getId());
            return CommonResult.ok();
         }
      } catch (CommonException var11) {
         log.error("服务器续费失败: {}", var11.getMessage(), var11);
         return CommonResult.error(var11.getMessage());
      } catch (Exception var12) {
         log.error("服务器续费失败", var12);
         return CommonResult.error("系统错误");
      }
   }

   @Override
   public CommonResult upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      try {
         MemberIndependent server = (MemberIndependent)this.getOne((Wrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(server);
            BaseIndependentLine baseIndependentLine = null;
            if (server.getLineId() != null) {
               baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
            }

            if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else {
               Integer oldDefense = server.getDefense();
               server.setServerStatus(ServerStatusEnum.UPGRADE.getCode());
               server.setDefense(defense);
               server.setRenewPrice(renewPrice);
               this.updateById(server);
               IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
               independentWorkAddParam.setLineId(server.getLineId());
               independentWorkAddParam.setRegionId(server.getRegionId());
               independentWorkAddParam.setMemberNo(server.getMemberNo());
               independentWorkAddParam.setIndependentId(server.getId());
               independentWorkAddParam.setOperateType(OperateTypeEnum.UPGRADE.getCode());
               independentWorkAddParam.setBusinessStatus(0);
               independentWorkAddParam.setSubmitTime(new Date());
               Map<String, Object> extJson = new HashMap<>();
               extJson.put("orderNo", orderNo);
               extJson.put("serverId", serverId);
               extJson.put("serverIp", server.getServerIp());
               extJson.put("lineId", server.getLineId());
               if (baseIndependentLine != null) {
                  extJson.put("lineName", baseIndependentLine.getName());
               }

               extJson.put("defense", defense - oldDefense);
               extJson.put("renewPrice", server.getRenewPrice());
               independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
               log.info("独立服务器升级防御任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
               this.memberIndependentWorkService.add(independentWorkAddParam);
               return CommonResult.ok("升级防御任务已创建");
            }
         }
      } catch (Exception var10) {
         log.error("升级服务器防御失败", var10);
         return CommonResult.error(var10.getMessage());
      }
   }

   @Override
   public CommonResult startServer(MemberIndependentIdParam memberIndependentIdParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberIndependent server = this.queryEntity(memberIndependentIdParam.getId());
         if (!server.getMemberNo().equals(clientLoginUser.getId())) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else if (server.getServerStatus().equals(ServerStatusEnum.CREATING.getCode())) {
            throw new CommonException("服务器创建中，请等待......", new Object[0]);
         } else if (server.getServerStatus().equals(ServerStatusEnum.LOCKED.getCode())) {
            throw new CommonException("独立服务器已锁定，请先解锁后再操作", new Object[0]);
         } else if (server.getServerStatus().equals(ServerStatusEnum.REINSTALLING.getCode())) {
            throw new CommonException("服务器重装系统中，请等待......", new Object[0]);
         } else {
            BaseIndependentLine baseIndependentLine = null;
            if (server.getLineId() != null) {
               baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
            }

            if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else if (this.isResourcePoolIndependent(server)) {
               return this.independentApi.forwardPoolIndependentStart(clientLoginUser.getId(), server.getId());
            } else {
               server.setServerStatus(ServerStatusEnum.STARTING.getCode());
               this.updateById(server);
               IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
               independentWorkAddParam.setLineId(server.getLineId());
               independentWorkAddParam.setRegionId(server.getRegionId());
               independentWorkAddParam.setMemberNo(server.getMemberNo());
               independentWorkAddParam.setIndependentId(server.getId());
               independentWorkAddParam.setOperateType(OperateTypeEnum.START.getCode());
               independentWorkAddParam.setBusinessStatus(0);
               independentWorkAddParam.setSubmitTime(new Date());
               Map<String, Object> extJson = new HashMap<>();
               extJson.put("serverId", server.getId());
               extJson.put("serverIp", server.getServerIp());
               extJson.put("lineId", server.getLineId());
               if (baseIndependentLine != null) {
                  extJson.put("lineName", baseIndependentLine.getName());
               }

               independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
               log.info("独立服务器开机任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
               this.memberIndependentWorkService.add(independentWorkAddParam);
               return CommonResult.ok("启动任务已创建");
            }
         }
      } catch (Exception var7) {
         log.error("启动独立服务器失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult closeServer(MemberIndependentIdParam memberIndependentIdParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberIndependent server = this.queryEntity(memberIndependentIdParam.getId());
         if (!server.getMemberNo().equals(clientLoginUser.getId())) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else {
            this.assertIndependentNotLocked(server);
            if (!server.getServerStatus().equals(ServerStatusEnum.RUNNING.getCode())) {
               throw new CommonException("服务器状态异常，无法关闭", new Object[0]);
            } else if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else if (this.isResourcePoolIndependent(server)) {
               return this.independentApi.forwardPoolIndependentStop(clientLoginUser.getId(), server.getId());
            } else {
               BaseIndependentLine baseIndependentLine = null;
               if (server.getLineId() != null) {
                  baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
               }

               server.setServerStatus(ServerStatusEnum.STOPPING.getCode());
               this.updateById(server);
               IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
               independentWorkAddParam.setLineId(server.getLineId());
               independentWorkAddParam.setRegionId(server.getRegionId());
               independentWorkAddParam.setMemberNo(server.getMemberNo());
               independentWorkAddParam.setIndependentId(server.getId());
               independentWorkAddParam.setOperateType(OperateTypeEnum.STOP.getCode());
               independentWorkAddParam.setBusinessStatus(0);
               independentWorkAddParam.setSubmitTime(new Date());
               Map<String, Object> extJson = new HashMap<>();
               extJson.put("serverId", server.getId());
               extJson.put("serverIp", server.getServerIp());
               extJson.put("lineId", server.getLineId());
               if (baseIndependentLine != null) {
                  extJson.put("lineName", baseIndependentLine.getName());
               }

               extJson.put("memberNo", server.getMemberNo());
               independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
               log.info("独立服务器关机任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
               this.memberIndependentWorkService.add(independentWorkAddParam);
               return CommonResult.ok("关闭任务已创建");
            }
         }
      } catch (Exception var7) {
         log.error("关闭独立服务器失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult reinstallServer(ReinstallEditParam reinstallEditParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         log.info("登录用户信息：{}", clientLoginUser);
         String pwdErr = OsInstancePasswordValidator.validate(reinstallEditParam.getOsPassword());
         if (pwdErr != null) {
            return CommonResult.error(pwdErr);
         } else {
            MemberIndependent server = (MemberIndependent)this.getOne(
               (Wrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getId, reinstallEditParam.getId())
            );
            if (null == server) {
               return CommonResult.error("服务器不存在");
            } else if (!server.getMemberNo().equals(clientLoginUser.getId())) {
               throw new CommonException("您没有权限操作该服务器", new Object[0]);
            } else {
               this.assertIndependentNotLocked(server);
               if (this.isExistWorkingTask(server.getId())) {
                  return CommonResult.error("服务器存在未处理的任务，请等待......");
               } else {
                  BaseIndependentLine baseIndependentLine = null;
                  if (server.getLineId() != null) {
                     baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
                  }

                  BaseMemberIndependentSystemVersion memberOsVersion = this.memberIndependentSystemVersionApi
                     .selectByOsVersionId(reinstallEditParam.getOsVersionId());
                  if (null == memberOsVersion) {
                     return CommonResult.error("操作系统版本信息不存在");
                  } else if (this.isResourcePoolIndependent(server)) {
                     return this.independentApi
                        .forwardPoolIndependentReinstall(
                           clientLoginUser.getId(),
                           server.getId(),
                           reinstallEditParam.getOsClassId(),
                           reinstallEditParam.getOsVersionId(),
                           reinstallEditParam.getOsPassword()
                        );
                  } else {
                     server.setServerStatus(ServerStatusEnum.REINSTALLING.getCode());
                     server.setSystemClassId(reinstallEditParam.getOsClassId());
                     server.setSystemId(reinstallEditParam.getOsVersionId());
                     server.setSystemClass(memberOsVersion.getOsClassName());
                     server.setSystemName(memberOsVersion.getOsName());
                     server.setSystemUserName(memberOsVersion.getDefaultUserName());
                     server.setPort(memberOsVersion.getDefaultPort());
                     server.setSystemPassword(reinstallEditParam.getOsPassword());
                     this.updateById(server);
                     IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
                     independentWorkAddParam.setLineId(server.getLineId());
                     independentWorkAddParam.setRegionId(server.getRegionId());
                     independentWorkAddParam.setMemberNo(server.getMemberNo());
                     independentWorkAddParam.setIndependentId(server.getId());
                     independentWorkAddParam.setOperateType(OperateTypeEnum.REINSTALL.getCode());
                     independentWorkAddParam.setBusinessStatus(0);
                     independentWorkAddParam.setSubmitTime(new Date());
                     Map<String, Object> extJson = new HashMap<>();
                     extJson.put("serverId", server.getId());
                     extJson.put("serverIp", server.getServerIp());
                     extJson.put("lineId", server.getLineId());
                     if (baseIndependentLine != null) {
                        extJson.put("lineName", baseIndependentLine.getName());
                     }

                     extJson.put("osClassId", reinstallEditParam.getOsClassId());
                     extJson.put("osVersionId", reinstallEditParam.getOsVersionId());
                     extJson.put("osName", memberOsVersion.getOsName());
                     extJson.put("osPassword", reinstallEditParam.getOsPassword());
                     extJson.put("osClassName", memberOsVersion.getOsClassName());
                     independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
                     log.info("独立服务器重装系统任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
                     this.memberIndependentWorkService.add(independentWorkAddParam);
                     return CommonResult.ok("重装任务已创建");
                  }
               }
            }
         }
      } catch (Exception var9) {
         log.error("重装服务器失败", var9);
         return CommonResult.error(var9.getMessage());
      }
   }

   private boolean isExistWorkingTask(String independentId) {
      return this.memberIndependentWorkService.isExistWorkingTask(independentId);
   }

   @Override
   public CommonResult powerServer(MemberIndependentIdParam serverIdParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberIndependent server = (MemberIndependent)this.getOne((Wrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getId, serverIdParam.getId()));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else if (!server.getMemberNo().equals(clientLoginUser.getId())) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else {
            this.assertIndependentNotLocked(server);
            if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else if (this.isResourcePoolIndependent(server)) {
               return this.independentApi.forwardPoolIndependentPower(clientLoginUser.getId(), server.getId());
            } else {
               BaseIndependentLine baseIndependentLine = null;
               if (server.getLineId() != null) {
                  baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
               }

               server.setServerStatus(ServerStatusEnum.POWERING.getCode());
               this.updateById(server);
               IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
               independentWorkAddParam.setLineId(server.getLineId());
               independentWorkAddParam.setRegionId(server.getRegionId());
               independentWorkAddParam.setMemberNo(server.getMemberNo());
               independentWorkAddParam.setIndependentId(server.getId());
               independentWorkAddParam.setOperateType(OperateTypeEnum.SHUT_DOWN.getCode());
               independentWorkAddParam.setBusinessStatus(0);
               independentWorkAddParam.setSubmitTime(new Date());
               Map<String, Object> extJson = new HashMap<>();
               extJson.put("serverId", server.getId());
               extJson.put("serverIp", server.getServerIp());
               extJson.put("lineId", server.getLineId());
               if (baseIndependentLine != null) {
                  extJson.put("lineName", baseIndependentLine.getName());
               }

               independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
               log.info("独立服务器重装系统任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
               this.memberIndependentWorkService.add(independentWorkAddParam);
               return CommonResult.ok("断开电源任务已创建");
            }
         }
      } catch (Exception var7) {
         log.error("断开服务器电源失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public CommonResult restartServer(MemberIndependentIdParam serverIdParam) {
      try {
         SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
         MemberIndependent server = (MemberIndependent)this.getOne((Wrapper<MemberIndependent>)new LambdaQueryWrapper<MemberIndependent>().eq(MemberIndependent::getId, serverIdParam.getId()));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else if (!server.getMemberNo().equals(clientLoginUser.getId())) {
            throw new CommonException("您没有权限操作该服务器", new Object[0]);
         } else {
            this.assertIndependentNotLocked(server);
            if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else if (this.isResourcePoolIndependent(server)) {
               return this.independentApi.forwardPoolIndependentRestart(clientLoginUser.getId(), server.getId());
            } else {
               BaseIndependentLine baseIndependentLine = null;
               if (server.getLineId() != null) {
                  baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
               }

               server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
               this.updateById(server);
               IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
               independentWorkAddParam.setLineId(server.getLineId());
               independentWorkAddParam.setRegionId(server.getRegionId());
               independentWorkAddParam.setMemberNo(server.getMemberNo());
               independentWorkAddParam.setIndependentId(server.getId());
               independentWorkAddParam.setOperateType(OperateTypeEnum.REBOOT.getCode());
               independentWorkAddParam.setBusinessStatus(0);
               independentWorkAddParam.setSubmitTime(new Date());
               Map<String, Object> extJson = new HashMap<>();
               extJson.put("serverId", server.getId());
               extJson.put("serverIp", server.getServerIp());
               extJson.put("lineId", server.getLineId());
               if (baseIndependentLine != null) {
                  extJson.put("lineName", baseIndependentLine.getName());
               }

               independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
               log.info("独立服务器重启系统任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
               this.memberIndependentWorkService.add(independentWorkAddParam);
               return CommonResult.ok("重启任务已创建");
            }
         }
      } catch (Exception var7) {
         log.error("重启服务器失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public void isRenew(MemberIndependentRenewAutoParam param) {
      if (param != null && !StrUtil.isBlank(param.getId()) && param.getRenewAuto() != null) {
         if (param.getRenewAuto() != 0 && param.getRenewAuto() != 1) {
            throw new CommonException("自动续费开关只能为0或1", new Object[0]);
         } else {
            SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
            MemberIndependent server = (MemberIndependent)this.getById(param.getId());
            if (server == null) {
               throw new CommonException("服务器不存在", new Object[0]);
            } else if (server.getMemberNo() == null || !server.getMemberNo().equals(clientLoginUser.getId())) {
               throw new CommonException("无权操作该服务器", new Object[0]);
            } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
               throw new CommonException("已删除服务器不支持修改自动续费", new Object[0]);
            } else if (Integer.valueOf(1).equals(param.getRenewAuto()) && BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus())) {
               throw new CommonException("服务器已过期，无法开启自动续费", new Object[0]);
            } else {
               this.update(
                  (Wrapper<MemberIndependent>)((LambdaUpdateWrapper<MemberIndependent>)new LambdaUpdateWrapper<MemberIndependent>().eq(MemberIndependent::getId, param.getId()))
                     .set(MemberIndependent::getRenewAuto, param.getRenewAuto())
               );
            }
         }
      } else {
         throw new CommonException("参数不能为空", new Object[0]);
      }
   }

   @Override
   public CommonResult plus(MemberServerTransferRecordAddParam memberServerTransferRecordAddParam) {
      memberServerTransferRecordAddParam.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
      return this.memberServerTransferRecordService.add(memberServerTransferRecordAddParam);
   }

   @Override
   public CommonResult updateStatus(MemberServerTransferRecordIdParam memberServerTransferRecordEditParam) {
      return this.memberServerTransferRecordService.updateStatus(memberServerTransferRecordEditParam);
   }
}
