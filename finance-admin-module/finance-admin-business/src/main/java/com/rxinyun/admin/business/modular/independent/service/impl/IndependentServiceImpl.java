package com.rxinyun.admin.business.modular.independent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.esc.util.ResourcePoolUpstreamDataApplier;
import com.rxinyun.admin.business.modular.esc.util.ResourcePoolUpstreamIndependentClient;
import com.rxinyun.admin.business.modular.esc.util.ResourcePoolUpstreamSyncClient;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import com.rxinyun.admin.business.modular.independent.mapper.IndependentIpMapper;
import com.rxinyun.admin.business.modular.independent.mapper.IndependentMapper;
import com.rxinyun.admin.business.modular.independent.mapper.OrderServerLinkMapper;
import com.rxinyun.admin.business.modular.independent.param.IndependentAddEndDaysParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentAddParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentEditParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentEndTimeParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIdListParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentIdParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentLockParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentPageParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentPasswordEditParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentReinstallEditParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentRenewAutoParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentRenewPriceParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkAddParam;
import com.rxinyun.admin.business.modular.independent.param.RemarkEditParam;
import com.rxinyun.admin.business.modular.independent.response.IndependentResponse;
import com.rxinyun.admin.business.modular.independent.service.IndependentIpService;
import com.rxinyun.admin.business.modular.independent.service.IndependentService;
import com.rxinyun.admin.business.modular.independent.service.IndependentWorkService;
import com.rxinyun.business.core.pojo.BaseIndependent;
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
import com.rxinyun.dev.api.ResourcePoolApi;
import com.rxinyun.dev.core.pojo.BaseResourcePool;
import com.rxinyun.dev.core.pojo.BaseResourcePoolRequest;
import com.rxinyun.production.admin.IndependentSystemVersionApi;
import com.rxinyun.production.admin.core.pojo.BaseIndependentSystemVersion;
import com.rxinyun.production.api.AdminMachineApi;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.api.IndependentRegionApi;
import com.rxinyun.production.core.pojo.BaseAdminMachine;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import com.rxinyun.production.core.pojo.BaseIndependentRegion;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndependentServiceImpl extends ServiceImpl<IndependentMapper, Independent> implements IndependentService {
   private static final Logger log = LoggerFactory.getLogger(IndependentServiceImpl.class);
   private static final String LOCKED_OPERATION_MESSAGE = "独立服务器已锁定，请先解锁后再操作";
   @Resource
   private IndependentMapper independentMapper;
   @Resource
   private OrderServerLinkMapper orderServerLinkMapper;
   @Resource
   private IndependentIpMapper independentIpMapper;
   @Resource
   private IndependentWorkService independentWorkService;
   @Resource
   private IndependentLineApi independentLineApi;
   @Resource
   private IndependentRegionApi independentRegionApi;
   @Resource
   private IndependentIpService independentIpService;
   @Resource
   private RedisTemplate<String, Object> redisTemplate;
   @Resource
   private ResourcePoolApi resourcePoolApi;
   @Resource
   private ResourcePoolUpstreamIndependentClient poolUpstreamIndependentClient;
   @Resource
   private ResourcePoolUpstreamSyncClient resourcePoolUpstreamSyncClient;
   @Autowired
   private AdminMachineApi adminMachineApi;
   @Resource
   private IndependentSystemVersionApi independentSystemVersionApi;

   @Override
   public Page<IndependentResponse> page(IndependentPageParam pageParam) {
      QueryWrapper<Independent> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getId())) {
         queryWrapper.lambda().and(q -> q.eq(Independent::getId, pageParam.getId()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getStatus())) {
         queryWrapper.lambda().and(q -> q.eq(Independent::getServerStatus, pageParam.getStatus()));
      }

      if (StrUtil.isNotBlank(pageParam.getServerName())) {
         queryWrapper.lambda().and(q -> q.like(Independent::getServerName, pageParam.getServerName()));
      } else if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
         queryWrapper.lambda().and(q -> q.like(Independent::getServerName, pageParam.getSearchKey()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getBusinessStatus())) {
         queryWrapper.lambda().and(q -> q.eq(Independent::getBusinessStatus, pageParam.getBusinessStatus()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
         queryWrapper.lambda().and(q -> q.eq(Independent::getMemberNo, pageParam.getMemberNo()));
      }

      if (StrUtil.isNotBlank(pageParam.getServerIp())) {
         List<String> serverIds = this.independentIpService.listServerIdByIp(pageParam.getServerIp());
         queryWrapper.lambda().and(q -> {
            if (ObjectUtil.isNotEmpty(serverIds)) {
               ((LambdaQueryWrapper<Independent>)q.in(Independent::getId, serverIds)).or();
            }

            q.like(Independent::getServerIp, pageParam.getServerIp());
         });
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         if (!StrUtil.equals(pageParam.getSortField(), "id")) {
            queryWrapper.lambda().orderByDesc(Independent::getId);
         }
      } else {
         queryWrapper.lambda().orderByDesc(Independent::getCreateTime).orderByDesc(Independent::getId);
      }

      Page<Independent> page = (Page<Independent>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<IndependentResponse> pageResultResponse = new Page();
      if (CollectionUtils.isNotEmpty(page.getRecords())) {
         List<IndependentResponse> responseList = page.getRecords().stream().map(independent -> {
            IndependentResponse independentResponse = new IndependentResponse();
            BeanUtils.copyProperties(independent, independentResponse);
            if (independent.getRegionId() != null) {
               BaseIndependentRegion baseIndependentRegion = this.independentRegionApi.selectByRegionId(independent.getRegionId());
               if (baseIndependentRegion != null && ObjectUtil.isNotEmpty(baseIndependentRegion.getName())) {
                  independentResponse.setRegionName(baseIndependentRegion.getName());
               }
            }

            if (independent.getLineId() != null) {
               BaseIndependentLine baseIndependentLine = this.independentLineApi.selectByLineId(independent.getLineId());
               if (baseIndependentLine != null && ObjectUtil.isNotEmpty(baseIndependentLine.getName())) {
                  independentResponse.setLineName(baseIndependentLine.getName());
               }
            }

            if (ObjectUtil.isNotEmpty(independent.getResourcePoolId())) {
               independentResponse.setResourcePoolId(independent.getResourcePoolId());
               BaseResourcePool resourcePool = this.resourcePoolApi.getById(independent.getResourcePoolId());
               if (ObjectUtil.isNotEmpty(resourcePool)) {
                  independentResponse.setResourcePoolType(resourcePool.getPoolType());
                  independentResponse.setResourcePoolName(resourcePool.getName());
               }
            }

            return independentResponse;
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
   public List<Independent> list(IndependentIdListParam memberIndependentIdListParam) {
      return this.list((Wrapper<Independent>)new QueryWrapper<Independent>().lambda().in(Independent::getId, memberIndependentIdListParam.getServerIds()));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void add(IndependentAddParam memberServerAddParam) {
      try {
         OsInstancePasswordValidator.validateOrThrow(memberServerAddParam.getSystemPassword());
         BaseAdminMachine machine = this.adminMachineApi.findMachineById(memberServerAddParam.getMachineId());
         if (machine == null) {
            throw new CommonException("该机型不存在", new Object[0]);
         } else {
            Independent memberIndependent = (Independent)BeanUtil.toBean(memberServerAddParam, Independent.class);
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

               try {
                  this.resourcePoolApi
                     .logResourceChange(
                        memberIndependent.getOrderNo(),
                        memberIndependent.getMemberNo(),
                        "CREATE",
                        StrUtil.blankToDefault(memberIndependent.getUpstreamServerId(), memberIndependent.getId()),
                        "INDEPENDENT"
                     );
               } catch (Exception var7) {
                  log.error("记录独立服务器创建变更日志失败，订单号：{}", memberIndependent.getOrderNo(), var7);
               }

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
               this.independentWorkService.add(independentWorkAddParam);
            }
         }
      } catch (CommonException var8) {
         log.error("创建独立服务器失败: {}", var8.getMessage(), var8);
         throw var8;
      } catch (Exception var9) {
         log.error("创建独立服务器时发生未知错误: {}", var9.getMessage(), var9);
         throw new CommonException("创建独立服务器失败", new Object[]{var9.getMessage()});
      }
   }

   @Override
   public void edit(IndependentEditParam memberServerEditParam) {
      if (memberServerEditParam != null && !StrUtil.isBlank(memberServerEditParam.getId())) {
         Independent memberIndependent = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, memberServerEditParam.getId()));
         if (null == memberIndependent) {
            throw new CommonException("独立服务器服务器不存在，id值为：{}", new Object[]{memberServerEditParam.getId()});
         } else {
            this.validateEditParam(memberServerEditParam);
            BaseIndependentSystemVersion osVer = null;
            if (memberServerEditParam.getSystemId() != null) {
               osVer = this.independentSystemVersionApi.selectByOsVersionId(memberServerEditParam.getSystemId());
               if (osVer == null) {
                  throw new CommonException("操作系统版本信息不存在，systemId值为：{}", new Object[]{memberServerEditParam.getSystemId()});
               }
            }

            LambdaUpdateWrapper<Independent> updateWrapper = (LambdaUpdateWrapper<Independent>)new LambdaUpdateWrapper<Independent>()
               .eq(Independent::getId, memberServerEditParam.getId());
            boolean needUpdate = false;
            if (memberServerEditParam.getPort() != null) {
               updateWrapper.set(Independent::getPort, memberServerEditParam.getPort());
               needUpdate = true;
            }

            if (memberServerEditParam.getSystemUserName() != null) {
               updateWrapper.set(Independent::getSystemUserName, memberServerEditParam.getSystemUserName());
               needUpdate = true;
            }

            if (memberServerEditParam.getServerStatus() != null) {
               updateWrapper.set(Independent::getServerStatus, memberServerEditParam.getServerStatus());
               needUpdate = true;
            }

            if (memberServerEditParam.getBusinessStatus() != null) {
               updateWrapper.set(Independent::getBusinessStatus, memberServerEditParam.getBusinessStatus());
               needUpdate = true;
            }

            if (memberServerEditParam.getSystemPassword() != null) {
               updateWrapper.set(Independent::getSystemPassword, memberServerEditParam.getSystemPassword());
               needUpdate = true;
            }

            if (memberServerEditParam.getSystemClassId() != null) {
               updateWrapper.set(Independent::getSystemClassId, memberServerEditParam.getSystemClassId());
               needUpdate = true;
            }

            if (memberServerEditParam.getSystemId() != null) {
               updateWrapper.set(Independent::getSystemId, memberServerEditParam.getSystemId());
               updateWrapper.set(Independent::getSystemClassId, StrUtil.blankToDefault(osVer.getOsClassId(), memberServerEditParam.getSystemClassId()));
               updateWrapper.set(Independent::getSystemClass, osVer.getOsClassName());
               updateWrapper.set(Independent::getSystemName, osVer.getOsName());
               needUpdate = true;
            }

            if (memberServerEditParam.getSystemClass() != null && memberServerEditParam.getSystemId() == null) {
               updateWrapper.set(Independent::getSystemClass, memberServerEditParam.getSystemClass());
               needUpdate = true;
            }

            if (memberServerEditParam.getSystemName() != null && memberServerEditParam.getSystemId() == null) {
               updateWrapper.set(Independent::getSystemName, memberServerEditParam.getSystemName());
               needUpdate = true;
            }

            if (memberServerEditParam.getRemark() != null) {
               updateWrapper.set(Independent::getRemark, memberServerEditParam.getRemark());
               needUpdate = true;
            }

            if (memberServerEditParam.getCpuInfo() != null) {
               updateWrapper.set(Independent::getCpuInfo, memberServerEditParam.getCpuInfo());
               needUpdate = true;
            }

            if (memberServerEditParam.getRamInfo() != null) {
               updateWrapper.set(Independent::getRamInfo, memberServerEditParam.getRamInfo());
               needUpdate = true;
            }

            if (memberServerEditParam.getDisksInfo() != null) {
               updateWrapper.set(Independent::getDisksInfo, memberServerEditParam.getDisksInfo());
               needUpdate = true;
            }

            if (memberServerEditParam.getIpSize() != null) {
               updateWrapper.set(Independent::getIpSize, memberServerEditParam.getIpSize());
               needUpdate = true;
            }

            if (memberServerEditParam.getDefense() != null) {
               updateWrapper.set(Independent::getDefense, memberServerEditParam.getDefense());
               needUpdate = true;
            }

            if (memberServerEditParam.getBandwidth() != null) {
               updateWrapper.set(Independent::getBandwidth, memberServerEditParam.getBandwidth());
               needUpdate = true;
            }

            if (!needUpdate) {
               throw new CommonException("请至少传入一个需要修改的字段", new Object[0]);
            } else {
               this.update(updateWrapper);
            }
         }
      } else {
         throw new CommonException("服务器ID不能为空", new Object[0]);
      }
   }

   @Override
   public IndependentResponse detail(IndependentIdParam serverIdParam) {
      Independent independent = this.queryEntity(serverIdParam.getId());
      if (null == independent) {
         throw new CommonException("该独立服务器不存在", new Object[0]);
      } else {
         IndependentResponse independentResponse = (IndependentResponse)BeanUtil.toBean(independent, IndependentResponse.class);
         if (independent.getLineId() != null) {
            BaseIndependentLine baseIndependentLine = this.independentLineApi.selectByLineId(independent.getLineId());
            if (baseIndependentLine != null) {
               independentResponse.setLineName(baseIndependentLine.getName());
            }
         }

         if (independent.getRegionId() != null) {
            BaseIndependentRegion baseIndependentRegion = this.independentRegionApi.selectByRegionId(independent.getRegionId());
            if (baseIndependentRegion != null) {
               independentResponse.setRegionName(baseIndependentRegion.getName());
            }
         }

         return independentResponse;
      }
   }

   @Override
   public Independent queryEntity(String id) {
      Independent memberServer = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, id));
      if (ObjectUtil.isEmpty(memberServer)) {
         throw new CommonException("云服务器不存在，id值为：{}", new Object[]{id});
      } else {
         return memberServer;
      }
   }

   @Override
   public String findLatestOrderNoByServerId(String serverId) {
      return StrUtil.isBlank(serverId) ? null : this.orderServerLinkMapper.selectLatestOrderNoByServerId(serverId);
   }

   @Override
   public void editRemark(RemarkEditParam memberRemarkEditParam) {
      Independent memberServer = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, memberRemarkEditParam.getId()));
      if (ObjectUtil.isEmpty(memberServer)) {
         throw new CommonException("独立服务器不存在，id值为：{}", new Object[]{memberRemarkEditParam.getId()});
      } else {
         memberServer.setRemark(memberRemarkEditParam.getRemark());
         this.updateById(memberServer);
      }
   }

   @Override
   public void updateRenewPrice(IndependentRenewPriceParam param) {
      if (param != null && !ObjectUtil.isEmpty(param.getServerIds())) {
         List<String> serverIds = param.getServerIds().stream().distinct().collect(Collectors.toList());
         List<Independent> independentList = this.list((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().in(Independent::getId, serverIds));
         if (independentList.size() != serverIds.size()) {
            throw new CommonException("存在不存在的独立服务器", new Object[0]);
         } else {
            this.update(
               (Wrapper<Independent>)((LambdaUpdateWrapper<Independent>)((LambdaUpdateWrapper<Independent>)new LambdaUpdateWrapper<Independent>().in(Independent::getId, serverIds))
                     .set(Independent::getRenewPrice, param.getRenewPrice()))
                  .set(CommonEntity::getUpdateTime, new Date())
            );
         }
      } else {
         throw new CommonException("独立服务器标识列表不能为空", new Object[0]);
      }
   }

   @Override
   public void updateEndTime(IndependentEndTimeParam param) {
      Independent independent = this.queryEntity(param.getId());
      if (independent == null) {
         throw new CommonException("独立服务器不存在", new Object[0]);
      } else {
         independent.setEndTime(param.getEndTime());
         DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
         String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
         independent.setDeleteTime(DateUtil.offsetDay(independent.getEndTime(), Integer.parseInt(cloudServerRetentionPeriod)));
         this.updateById(independent);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void batchAddEndDays(IndependentAddEndDaysParam param) {
      if (param != null && !ObjectUtil.isEmpty(param.getServerIds())) {
         if (param.getDays() != null && param.getDays() > 0) {
            List<String> serverIds = param.getServerIds().stream().distinct().collect(Collectors.toList());
            List<Independent> independentList = this.list((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().in(Independent::getId, serverIds));
            if (independentList.size() != serverIds.size()) {
               throw new CommonException("存在不存在的独立服务器", new Object[0]);
            } else {
               DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
               String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
               int retentionDays = Integer.parseInt(cloudServerRetentionPeriod);
               Date now = new Date();

               for (Independent independent : independentList) {
                  if (independent.getEndTime() == null) {
                     throw new CommonException("独立服务器到期时间为空，无法增加天数，id值为：{}", new Object[]{independent.getId()});
                  }

                  independent.setEndTime(DateUtil.offsetDay(independent.getEndTime(), param.getDays()));
                  independent.setDeleteTime(DateUtil.offsetDay(independent.getEndTime(), retentionDays));
                  independent.setUpdateTime(now);
               }

               this.updateBatchById(independentList);
            }
         } else {
            throw new CommonException("增加的到期天数必须大于0", new Object[0]);
         }
      } else {
         throw new CommonException("独立服务器标识列表不能为空", new Object[0]);
      }
   }

   @Override
   public CommonResult updatePasswordServer(IndependentPasswordEditParam passwordEditParam) {
      if (passwordEditParam == null || StrUtil.isBlank(passwordEditParam.getId())) {
         return CommonResult.error("服务器ID不能为空");
      } else if (StrUtil.isBlank(passwordEditParam.getOsPassword())) {
         return CommonResult.error("系统密码不能为空");
      } else {
         String pwdErr = OsInstancePasswordValidator.validate(passwordEditParam.getOsPassword());
         if (pwdErr != null) {
            return CommonResult.error(pwdErr);
         } else {
            String lockKey = "server:operation_lock:" + passwordEditParam.getId();
            String lockValue = UUID.randomUUID().toString();
            long expireTime = 300L;

            try {
               if (!this.tryLock(lockKey, lockValue, expireTime)) {
                  return CommonResult.error("服务器正在执行其他操作，请稍后再试");
               } else {
                  Independent server = this.queryEntity(passwordEditParam.getId());
                  if (server == null) {
                     return CommonResult.error("服务器不存在");
                  } else if (isDownstreamResourcePoolIndependent(server)) {
                     return this.forwardPoolIndependentUpdatePassword(server.getMemberNo(), server.getId(), passwordEditParam.getOsPassword());
                  } else if (this.isExistWorkingTask(server.getId())) {
                     return CommonResult.error("服务器存在未处理的任务，请等待......");
                  } else {
                     server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
                     server.setSystemPassword(passwordEditParam.getOsPassword());
                     this.updateById(server);
                     Map<String, Object> extJson = new HashMap<>();
                     extJson.put("serverId", server.getId());
                     extJson.put("serverIp", server.getServerIp());
                     extJson.put("lineId", server.getLineId());
                     if (server.getLineId() != null) {
                        BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
                        if (bl != null) {
                           extJson.put("lineName", bl.getName());
                        }
                     }

                     extJson.put("osPassword", passwordEditParam.getOsPassword());
                     this.submitPoolIndependentWork(server, OperateTypeEnum.RESET_PASSWORD.getCode(), extJson);
                     return CommonResult.ok("密码修改任务已创建");
                  }
               }
            } catch (Exception var10) {
               log.error("修改服务器密码失败", var10);
               return CommonResult.error(var10.getMessage());
            }
         }
      }
   }

   private static String resolveIndependentServerId(IndependentIdParam p) {
      return p == null ? null : StrUtil.trimToNull(p.getId());
   }

   private static String resolveIndependentServerId(IndependentLockParam p) {
      return p == null ? null : StrUtil.trimToNull(p.getId());
   }

   private void assertIndependentNotLocked(Independent server) {
      if (server != null && ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
         throw new CommonException("独立服务器已锁定，请先解锁后再操作", new Object[0]);
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult adminStartIndependent(IndependentIdParam param) {
      try {
         String serverId = resolveIndependentServerId(param);
         if (StrUtil.isBlank(serverId)) {
            return CommonResult.error("服务器ID不能为空");
         } else {
            Independent server = this.queryEntity(serverId);
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else {
               this.assertIndependentNotLocked(server);
               if (isDownstreamResourcePoolIndependent(server)) {
                  return this.forwardPoolIndependentStart(server.getMemberNo(), serverId);
               } else if (ServerStatusEnum.CREATING.getCode().equals(server.getServerStatus())) {
                  return CommonResult.error("服务器创建中，请等待......");
               } else if (ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
                  return CommonResult.error("服务器已锁定，请联系客服");
               } else if (ServerStatusEnum.REINSTALLING.getCode().equals(server.getServerStatus())) {
                  return CommonResult.error("服务器重装系统中，请等待......");
               } else if (this.isExistWorkingTask(server.getId())) {
                  return CommonResult.error("服务器存在未处理的任务，请等待......");
               } else {
                  server.setServerStatus(ServerStatusEnum.STARTING.getCode());
                  this.updateById(server);
                  Map<String, Object> ext = new HashMap<>();
                  ext.put("serverId", server.getId());
                  ext.put("serverIp", server.getServerIp());
                  ext.put("lineId", server.getLineId());
                  if (server.getLineId() != null) {
                     BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
                     if (bl != null) {
                        ext.put("lineName", bl.getName());
                     }
                  }

                  this.submitPoolIndependentWork(server, OperateTypeEnum.START.getCode(), ext);
                  return CommonResult.ok("启动任务已创建");
               }
            }
         }
      } catch (Exception var6) {
         log.error("后台独立服务器开机失败", var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult adminStopIndependent(IndependentIdParam param) {
      try {
         String serverId = resolveIndependentServerId(param);
         if (StrUtil.isBlank(serverId)) {
            return CommonResult.error("服务器ID不能为空");
         } else {
            Independent server = this.queryEntity(serverId);
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else {
               this.assertIndependentNotLocked(server);
               if (isDownstreamResourcePoolIndependent(server)) {
                  return this.forwardPoolIndependentStop(server.getMemberNo(), serverId);
               } else if (!ServerStatusEnum.RUNNING.getCode().equals(server.getServerStatus())) {
                  return CommonResult.error("服务器状态异常，无法关闭");
               } else if (this.isExistWorkingTask(server.getId())) {
                  return CommonResult.error("服务器存在未处理的任务，请等待......");
               } else {
                  server.setServerStatus(ServerStatusEnum.STOPPING.getCode());
                  this.updateById(server);
                  Map<String, Object> ext = new HashMap<>();
                  ext.put("serverId", server.getId());
                  ext.put("serverIp", server.getServerIp());
                  ext.put("lineId", server.getLineId());
                  if (server.getLineId() != null) {
                     BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
                     if (bl != null) {
                        ext.put("lineName", bl.getName());
                     }
                  }

                  ext.put("memberNo", server.getMemberNo());
                  this.submitPoolIndependentWork(server, OperateTypeEnum.STOP.getCode(), ext);
                  return CommonResult.ok("关闭任务已创建");
               }
            }
         }
      } catch (Exception var6) {
         log.error("后台独立服务器关机失败", var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult adminRestartIndependent(IndependentIdParam param) {
      try {
         String serverId = resolveIndependentServerId(param);
         if (StrUtil.isBlank(serverId)) {
            return CommonResult.error("服务器ID不能为空");
         } else {
            Independent server = this.queryEntity(serverId);
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else {
               this.assertIndependentNotLocked(server);
               if (isDownstreamResourcePoolIndependent(server)) {
                  return this.forwardPoolIndependentRestart(server.getMemberNo(), serverId);
               } else if (this.isExistWorkingTask(server.getId())) {
                  return CommonResult.error("服务器存在未处理的任务，请等待......");
               } else {
                  server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
                  this.updateById(server);
                  Map<String, Object> ext = new HashMap<>();
                  ext.put("serverId", server.getId());
                  ext.put("serverIp", server.getServerIp());
                  ext.put("lineId", server.getLineId());
                  if (server.getLineId() != null) {
                     BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
                     if (bl != null) {
                        ext.put("lineName", bl.getName());
                     }
                  }

                  this.submitPoolIndependentWork(server, OperateTypeEnum.REBOOT.getCode(), ext);
                  return CommonResult.ok("重启任务已创建");
               }
            }
         }
      } catch (Exception var6) {
         log.error("后台独立服务器重启失败", var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult adminPowerIndependent(IndependentIdParam param) {
      try {
         String serverId = resolveIndependentServerId(param);
         if (StrUtil.isBlank(serverId)) {
            return CommonResult.error("服务器ID不能为空");
         } else {
            Independent server = this.queryEntity(serverId);
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else {
               this.assertIndependentNotLocked(server);
               if (isDownstreamResourcePoolIndependent(server)) {
                  return this.forwardPoolIndependentPower(server.getMemberNo(), serverId);
               } else if (this.isExistWorkingTask(server.getId())) {
                  return CommonResult.error("服务器存在未处理的任务，请等待......");
               } else {
                  server.setServerStatus(ServerStatusEnum.POWERING.getCode());
                  this.updateById(server);
                  Map<String, Object> ext = new HashMap<>();
                  ext.put("serverId", server.getId());
                  ext.put("serverIp", server.getServerIp());
                  ext.put("lineId", server.getLineId());
                  if (server.getLineId() != null) {
                     BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
                     if (bl != null) {
                        ext.put("lineName", bl.getName());
                     }
                  }

                  this.submitPoolIndependentWork(server, OperateTypeEnum.SHUT_DOWN.getCode(), ext);
                  return CommonResult.ok("断开电源任务已创建");
               }
            }
         }
      } catch (Exception var6) {
         log.error("后台独立服务器断开电源失败", var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult adminLockIndependent(IndependentLockParam param) {
      try {
         String serverId = resolveIndependentServerId(param);
         if (StrUtil.isBlank(serverId)) {
            return CommonResult.error("服务器ID不能为空");
         } else if (param != null && !StrUtil.isBlank(param.getLockReason())) {
            Independent server = (Independent)this.getById(serverId);
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else if (isDownstreamResourcePoolIndependent(server)) {
               return this.forwardPoolIndependentLock(server.getMemberNo(), serverId, param.getLockReason());
            } else if (ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
               return CommonResult.error("服务器已锁定");
            } else if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else {
               server.setServerStatus(ServerStatusEnum.LOCKING.getCode());
               server.setLockReason(param.getLockReason());
               this.updateById(server);
               this.submitPoolIndependentWork(server, OperateTypeEnum.LOCK.getCode(), this.buildIndependentWorkExt(server, param.getLockReason()));
               return CommonResult.ok("锁定任务已创建");
            }
         } else {
            return CommonResult.error("锁定原因不能为空");
         }
      } catch (Exception var4) {
         log.error("管理员锁定独立服务器失败", var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult adminUnlockIndependent(IndependentIdParam param) {
      try {
         String serverId = resolveIndependentServerId(param);
         if (StrUtil.isBlank(serverId)) {
            return CommonResult.error("服务器ID不能为空");
         } else {
            Independent server = (Independent)this.getById(serverId);
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else if (isDownstreamResourcePoolIndependent(server)) {
               return this.forwardPoolIndependentUnlock(server.getMemberNo(), serverId);
            } else if (!ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
               return CommonResult.error("服务器状态异常，无法解锁");
            } else if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else {
               this.updateIndependentStatusAndLockReason(server.getId(), ServerStatusEnum.UNLOCKING.getCode(), null);
               server.setServerStatus(ServerStatusEnum.UNLOCKING.getCode());
               server.setLockReason(null);
               this.submitPoolIndependentWork(server, OperateTypeEnum.UNLOCK.getCode(), this.buildIndependentWorkExt(server, null));
               return CommonResult.ok("解锁任务已创建");
            }
         }
      } catch (Exception var4) {
         log.error("管理员解锁独立服务器失败", var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult adminReinstallIndependent(IndependentReinstallEditParam reinstallEditParam) {
      try {
         if (reinstallEditParam == null || StrUtil.isBlank(reinstallEditParam.getId())) {
            return CommonResult.error("服务器ID不能为空");
         } else if (!StrUtil.isBlank(reinstallEditParam.getOsClassId())
            && !StrUtil.isBlank(reinstallEditParam.getOsVersionId())
            && !StrUtil.isBlank(reinstallEditParam.getOsPassword())) {
            String pwdErr = OsInstancePasswordValidator.validate(reinstallEditParam.getOsPassword());
            if (pwdErr != null) {
               return CommonResult.error(pwdErr);
            } else {
               Independent server = this.queryEntity(reinstallEditParam.getId());
               if (server == null) {
                  return CommonResult.error("服务器不存在");
               } else {
                  this.assertIndependentNotLocked(server);
                  if (isDownstreamResourcePoolIndependent(server)) {
                     return this.forwardPoolIndependentReinstall(
                        server.getMemberNo(),
                        server.getId(),
                        reinstallEditParam.getOsClassId(),
                        reinstallEditParam.getOsVersionId(),
                        reinstallEditParam.getOsPassword()
                     );
                  } else if (this.isExistWorkingTask(server.getId())) {
                     return CommonResult.error("服务器存在未处理的任务，请等待......");
                  } else {
                     BaseIndependentSystemVersion osVer = this.independentSystemVersionApi.selectByOsVersionId(reinstallEditParam.getOsVersionId());
                     if (osVer == null) {
                        return CommonResult.error("操作系统版本信息不存在");
                     } else {
                        server.setServerStatus(ServerStatusEnum.REINSTALLING.getCode());
                        server.setSystemClassId(reinstallEditParam.getOsClassId());
                        server.setSystemId(reinstallEditParam.getOsVersionId());
                        server.setSystemClass(osVer.getOsClassName());
                        server.setSystemName(osVer.getOsName());
                        server.setSystemUserName(osVer.getDefaultUserName());
                        server.setPort(osVer.getDefaultPort());
                        server.setSystemPassword(reinstallEditParam.getOsPassword());
                        this.updateById(server);
                        Map<String, Object> ext = new HashMap<>();
                        ext.put("serverId", server.getId());
                        ext.put("serverIp", server.getServerIp());
                        ext.put("lineId", server.getLineId());
                        if (server.getLineId() != null) {
                           BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
                           if (bl != null) {
                              ext.put("lineName", bl.getName());
                           }
                        }

                        ext.put("osClassId", reinstallEditParam.getOsClassId());
                        ext.put("osVersionId", reinstallEditParam.getOsVersionId());
                        ext.put("osName", osVer.getOsName());
                        ext.put("osPassword", reinstallEditParam.getOsPassword());
                        ext.put("osClassName", osVer.getOsClassName());
                        this.submitPoolIndependentWork(server, OperateTypeEnum.REINSTALL.getCode(), ext);
                        return CommonResult.ok("重装任务已创建");
                     }
                  }
               }
            }
         } else {
            return CommonResult.error("操作系统分类、版本、密码不能为空");
         }
      } catch (Exception var7) {
         log.error("后台独立服务器重装失败", var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Override
   public Long getServerCountByCreateTime(LocalDateTime startTime, LocalDateTime endTime) {
      LambdaQueryWrapper<Independent> lambdaQueryWrapper = new LambdaQueryWrapper();
      List<Integer> list = new ArrayList<>();
      list.add(BusinessStatusEnum.NORMAL.getCode());
      list.add(BusinessStatusEnum.EXPIRED.getCode());
      lambdaQueryWrapper.ge(startTime != null, Independent::getCreateTime, startTime)
            .le(endTime != null, Independent::getCreateTime, endTime)
            .in(Independent::getBusinessStatus, list);
      return this.independentMapper.selectCount(lambdaQueryWrapper);
   }

   @Override
   public List<Independent> getServersToCheck(LocalDateTime sevenDaysLater) {
      return this.independentMapper.getServersToCheck(sevenDaysLater);
   }

   @Override
   public List<Independent> getServersForAutoRenew(LocalDateTime windowEnd) {
      return this.independentMapper.getServersForAutoRenew(windowEnd);
   }

   @Override
   public void isRenew(IndependentRenewAutoParam param) {
      if (param != null && !StrUtil.isBlank(param.getId()) && param.getRenewAuto() != null) {
         if (param.getRenewAuto() != 0 && param.getRenewAuto() != 1) {
            throw new CommonException("自动续费开关只能为0或1", new Object[0]);
         } else {
            Independent server = (Independent)this.getById(param.getId());
            if (server == null) {
               throw new CommonException("独立服务器不存在", new Object[0]);
            } else if (BusinessStatusEnum.DELETE.getCode().equals(server.getBusinessStatus())) {
               throw new CommonException("已删除服务器不支持修改自动续费", new Object[0]);
            } else if (Integer.valueOf(1).equals(param.getRenewAuto()) && BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus())) {
               throw new CommonException("服务器已过期，无法开启自动续费", new Object[0]);
            } else {
               this.update(
                  (Wrapper<Independent>)((LambdaUpdateWrapper<Independent>)new LambdaUpdateWrapper<Independent>().eq(Independent::getId, param.getId()))
                     .set(Independent::getRenewAuto, param.getRenewAuto())
               );
            }
         }
      } else {
         throw new CommonException("参数不能为空", new Object[0]);
      }
   }

   @Override
   public void updateServerStatus(String serverId) {
      this.updateServerStatus(serverId, OperateTypeEnum.STOP.getCode());
   }

   @Override
   public void updateServerStatus(String serverId, Integer operateType) {
      log.info("更新服务器状态，参数：{}", serverId);
      Independent independent = this.queryEntity(serverId);
      if (null != independent) {
         if (Integer.valueOf(1).equals(independent.getType())) {
            CommonResult<?> result = this.poolUpstreamIndependentClient.call(independent, "stop", null);
            if (upstreamCallOk(result)) {
               independent.setServerStatus(ServerStatusEnum.STOPPING.getCode());
               log.info("资源池独立服务器 {} 到期关机请求已发送", serverId);
            } else {
               log.error("资源池独立服务器到期关机请求失败，serverId={}，返回={}", serverId, JSON.toJSONString(result));
            }

            independent.setBusinessStatus(BusinessStatusEnum.EXPIRED.getCode());
            this.updateById(independent);
            log.info("独立服务器 {} 业务状态已更新为过期", serverId);
         } else {
            independent.setServerStatus(ServerStatusEnum.STOP.getCode());
            independent.setBusinessStatus(BusinessStatusEnum.EXPIRED.getCode());
            this.updateById(independent);
            BaseIndependentLine baseIndependentLine = null;
            if (independent.getLineId() != null) {
               baseIndependentLine = this.independentLineApi.selectByLineId(independent.getLineId());
            }

            IndependentWorkAddParam independentWorkAddParam = new IndependentWorkAddParam();
            independentWorkAddParam.setLineId(independent.getLineId());
            independentWorkAddParam.setRegionId(independent.getRegionId());
            independentWorkAddParam.setMemberNo(independent.getMemberNo());
            independentWorkAddParam.setIndependentId(independent.getId());
            independentWorkAddParam.setOperateType(operateType);
            independentWorkAddParam.setBusinessStatus(0);
            independentWorkAddParam.setSubmitTime(new Date());
            Map<String, Object> extJson = new HashMap<>();
            extJson.put("serverId", independent.getId());
            extJson.put("serverIp", independent.getServerIp());
            extJson.put("lineId", independent.getLineId());
            if (baseIndependentLine != null) {
               extJson.put("lineName", baseIndependentLine.getName());
            }

            extJson.put("memberNo", independent.getMemberNo());
            independentWorkAddParam.setExtJson(JSON.toJSONString(extJson));
            log.info("独立服务器关机任务开始，param:{}", JSON.toJSONString(independentWorkAddParam));
            this.independentWorkService.add(independentWorkAddParam);
         }
      }
   }

   @Override
   public List<Independent> getServersToDeleteCheck() {
      return this.independentMapper.getServersToDeleteCheck();
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult syncUpstreamOrder(IndependentIdParam independentIdParam) {
      String serverId = independentIdParam == null ? null : independentIdParam.getId();

      try {
         Independent independent = (Independent)this.getById(serverId);
         if (independent == null) {
            return CommonResult.error("服务器不存在");
         } else if (!isDownstreamResourcePoolIndependent(independent)) {
            return CommonResult.error("当前独立服务器不是资源池服务器");
         } else {
            String upstreamServerId = StrUtil.trimToNull(independent.getUpstreamServerId());
            if (StrUtil.isBlank(upstreamServerId)) {
               return CommonResult.error("资源池独立服务器缺少 UPSTREAM_SERVER_ID，无法同步上游数据");
            } else {
               JSONObject data = this.resourcePoolUpstreamSyncClient.queryServerDetail(independent.getResourcePoolId(), upstreamServerId, "INDEPENDENT");
               BaseIndependent upstream = new BaseIndependent();
               BeanUtils.copyProperties(independent, upstream);
               BigDecimal localRenewPrice = independent.getRenewPrice();
               ResourcePoolUpstreamDataApplier.applyToIndependent(data, upstream, independent.getId());
               upstream.setId(independent.getId());
               upstream.setRenewPrice(localRenewPrice);
               this.syncFromUpstream(upstream);
               this.independentIpService.replaceFromResourcePoolUpstreamSnapshot(independent.getId(), data);
               return CommonResult.ok("同步上游独立服务器订单成功");
            }
         }
      } catch (Exception var8) {
         log.error("同步上游独立服务器订单失败 serverId={}", serverId, var8);
         return CommonResult.error(var8.getMessage());
      }
   }

   @Override
   public CommonResult deleteServer(IndependentIdParam serverIdParam) {
      return this.deleteServer(serverIdParam, OperateTypeEnum.DELETE.getCode());
   }

   @Override
   public CommonResult deleteServer(IndependentIdParam serverIdParam, Integer operateType) {
      try {
         Independent server = this.queryEntity(serverIdParam.getId());
         if (null == server) {
            return CommonResult.error("该独立服务器不存在");
         }

         if (Integer.valueOf(1).equals(server.getType())) {
            String resourcePoolId = this.resolveIndependentResourcePoolId(server);
            if (StrUtil.isBlank(resourcePoolId)) {
               return CommonResult.error("资源池ID未配置，无法通知上游删除");
            }

            String upstreamPk = StrUtil.trimToNull(resolveIndependentUpstreamPk(server));
            if (StrUtil.isBlank(upstreamPk)) {
               return CommonResult.error("上游服务器ID未同步，无法通知上游删除");
            }

            String effectiveOrderNo = resolveIndependentPoolOrderNo(server.getOrderNo(), server);
            JSONObject params = new JSONObject();
            if (StrUtil.isNotBlank(effectiveOrderNo)) {
               params.put("orderNo", effectiveOrderNo);
            }

            params.put("upstreamServerId", upstreamPk);
            BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
            if (StrUtil.isNotBlank(effectiveOrderNo)) {
               poolRequest.setOrderNo(effectiveOrderNo);
            }

            poolRequest.setMemberNo(server.getMemberNo());
            poolRequest.setResourcePoolId(resourcePoolId);
            poolRequest.setOperationType("DELETE");
            poolRequest.setAServerId(upstreamPk);
            poolRequest.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
            poolRequest.setRequestParams(JSON.toJSONString(params));
            this.resourcePoolApi.createRequest(poolRequest);
            server.setServerStatus(ServerStatusEnum.STOP.getCode());
            this.updateById(server);
            this.independentIpMapper.deleteByServerId(server.getId());
            log.info("✅ 资源池独立服务器删除请求已创建，等待定时任务发送，serverId={}", server.getId());
            return CommonResult.ok("删除任务已创建");
         }

         if (!Integer.valueOf(1).equals(server.getType())) {
            Map<String, Object> extJson = new HashMap<>();
            extJson.put("serverId", server.getId());
            extJson.put("serverIp", server.getServerIp());
            extJson.put("lineId", server.getLineId());
            if (server.getLineId() != null) {
               BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
               if (bl != null) {
                  extJson.put("lineName", bl.getName());
               }
            }

            extJson.put("memberNo", server.getMemberNo());
            log.info("独立服务器删除工单创建，serverId={}", server.getId());
            this.submitPoolIndependentWork(server, operateType, extJson);
         }

         server.setServerStatus(ServerStatusEnum.STOP.getCode());
         server.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
         this.updateById(server);
         this.independentIpMapper.deleteByServerId(server.getId());
      } catch (Exception var9) {
         log.error("独立服务器删除失败", var9);
         return CommonResult.error(var9.getMessage());
      }

      return CommonResult.ok();
   }

   private boolean tryLock(String lockKey, String lockValue, long expireTime) {
      Boolean result = this.redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.SECONDS);
      return result != null && result;
   }

   @Override
   public CommonResult upgradeBandwidthServer(String orderNo, String serverId, Integer bandwidth, BigDecimal renewPrice) {
      try {
         Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(server);
            if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else if (Integer.valueOf(1).equals(server.getType())) {
               String resourcePoolId = this.resolveIndependentResourcePoolId(server);
               if (StrUtil.isBlank(resourcePoolId)) {
                  return CommonResult.error("资源池ID未配置，无法通知上游升级带宽");
               } else {
                  String upstreamPk = StrUtil.trimToNull(resolveIndependentUpstreamPk(server));
                  if (StrUtil.isBlank(upstreamPk)) {
                     return CommonResult.error("上游服务器ID未同步，无法通知上游升级带宽");
                  } else {
                     String effectiveOrderNo = resolveIndependentPoolOrderNo(orderNo, server);
                     if (StrUtil.isBlank(effectiveOrderNo)) {
                        return CommonResult.error("订单号不能为空，无法通知上游");
                     } else {
                        JSONObject params = new JSONObject();
                        params.put("downstreamOrderNo", effectiveOrderNo);
                        params.put("orderNo", effectiveOrderNo);
                        params.put("upstreamServerId", upstreamPk);
                        params.put("bandwidth", bandwidth);
                        if (renewPrice != null) {
                           params.put("renewPrice", renewPrice);
                        }

                        BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
                        poolRequest.setOrderNo(effectiveOrderNo);
                        poolRequest.setMemberNo(server.getMemberNo());
                        poolRequest.setResourcePoolId(resourcePoolId);
                        poolRequest.setOperationType("UPGRADE_BANDWIDTH");
                        poolRequest.setAServerId(upstreamPk);
                        poolRequest.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                        poolRequest.setRequestParams(JSON.toJSONString(params));
                        this.resourcePoolApi.createRequest(poolRequest);
                        log.info("✅ 资源池独立机升级带宽请求已创建，订单号：{}", effectiveOrderNo);
                        return CommonResult.ok("升级带宽任务已创建");
                     }
                  }
               }
            } else {
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
               this.independentWorkService.add(independentWorkAddParam);
               return CommonResult.ok("升级带宽任务已创建");
            }
         }
      } catch (Exception var11) {
         log.error("升级服务器带宽失败", var11);
         return CommonResult.error(var11.getMessage());
      }
   }

   @Override
   public CommonResult upgradeIpServer(String orderNo, String serverId, Integer ipSize, BigDecimal renewPrice) {
      try {
         Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(server);
            if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else if (Integer.valueOf(1).equals(server.getType())) {
               String resourcePoolId = this.resolveIndependentResourcePoolId(server);
               if (StrUtil.isBlank(resourcePoolId)) {
                  return CommonResult.error("资源池ID未配置，无法通知上游升级IP");
               } else {
                  String upstreamPk = StrUtil.trimToNull(resolveIndependentUpstreamPk(server));
                  if (StrUtil.isBlank(upstreamPk)) {
                     return CommonResult.error("上游服务器ID未同步，无法通知上游升级IP");
                  } else {
                     String effectiveOrderNo = resolveIndependentPoolOrderNo(orderNo, server);
                     if (StrUtil.isBlank(effectiveOrderNo)) {
                        return CommonResult.error("订单号不能为空，无法通知上游");
                     } else {
                        JSONObject params = new JSONObject();
                        params.put("downstreamOrderNo", effectiveOrderNo);
                        params.put("orderNo", effectiveOrderNo);
                        params.put("upstreamServerId", upstreamPk);
                        params.put("ipSize", ipSize);
                        if (renewPrice != null) {
                           params.put("renewPrice", renewPrice);
                        }

                        BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
                        poolRequest.setOrderNo(effectiveOrderNo);
                        poolRequest.setMemberNo(server.getMemberNo());
                        poolRequest.setResourcePoolId(resourcePoolId);
                        poolRequest.setOperationType("UPGRADE_IP");
                        poolRequest.setAServerId(upstreamPk);
                        poolRequest.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                        poolRequest.setRequestParams(JSON.toJSONString(params));
                        this.resourcePoolApi.createRequest(poolRequest);
                        log.info("✅ 资源池独立机升级IP请求已创建，订单号：{}", effectiveOrderNo);
                        return CommonResult.ok("升级IP任务已创建");
                     }
                  }
               }
            } else {
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
               this.independentWorkService.add(independentWorkAddParam);
               return CommonResult.ok("升级IP任务已创建");
            }
         }
      } catch (Exception var11) {
         log.error("升级服务器核心失败", var11);
         return CommonResult.error(var11.getMessage());
      }
   }

   @Override
   public CommonResult renewServer(String orderNo, String serverId, Integer months, String unit, BigDecimal newMonthlyRenewPrice) {
      try {
         Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(server);
            if (Integer.valueOf(1).equals(server.getType())) {
               String resourcePoolId = this.resolveIndependentResourcePoolId(server);
               if (StrUtil.isBlank(resourcePoolId)) {
                  return CommonResult.error("资源池ID未配置，无法通知上游续费");
               } else {
                  String upstreamPk = StrUtil.trimToNull(resolveIndependentUpstreamPk(server));
                  if (StrUtil.isBlank(upstreamPk)) {
                     return CommonResult.error("上游服务器ID未同步，无法通知上游续费");
                  } else {
                     String effectiveOrderNo = resolveIndependentPoolOrderNo(orderNo, server);
                     if (StrUtil.isBlank(effectiveOrderNo)) {
                        return CommonResult.error("订单号不能为空，无法通知上游");
                     } else {
                        if (newMonthlyRenewPrice != null) {
                           server.setRenewPrice(newMonthlyRenewPrice);
                        }

                        this.applyIndependentRenewEndTime(server, months);
                        JSONObject params = new JSONObject();
                        params.put("downstreamOrderNo", effectiveOrderNo);
                        params.put("orderNo", effectiveOrderNo);
                        params.put("upstreamServerId", upstreamPk);
                        params.put("months", months);
                        if (newMonthlyRenewPrice != null) {
                           params.put("renewPrice", newMonthlyRenewPrice);
                        }

                        BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
                        poolRequest.setOrderNo(effectiveOrderNo);
                        poolRequest.setMemberNo(server.getMemberNo());
                        poolRequest.setResourcePoolId(resourcePoolId);
                        poolRequest.setOperationType("RENEW");
                        poolRequest.setAServerId(upstreamPk);
                        poolRequest.setMonths(months);
                        poolRequest.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                        poolRequest.setRequestParams(JSON.toJSONString(params));
                        this.resourcePoolApi.createRequest(poolRequest);
                        log.info("✅ 资源池独立服务器续费请求已创建，等待定时任务发送，订单号：{}", effectiveOrderNo);
                        return CommonResult.ok();
                     }
                  }
               }
            } else {
               BaseIndependentLine baseIndependentLine = null;
               if (server.getLineId() != null) {
                  baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
               }

               if (newMonthlyRenewPrice != null) {
                  server.setRenewPrice(newMonthlyRenewPrice);
               }

               this.applyIndependentRenewEndTime(server, months);
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
               this.independentWorkService.add(independentWorkAddParam);
               this.independentWorkService.closePendingExpireStopWorks(server.getId());
               return CommonResult.ok();
            }
         }
      } catch (CommonException var12) {
         log.error("服务器续费失败: {}", var12.getMessage(), var12);
         return CommonResult.error(var12.getMessage());
      } catch (Exception var13) {
         log.error("服务器续费失败", var13);
         return CommonResult.error("系统错误");
      }
   }

   @Override
   public CommonResult upgradeDefenseServer(String orderNo, String serverId, Integer defense, BigDecimal renewPrice) {
      try {
         Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
         if (null == server) {
            return CommonResult.error("服务器不存在");
         } else {
            this.assertIndependentNotLocked(server);
            if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else if (Integer.valueOf(1).equals(server.getType())) {
               String resourcePoolId = this.resolveIndependentResourcePoolId(server);
               if (StrUtil.isBlank(resourcePoolId)) {
                  return CommonResult.error("资源池ID未配置，无法通知上游升级防御");
               } else {
                  String upstreamPk = StrUtil.trimToNull(resolveIndependentUpstreamPk(server));
                  if (StrUtil.isBlank(upstreamPk)) {
                     return CommonResult.error("上游服务器ID未同步，无法通知上游升级防御");
                  } else {
                     String effectiveOrderNo = resolveIndependentPoolOrderNo(orderNo, server);
                     if (StrUtil.isBlank(effectiveOrderNo)) {
                        return CommonResult.error("订单号不能为空，无法通知上游");
                     } else {
                        JSONObject params = new JSONObject();
                        params.put("downstreamOrderNo", effectiveOrderNo);
                        params.put("orderNo", effectiveOrderNo);
                        params.put("upstreamServerId", upstreamPk);
                        params.put("defense", defense);
                        if (renewPrice != null) {
                           params.put("renewPrice", renewPrice);
                        }

                        BaseResourcePoolRequest poolRequest = new BaseResourcePoolRequest();
                        poolRequest.setOrderNo(effectiveOrderNo);
                        poolRequest.setMemberNo(server.getMemberNo());
                        poolRequest.setResourcePoolId(resourcePoolId);
                        poolRequest.setOperationType("UPGRADE_DEFENSE");
                        poolRequest.setAServerId(upstreamPk);
                        poolRequest.setProductType(ProductTypeEnum.INDEPENDENT_SERVER.getCode());
                        poolRequest.setRequestParams(JSON.toJSONString(params));
                        this.resourcePoolApi.createRequest(poolRequest);
                        log.info("✅ 资源池独立机升级防御请求已创建，订单号：{}", effectiveOrderNo);
                        return CommonResult.ok("升级防御任务已创建");
                     }
                  }
               }
            } else {
               BaseIndependentLine baseIndependentLine = null;
               if (server.getLineId() != null) {
                  baseIndependentLine = this.independentLineApi.selectByLineId(server.getLineId());
               }

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
               this.independentWorkService.add(independentWorkAddParam);
               return CommonResult.ok("升级防御任务已创建");
            }
         }
      } catch (Exception var11) {
         log.error("升级服务器防御失败", var11);
         return CommonResult.error(var11.getMessage());
      }
   }

   private boolean isExistWorkingTask(String independentId) {
      return this.independentWorkService.isExistWorkingTask(independentId);
   }

   @Override
   public void applyLocalResourcePoolAfterUpstreamSuccess(String operationType, String serverId, String requestParamsJson, String orderNo) {
      if (!StrUtil.isBlank(serverId) && !StrUtil.isBlank(operationType)) {
         Independent independent = (Independent)this.getById(serverId);
         if (this.isPoolManagedIndependent(independent)) {
            cn.hutool.json.JSONObject params = StrUtil.isBlank(requestParamsJson) ? new cn.hutool.json.JSONObject() : JSONUtil.parseObj(requestParamsJson);

            try {
               switch (operationType) {
                  case "RENEW":
                     this.applyPoolRenewLocalAfterUpstream(independent, serverId, params);
                     break;
                  case "UPGRADE_BANDWIDTH":
                     Integer bw = params.getInt("bandwidth");
                     if (bw != null) {
                        independent.setBandwidth(bw);
                     }

                     this.updatePoolUpgradeRenewPrice(independent, params);
                     independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
                     this.updateById(independent);
                     break;
                  case "UPGRADE_DEFENSE":
                     Integer def = params.getInt("defense");
                     if (def != null) {
                        independent.setDefense(def);
                     }

                     this.updatePoolUpgradeRenewPrice(independent, params);
                     independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
                     this.updateById(independent);
                     break;
                  case "UPGRADE_IP":
                     Integer ipSz = params.getInt("ipSize");
                     if (ipSz != null) {
                        independent.setIpSize(ipSz);
                     }

                     this.updatePoolUpgradeRenewPrice(independent, params);
                     independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
                     this.updateById(independent);
                     break;
                  case "DELETE":
                     independent.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
                     independent.setServerIp(null);
                     this.updateById(independent);
                     this.independentIpMapper.deleteByServerId(serverId);
               }
            } catch (Exception var12) {
               log.error("上游已成功，本地资源池独立服务器同步失败 serverId={} op={}", new Object[]{serverId, operationType, var12});
            }
         }
      }
   }

   private boolean isPoolManagedIndependent(Independent independent) {
      return independent != null
         && (
            Integer.valueOf(1).equals(independent.getType())
               || StrUtil.isNotBlank(independent.getResourcePoolId())
               || StrUtil.isNotBlank(independent.getUpstreamServerId())
         );
   }

   private void applyPoolRenewLocalAfterUpstream(Independent independent, String serverId, cn.hutool.json.JSONObject params) {
      Independent current = (Independent)this.getById(serverId);
      if (current != null) {
         if (BusinessStatusEnum.EXPIRED.getCode().equals(current.getBusinessStatus())) {
            current.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
            this.updateById(current);
         }
      }
   }

   private void applyIndependentRenewEndTime(Independent server, Integer months) {
      server.setEndTime(DateUtil.offsetMonth(server.getEndTime(), months));
      DevConfigApi devConfigApi = (DevConfigApi)SpringUtil.getBean(DevConfigApi.class);
      String cloudServerRetentionPeriod = devConfigApi.getValueByKey("CLOUD_SERVER_RETENTION_PERIOD");
      log.info("云服务器保留天数：{}", cloudServerRetentionPeriod);
      server.setDeleteTime(DateUtil.offsetDay(server.getEndTime(), Integer.valueOf(cloudServerRetentionPeriod)));
      if (BusinessStatusEnum.EXPIRED.getCode().equals(server.getBusinessStatus())) {
         server.setBusinessStatus(BusinessStatusEnum.NORMAL.getCode());
      }

      this.updateById(server);
   }

   private void updatePoolUpgradeRenewPrice(Independent independent, cn.hutool.json.JSONObject params) {
      BigDecimal renewPrice = params.getBigDecimal("renewPrice");
      if (renewPrice == null && params.containsKey("renewPrice")) {
         Object o = params.get("renewPrice");
         if (o != null) {
            try {
               renewPrice = new BigDecimal(o.toString());
            } catch (Exception var6) {
            }
         }
      }

      if (renewPrice != null) {
         independent.setRenewPrice(renewPrice);
      } else {
         log.warn("资源池独立服务器升级回写续费价缺少 renewPrice，serverId={}", independent.getId());
      }
   }

   private void submitPoolIndependentWork(Independent server, Integer operateType, Map<String, Object> extJson) {
      IndependentWorkAddParam w = new IndependentWorkAddParam();
      w.setLineId(server.getLineId());
      w.setRegionId(server.getRegionId());
      w.setMemberNo(server.getMemberNo());
      w.setIndependentId(server.getId());
      w.setOperateType(operateType);
      w.setBusinessStatus(0);
      w.setSubmitTime(new Date());
      w.setExtJson(JSON.toJSONString(extJson));
      this.independentWorkService.add(w);
   }

   private Map<String, Object> buildIndependentWorkExt(Independent server, String lockReason) {
      Map<String, Object> ext = new HashMap<>();
      ext.put("serverId", server.getId());
      ext.put("serverIp", server.getServerIp());
      ext.put("lineId", server.getLineId());
      ext.put("memberNo", server.getMemberNo());
      if (StrUtil.isNotBlank(lockReason)) {
         ext.put("lockReason", lockReason);
      }

      if (server.getLineId() != null) {
         BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
         if (bl != null) {
            ext.put("lineName", bl.getName());
         }
      }

      return ext;
   }

   private void updateIndependentStatusAndLockReason(String serverId, String serverStatus, String lockReason) {
      this.update(
         (Wrapper<Independent>)((LambdaUpdateWrapper<Independent>)((LambdaUpdateWrapper<Independent>)((LambdaUpdateWrapper<Independent>)new LambdaUpdateWrapper<Independent>().eq(Independent::getId, serverId))
                  .set(Independent::getServerStatus, serverStatus))
               .set(Independent::getLockReason, lockReason))
            .set(CommonEntity::getUpdateTime, new Date())
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult poolApiStartIndependent(Long memberNo, String serverId) {
      try {
         Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (memberNo == null || !Objects.equals(server.getMemberNo(), memberNo)) {
            return CommonResult.error("无权操作该服务器");
         } else if (ServerStatusEnum.CREATING.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器创建中，请等待......");
         } else if (ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器已锁定，请联系客服");
         } else if (ServerStatusEnum.REINSTALLING.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器重装系统中，请等待......");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            server.setServerStatus(ServerStatusEnum.STARTING.getCode());
            this.updateById(server);
            Map<String, Object> ext = new HashMap<>();
            ext.put("serverId", server.getId());
            ext.put("serverIp", server.getServerIp());
            ext.put("lineId", server.getLineId());
            if (server.getLineId() != null) {
               BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
               if (bl != null) {
                  ext.put("lineName", bl.getName());
               }
            }

            this.submitPoolIndependentWork(server, OperateTypeEnum.START.getCode(), ext);
            return CommonResult.ok("启动任务已创建");
         }
      } catch (Exception var6) {
         log.error("资源池API独立服务器开机失败 serverId={}", serverId, var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult poolApiStopIndependent(Long memberNo, String serverId) {
      try {
         Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (memberNo == null || !Objects.equals(server.getMemberNo(), memberNo)) {
            return CommonResult.error("无权操作该服务器");
         } else if (!ServerStatusEnum.RUNNING.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器状态异常，无法关闭");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            server.setServerStatus(ServerStatusEnum.STOPPING.getCode());
            this.updateById(server);
            Map<String, Object> ext = new HashMap<>();
            ext.put("serverId", server.getId());
            ext.put("serverIp", server.getServerIp());
            ext.put("lineId", server.getLineId());
            if (server.getLineId() != null) {
               BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
               if (bl != null) {
                  ext.put("lineName", bl.getName());
               }
            }

            ext.put("memberNo", server.getMemberNo());
            this.submitPoolIndependentWork(server, OperateTypeEnum.STOP.getCode(), ext);
            return CommonResult.ok("关闭任务已创建");
         }
      } catch (Exception var6) {
         log.error("资源池API独立服务器关机失败 serverId={}", serverId, var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult poolApiRestartIndependent(Long memberNo, String serverId) {
      try {
         Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (memberNo == null || !Objects.equals(server.getMemberNo(), memberNo)) {
            return CommonResult.error("无权操作该服务器");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
            this.updateById(server);
            Map<String, Object> ext = new HashMap<>();
            ext.put("serverId", server.getId());
            ext.put("serverIp", server.getServerIp());
            ext.put("lineId", server.getLineId());
            if (server.getLineId() != null) {
               BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
               if (bl != null) {
                  ext.put("lineName", bl.getName());
               }
            }

            this.submitPoolIndependentWork(server, OperateTypeEnum.REBOOT.getCode(), ext);
            return CommonResult.ok("重启任务已创建");
         }
      } catch (Exception var6) {
         log.error("资源池API独立服务器重启失败 serverId={}", serverId, var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult poolApiPowerIndependent(Long memberNo, String serverId) {
      try {
         Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (memberNo == null || !Objects.equals(server.getMemberNo(), memberNo)) {
            return CommonResult.error("无权操作该服务器");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            server.setServerStatus(ServerStatusEnum.POWERING.getCode());
            this.updateById(server);
            Map<String, Object> ext = new HashMap<>();
            ext.put("serverId", server.getId());
            ext.put("serverIp", server.getServerIp());
            ext.put("lineId", server.getLineId());
            if (server.getLineId() != null) {
               BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
               if (bl != null) {
                  ext.put("lineName", bl.getName());
               }
            }

            this.submitPoolIndependentWork(server, OperateTypeEnum.SHUT_DOWN.getCode(), ext);
            return CommonResult.ok("断开电源任务已创建");
         }
      } catch (Exception var6) {
         log.error("资源池API独立服务器断开电源失败 serverId={}", serverId, var6);
         return CommonResult.error(var6.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult poolApiReinstallIndependent(Long memberNo, String serverId, String osClassId, String osVersionId, String osPassword) {
      try {
         if (!StrUtil.isBlank(osClassId) && !StrUtil.isBlank(osVersionId) && !StrUtil.isBlank(osPassword)) {
            String pwdErr = OsInstancePasswordValidator.validate(osPassword);
            if (pwdErr != null) {
               return CommonResult.error(pwdErr);
            } else {
               Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
               if (server == null) {
                  return CommonResult.error("服务器不存在");
               } else if (memberNo == null || !Objects.equals(server.getMemberNo(), memberNo)) {
                  return CommonResult.error("无权操作该服务器");
               } else if (this.isExistWorkingTask(server.getId())) {
                  return CommonResult.error("服务器存在未处理的任务，请等待......");
               } else {
                  BaseIndependentSystemVersion osVer = this.independentSystemVersionApi.selectByOsVersionId(osVersionId);
                  if (osVer == null) {
                     return CommonResult.error("操作系统版本信息不存在");
                  } else {
                     server.setServerStatus(ServerStatusEnum.REINSTALLING.getCode());
                     server.setSystemClassId(osClassId);
                     server.setSystemId(osVersionId);
                     server.setSystemClass(osVer.getOsClassName());
                     server.setSystemName(osVer.getOsName());
                     server.setSystemUserName(osVer.getDefaultUserName());
                     server.setPort(osVer.getDefaultPort());
                     server.setSystemPassword(osPassword);
                     this.updateById(server);
                     Map<String, Object> ext = new HashMap<>();
                     ext.put("serverId", server.getId());
                     ext.put("serverIp", server.getServerIp());
                     ext.put("lineId", server.getLineId());
                     if (server.getLineId() != null) {
                        BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
                        if (bl != null) {
                           ext.put("lineName", bl.getName());
                        }
                     }

                     ext.put("osClassId", osClassId);
                     ext.put("osVersionId", osVersionId);
                     ext.put("osName", osVer.getOsName());
                     ext.put("osPassword", osPassword);
                     ext.put("osClassName", osVer.getOsClassName());
                     this.submitPoolIndependentWork(server, OperateTypeEnum.REINSTALL.getCode(), ext);
                     return CommonResult.ok("重装任务已创建");
                  }
               }
            }
         } else {
            return CommonResult.error("osClassId、osVersionId、osPassword 不能为空");
         }
      } catch (Exception var11) {
         log.error("资源池API独立服务器重装失败 serverId={}", serverId, var11);
         return CommonResult.error(var11.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult poolApiUpdatePasswordIndependent(Long memberNo, String serverId, String osPassword) {
      try {
         if (StrUtil.isBlank(osPassword)) {
            return CommonResult.error("系统密码不能为空");
         } else {
            String pwdErr = OsInstancePasswordValidator.validate(osPassword);
            if (pwdErr != null) {
               return CommonResult.error(pwdErr);
            } else {
               Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
               if (server == null) {
                  return CommonResult.error("服务器不存在");
               } else if (memberNo == null || !Objects.equals(server.getMemberNo(), memberNo)) {
                  return CommonResult.error("无权操作该服务器");
               } else if (this.isExistWorkingTask(server.getId())) {
                  return CommonResult.error("服务器存在未处理的任务，请等待......");
               } else {
                  server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
                  server.setSystemPassword(osPassword);
                  this.updateById(server);
                  Map<String, Object> ext = new HashMap<>();
                  ext.put("serverId", server.getId());
                  ext.put("serverIp", server.getServerIp());
                  ext.put("lineId", server.getLineId());
                  if (server.getLineId() != null) {
                     BaseIndependentLine bl = this.independentLineApi.selectByLineId(server.getLineId());
                     if (bl != null) {
                        ext.put("lineName", bl.getName());
                     }
                  }

                  ext.put("osPassword", osPassword);
                  this.submitPoolIndependentWork(server, OperateTypeEnum.RESET_PASSWORD.getCode(), ext);
                  return CommonResult.ok("密码修改任务已创建");
               }
            }
         }
      } catch (Exception var8) {
         log.error("资源池API独立服务器改密失败 serverId={}", serverId, var8);
         return CommonResult.error(var8.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult poolApiLockIndependent(Long memberNo, String serverId, String lockReason) {
      try {
         if (StrUtil.isBlank(lockReason)) {
            return CommonResult.error("锁定原因不能为空");
         } else {
            Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else if (memberNo == null || !Objects.equals(server.getMemberNo(), memberNo)) {
               return CommonResult.error("无权操作该服务器");
            } else if (ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
               return CommonResult.error("服务器已锁定");
            } else if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else {
               server.setServerStatus(ServerStatusEnum.LOCKING.getCode());
               server.setLockReason(lockReason);
               this.updateById(server);
               this.submitPoolIndependentWork(server, OperateTypeEnum.LOCK.getCode(), this.buildIndependentWorkExt(server, lockReason));
               return CommonResult.ok("锁定任务已创建");
            }
         }
      } catch (Exception var5) {
         log.error("资源池API独立服务器锁定失败 serverId={}", serverId, var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult poolApiUnlockIndependent(Long memberNo, String serverId) {
      try {
         Independent server = (Independent)this.getOne((Wrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId));
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (memberNo == null || !Objects.equals(server.getMemberNo(), memberNo)) {
            return CommonResult.error("无权操作该服务器");
         } else if (!ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器状态异常，无法解锁");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            this.updateIndependentStatusAndLockReason(server.getId(), ServerStatusEnum.UNLOCKING.getCode(), null);
            server.setServerStatus(ServerStatusEnum.UNLOCKING.getCode());
            server.setLockReason(null);
            this.submitPoolIndependentWork(server, OperateTypeEnum.UNLOCK.getCode(), this.buildIndependentWorkExt(server, null));
            return CommonResult.ok("解锁任务已创建");
         }
      } catch (Exception var4) {
         log.error("资源池API独立服务器解锁失败 serverId={}", serverId, var4);
         return CommonResult.error(var4.getMessage());
      }
   }

   private Independent loadIndependentForMember(Long memberNo, String serverId) {
      return memberNo != null && !StrUtil.isBlank(serverId)
         ? (Independent)this.getOne(
            (Wrapper<Independent>)((LambdaQueryWrapper<Independent>)new LambdaQueryWrapper<Independent>().eq(Independent::getId, serverId)).eq(Independent::getMemberNo, memberNo)
         )
         : null;
   }

   private static boolean isDownstreamResourcePoolIndependent(Independent s) {
      return s != null && Integer.valueOf(1).equals(s.getType()) && StrUtil.isNotBlank(s.getResourcePoolId());
   }

   private static boolean upstreamCallOk(CommonResult<?> r) {
      return r != null && r.getCode() != null && r.getCode() == 200;
   }

   private static CommonResult castCommonResult(CommonResult<?> r) {
      return r == null ? CommonResult.error("上游无响应") : r;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult forwardPoolIndependentStart(Long memberNo, String serverId) {
      try {
         Independent server = this.loadIndependentForMember(memberNo, serverId);
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (!isDownstreamResourcePoolIndependent(server)) {
            return CommonResult.error("非资源池独立服务器");
         } else if (ServerStatusEnum.CREATING.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器创建中，请等待......");
         } else if (ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器已锁定，请联系客服");
         } else if (ServerStatusEnum.REINSTALLING.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器重装系统中，请等待......");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            CommonResult<?> r = this.poolUpstreamIndependentClient.call(server, "start", null);
            if (upstreamCallOk(r)) {
               server.setServerStatus(ServerStatusEnum.STARTING.getCode());
               this.updateById(server);
            }

            return castCommonResult(r);
         }
      } catch (Exception var5) {
         log.error("下游资源池独立机开机转发上游失败 serverId={}", serverId, var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult forwardPoolIndependentStop(Long memberNo, String serverId) {
      try {
         Independent server = this.loadIndependentForMember(memberNo, serverId);
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (!isDownstreamResourcePoolIndependent(server)) {
            return CommonResult.error("非资源池独立服务器");
         } else if (!ServerStatusEnum.RUNNING.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器状态异常，无法关闭");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            CommonResult<?> r = this.poolUpstreamIndependentClient.call(server, "stop", null);
            if (upstreamCallOk(r)) {
               server.setServerStatus(ServerStatusEnum.STOPPING.getCode());
               this.updateById(server);
            }

            return castCommonResult(r);
         }
      } catch (Exception var5) {
         log.error("下游资源池独立机关机转发上游失败 serverId={}", serverId, var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult forwardPoolIndependentRestart(Long memberNo, String serverId) {
      try {
         Independent server = this.loadIndependentForMember(memberNo, serverId);
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (!isDownstreamResourcePoolIndependent(server)) {
            return CommonResult.error("非资源池独立服务器");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            CommonResult<?> r = this.poolUpstreamIndependentClient.call(server, "restart", null);
            if (upstreamCallOk(r)) {
               server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
               this.updateById(server);
            }

            return castCommonResult(r);
         }
      } catch (Exception var5) {
         log.error("下游资源池独立机重启转发上游失败 serverId={}", serverId, var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult forwardPoolIndependentPower(Long memberNo, String serverId) {
      try {
         Independent server = this.loadIndependentForMember(memberNo, serverId);
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (!isDownstreamResourcePoolIndependent(server)) {
            return CommonResult.error("非资源池独立服务器");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            CommonResult<?> r = this.poolUpstreamIndependentClient.call(server, "power", null);
            if (upstreamCallOk(r)) {
               server.setServerStatus(ServerStatusEnum.POWERING.getCode());
               this.updateById(server);
            }

            return castCommonResult(r);
         }
      } catch (Exception var5) {
         log.error("下游资源池独立机断电源转发上游失败 serverId={}", serverId, var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult forwardPoolIndependentReinstall(Long memberNo, String serverId, String osClassId, String osVersionId, String osPassword) {
      try {
         if (!StrUtil.isBlank(osClassId) && !StrUtil.isBlank(osVersionId) && !StrUtil.isBlank(osPassword)) {
            String pwdErr = OsInstancePasswordValidator.validate(osPassword);
            if (pwdErr != null) {
               return CommonResult.error(pwdErr);
            } else {
               Independent server = this.loadIndependentForMember(memberNo, serverId);
               if (server == null) {
                  return CommonResult.error("服务器不存在");
               } else if (!isDownstreamResourcePoolIndependent(server)) {
                  return CommonResult.error("非资源池独立服务器");
               } else if (this.isExistWorkingTask(server.getId())) {
                  return CommonResult.error("服务器存在未处理的任务，请等待......");
               } else {
                  JSONObject body = new JSONObject();
                  body.put("osClassId", osClassId);
                  body.put("osVersionId", osVersionId);
                  body.put("osPassword", osPassword);
                  CommonResult<?> r = this.poolUpstreamIndependentClient.call(server, "reinstall", body);
                  if (upstreamCallOk(r)) {
                     server.setServerStatus(ServerStatusEnum.REINSTALLING.getCode());
                     server.setSystemClassId(osClassId);
                     server.setSystemId(osVersionId);
                     server.setSystemPassword(osPassword);
                     BaseIndependentSystemVersion osVer = this.independentSystemVersionApi.selectByOsVersionId(osVersionId);
                     if (osVer != null) {
                        server.setSystemClass(osVer.getOsClassName());
                        server.setSystemName(osVer.getOsName());
                        server.setSystemUserName(osVer.getDefaultUserName());
                        server.setPort(osVer.getDefaultPort());
                     }

                     this.updateById(server);
                  }

                  return castCommonResult(r);
               }
            }
         } else {
            return CommonResult.error("系统分类、系统版本、密码不能为空");
         }
      } catch (Exception var11) {
         log.error("下游资源池独立机重装转发上游失败 serverId={}", serverId, var11);
         return CommonResult.error(var11.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult forwardPoolIndependentUpdatePassword(Long memberNo, String serverId, String osPassword) {
      try {
         if (StrUtil.isBlank(osPassword)) {
            return CommonResult.error("系统密码不能为空");
         } else {
            String pwdErr = OsInstancePasswordValidator.validate(osPassword);
            if (pwdErr != null) {
               return CommonResult.error(pwdErr);
            } else {
               Independent server = this.loadIndependentForMember(memberNo, serverId);
               if (server == null) {
                  return CommonResult.error("服务器不存在");
               } else if (!isDownstreamResourcePoolIndependent(server)) {
                  return CommonResult.error("非资源池独立服务器");
               } else if (this.isExistWorkingTask(server.getId())) {
                  return CommonResult.error("服务器存在未处理的任务，请等待......");
               } else {
                  JSONObject body = new JSONObject();
                  body.put("osPassword", osPassword);
                  CommonResult<?> r = this.poolUpstreamIndependentClient.call(server, "updatePassword", body);
                  if (upstreamCallOk(r)) {
                     server.setServerStatus(ServerStatusEnum.REBOOTING.getCode());
                     server.setSystemPassword(osPassword);
                     this.updateById(server);
                  }

                  return castCommonResult(r);
               }
            }
         }
      } catch (Exception var8) {
         log.error("下游资源池独立机改密转发上游失败 serverId={}", serverId, var8);
         return CommonResult.error(var8.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult forwardPoolIndependentLock(Long memberNo, String serverId, String lockReason) {
      try {
         if (StrUtil.isBlank(lockReason)) {
            return CommonResult.error("锁定原因不能为空");
         } else {
            Independent server = this.loadIndependentForMember(memberNo, serverId);
            if (server == null) {
               return CommonResult.error("服务器不存在");
            } else if (!isDownstreamResourcePoolIndependent(server)) {
               return CommonResult.error("非资源池独立服务器");
            } else if (ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
               return CommonResult.error("服务器已锁定");
            } else if (this.isExistWorkingTask(server.getId())) {
               return CommonResult.error("服务器存在未处理的任务，请等待......");
            } else {
               JSONObject body = new JSONObject();
               body.put("lockReason", lockReason);
               CommonResult<?> r = this.poolUpstreamIndependentClient.call(server, "lock", body);
               if (upstreamCallOk(r)) {
                  server.setServerStatus(ServerStatusEnum.LOCKING.getCode());
                  server.setLockReason(lockReason);
                  this.updateById(server);
               }

               return castCommonResult(r);
            }
         }
      } catch (Exception var7) {
         log.error("下游资源池独立机锁定转发上游失败 serverId={}", serverId, var7);
         return CommonResult.error(var7.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult forwardPoolIndependentUnlock(Long memberNo, String serverId) {
      try {
         Independent server = this.loadIndependentForMember(memberNo, serverId);
         if (server == null) {
            return CommonResult.error("服务器不存在");
         } else if (!isDownstreamResourcePoolIndependent(server)) {
            return CommonResult.error("非资源池独立服务器");
         } else if (!ServerStatusEnum.LOCKED.getCode().equals(server.getServerStatus())) {
            return CommonResult.error("服务器状态异常，无法解锁");
         } else if (this.isExistWorkingTask(server.getId())) {
            return CommonResult.error("服务器存在未处理的任务，请等待......");
         } else {
            CommonResult<?> r = this.poolUpstreamIndependentClient.call(server, "unlock", null);
            if (upstreamCallOk(r)) {
               this.updateIndependentStatusAndLockReason(server.getId(), ServerStatusEnum.UNLOCKING.getCode(), null);
            }

            return castCommonResult(r);
         }
      } catch (Exception var5) {
         log.error("下游资源池独立机解锁转发上游失败 serverId={}", serverId, var5);
         return CommonResult.error(var5.getMessage());
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void syncFromUpstream(BaseIndependent upstream) {
      if (upstream != null && !StrUtil.isBlank(upstream.getId())) {
         Independent existing = (Independent)this.getById(upstream.getId());
         if (existing == null) {
            Independent toSave = new Independent();
            BeanUtils.copyProperties(upstream, toSave);
            this.save(toSave);
            log.info("上游同步 - 新增独立服务器记录，ID: {}", toSave.getId());
         } else {
            if (StrUtil.isNotBlank(upstream.getServerStatus())) {
               existing.setServerStatus(upstream.getServerStatus());
               if (!ServerStatusEnum.LOCKED.getCode().equals(upstream.getServerStatus())
                  && !ServerStatusEnum.LOCKING.getCode().equals(upstream.getServerStatus())) {
                  existing.setLockReason("");
               }
            }

            if (StrUtil.isNotBlank(upstream.getLockReason())) {
               existing.setLockReason(upstream.getLockReason());
            }

            if (StrUtil.isNotBlank(upstream.getServerIp())) {
               existing.setServerIp(upstream.getServerIp());
            }

            if (upstream.getPort() != null) {
               existing.setPort(upstream.getPort());
            }

            if (StrUtil.isNotBlank(upstream.getSystemUserName())) {
               existing.setSystemUserName(upstream.getSystemUserName());
            }

            if (StrUtil.isNotBlank(upstream.getSystemPassword())) {
               existing.setSystemPassword(upstream.getSystemPassword());
            }

            if (upstream.getBandwidth() != null) {
               existing.setBandwidth(upstream.getBandwidth());
            }

            if (upstream.getDefense() != null) {
               existing.setDefense(upstream.getDefense());
            }

            if (StrUtil.isNotBlank(upstream.getSystemName())) {
               existing.setSystemName(upstream.getSystemName());
            }

            if (StrUtil.isNotBlank(upstream.getSystemId())) {
               existing.setSystemId(upstream.getSystemId());
            }

            if (StrUtil.isNotBlank(upstream.getSystemClassId())) {
               existing.setSystemClassId(upstream.getSystemClassId());
            }

            if (StrUtil.isNotBlank(upstream.getSystemClass())) {
               existing.setSystemClass(upstream.getSystemClass());
            }

            if (StrUtil.isNotBlank(upstream.getUpstreamServerId())) {
               existing.setUpstreamServerId(upstream.getUpstreamServerId());
            }

            if (upstream.getBusinessStatus() != null) {
               existing.setBusinessStatus(upstream.getBusinessStatus());
            }

            this.updateById(existing);
            log.info("上游同步 - 更新独立服务器成功，ID: {}", existing.getId());
         }
      } else {
         log.warn("syncFromUpstream 独立服务器数据为空或缺少 id，跳过");
      }
   }

   private static String resolveIndependentPoolOrderNo(String orderNo, Independent server) {
      if (StrUtil.isNotBlank(orderNo)) {
         return orderNo;
      } else {
         return server != null ? server.getOrderNo() : null;
      }
   }

   private void validateEditParam(IndependentEditParam editParam) {
      if (editParam.getPort() != null && editParam.getPort() <= 0) {
         throw new CommonException("远程端口必须大于0", new Object[0]);
      } else if (editParam.getIpSize() != null && editParam.getIpSize() < 0) {
         throw new CommonException("IP数量不能小于0", new Object[0]);
      } else if (editParam.getDefense() != null && editParam.getDefense() < 0) {
         throw new CommonException("防御不能小于0", new Object[0]);
      } else if (editParam.getBandwidth() != null && editParam.getBandwidth() < 0) {
         throw new CommonException("带宽不能小于0", new Object[0]);
      } else {
         if (editParam.getServerStatus() != null) {
            List<String> editableStatus = Arrays.asList(
               ServerStatusEnum.CREATING.getCode(),
               ServerStatusEnum.RUNNING.getCode(),
               ServerStatusEnum.STOP.getCode(),
               ServerStatusEnum.LOCKED.getCode(),
               ServerStatusEnum.REINSTALLING.getCode()
            );
            if (!editableStatus.contains(editParam.getServerStatus())) {
               throw new CommonException("主机状态仅支持：CREATING、RUNNING、STOP、LOCKED、REINSTALLING", new Object[0]);
            }
         }

         if (editParam.getBusinessStatus() != null) {
            List<Integer> editableBusinessStatus = Arrays.asList(BusinessStatusEnum.NORMAL.getCode(), BusinessStatusEnum.EXPIRED.getCode());
            if (!editableBusinessStatus.contains(editParam.getBusinessStatus())) {
               throw new CommonException("业务状态仅支持：1（正常）、-1（过期）", new Object[0]);
            }
         }
      }
   }

   private static String resolveIndependentUpstreamPk(Independent server) {
      return server != null ? server.getUpstreamServerId() : null;
   }

   private String resolveIndependentResourcePoolId(Independent server) {
      if (server == null) {
         return null;
      } else if (StrUtil.isNotBlank(server.getResourcePoolId())) {
         return server.getResourcePoolId();
      } else {
         if (StrUtil.isNotBlank(server.getMachineId())) {
            try {
               BaseAdminMachine m = this.adminMachineApi.findMachineById(server.getMachineId());
               if (m != null && StrUtil.isNotBlank(m.getResourcePoolId())) {
                  return m.getResourcePoolId();
               }
            } catch (Exception var3) {
            }
         }

         return null;
      }
   }
}
