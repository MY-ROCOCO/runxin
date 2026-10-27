package com.rxinyun.admin.business.modular.independent.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.business.modular.independent.async.DedicatedServerManualWorkNotificationPublisher;
import com.rxinyun.admin.business.modular.independent.entity.Independent;
import com.rxinyun.admin.business.modular.independent.entity.IndependentWork;
import com.rxinyun.admin.business.modular.independent.mapper.IndependentWorkMapper;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkAddParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkIdParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkPageParam;
import com.rxinyun.admin.business.modular.independent.param.IndependentWorkStatusUpdateParam;
import com.rxinyun.admin.business.modular.independent.response.IndependentWorkResponse;
import com.rxinyun.admin.business.modular.independent.service.IndependentIpService;
import com.rxinyun.admin.business.modular.independent.service.IndependentService;
import com.rxinyun.admin.business.modular.independent.service.IndependentWorkService;
import com.rxinyun.auth.core.pojo.SaBaseLoginUser;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.OperateTypeEnum;
import com.rxinyun.common.enums.ProcessingStatusEnum;
import com.rxinyun.common.enums.business.BusinessStatusEnum;
import com.rxinyun.common.enums.business.ServerStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import com.rxinyun.dev.api.ResourceChangeLogApi;
import com.rxinyun.dev.core.pojo.BaseResourceChangeLog;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.api.IndependentRegionApi;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import com.rxinyun.production.core.pojo.BaseIndependentRegion;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndependentWorkServiceImpl extends ServiceImpl<IndependentWorkMapper, IndependentWork> implements IndependentWorkService {
   private static final Logger log = LoggerFactory.getLogger(IndependentWorkServiceImpl.class);
   @Resource
   private IndependentService independentService;
   @Resource
   private IndependentIpService independentIpService;
   @Resource
   private IndependentLineApi independentLineApi;
   @Resource
   private IndependentRegionApi independentRegionApi;
   @Resource
   private ResourceChangeLogApi resourceChangeLogApi;
   @Resource
   private DedicatedServerManualWorkNotificationPublisher dedicatedServerManualWorkNotificationPublisher;

   @Override
   public Page<IndependentWorkResponse> page(IndependentWorkPageParam pageParam) {
      QueryWrapper<IndependentWork> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isNotEmpty(pageParam.getBusinessStatus())) {
         queryWrapper.lambda().and(q -> q.eq(IndependentWork::getBusinessStatus, pageParam.getBusinessStatus()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getOperateType())) {
         queryWrapper.lambda().and(q -> q.eq(IndependentWork::getOperateType, pageParam.getOperateType()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
         queryWrapper.lambda().and(q -> q.eq(IndependentWork::getMemberNo, pageParam.getMemberNo()));
      }

      if (ObjectUtil.isNotEmpty(pageParam.getSearchKey())) {
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
         if (!StrUtil.equals(pageParam.getSortField(), "id")) {
            queryWrapper.lambda().orderByDesc(IndependentWork::getId);
         }
      } else {
         queryWrapper.lambda().orderByDesc(IndependentWork::getCreateTime).orderByDesc(IndependentWork::getId);
      }

      Page<IndependentWork> page = (Page<IndependentWork>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<IndependentWorkResponse> pageResultResponse = new Page();
      if (CollectionUtils.isNotEmpty(page.getRecords())) {
         List<IndependentWorkResponse> responseList = page.getRecords().stream().map(this::toIndependentWorkResponse).collect(Collectors.toList());
         pageResultResponse.setRecords(responseList);
      }

      pageResultResponse.setCurrent(page.getCurrent());
      pageResultResponse.setSize(page.getSize());
      pageResultResponse.setTotal(page.getTotal());
      pageResultResponse.setPages(page.getPages());
      return pageResultResponse;
   }

   @Override
   public IndependentWorkResponse detail(IndependentWorkIdParam idParam) {
      IndependentWork row = this.queryEntity(idParam.getId());
      return this.toIndependentWorkResponse(row);
   }

   private IndependentWorkResponse toIndependentWorkResponse(IndependentWork memberIndependent) {
      IndependentWorkResponse independentWorkResponse = new IndependentWorkResponse();
      BeanUtils.copyProperties(memberIndependent, independentWorkResponse);
      if (memberIndependent.getRegionId() != null) {
         BaseIndependentRegion memberIndependentRegion = this.independentRegionApi.selectByRegionId(memberIndependent.getRegionId());
         if (memberIndependentRegion != null) {
            independentWorkResponse.setRegionName(memberIndependentRegion.getName());
         }
      }

      if (memberIndependent.getLineId() != null) {
         BaseIndependentLine baseIndependentLine = this.independentLineApi.selectByLineId(memberIndependent.getLineId());
         if (baseIndependentLine != null) {
            independentWorkResponse.setLineName(baseIndependentLine.getName());
         }
      }

      if (StrUtil.isNotBlank(memberIndependent.getIndependentId())) {
         Independent independent = (Independent)this.independentService.getById(memberIndependent.getIndependentId());
         if (independent != null) {
            independentWorkResponse.setIndependentIp(independent.getServerIp());
         }
      }

      return independentWorkResponse;
   }

   @Override
   public void add(IndependentWorkAddParam independentWorkAddParam) {
      IndependentWork independentWork = (IndependentWork)BeanUtil.toBean(independentWorkAddParam, IndependentWork.class);
      this.save(independentWork);
      String independentIp = null;
      if (StrUtil.isNotBlank(independentWorkAddParam.getIndependentId())) {
         Independent independent = (Independent)this.independentService.getById(independentWorkAddParam.getIndependentId());
         if (independent != null) {
            independentIp = independent.getServerIp();
         }
      }

      this.dedicatedServerManualWorkNotificationPublisher
         .publishAfterWorkCreated(independentWorkAddParam.getIndependentId(), independentIp, independentWorkAddParam.getOperateType());
   }

   @Override
   public boolean isExistWorkingTask(String independentId) {
      IndependentWork independentWork = new IndependentWork();
      independentWork.setIndependentId(independentId);
      independentWork.setBusinessStatus(0);
      List<IndependentWork> independentWorkList = this.list(new LambdaQueryWrapper(independentWork));
      return independentWorkList.size() > 0;
   }

   @Transactional
   @Override
   public void updateStatus(IndependentWorkStatusUpdateParam updateParam) {
      IndependentWork independentWork = this.queryEntity(updateParam.getId());
      independentWork.setBusinessStatus(updateParam.getBusinessStatus());
      SaBaseLoginUser loginUser = StpLoginUserUtil.getLoginUser();
      independentWork.setIndependentUser(loginUser.getId());
      independentWork.setCompleteTime(new Date());
      this.updateById(independentWork);
      log.info("更新独立服务器处理工单状态成功，工单ID：{}", independentWork.getId());
      if (Objects.equals(independentWork.getBusinessStatus(), ProcessingStatusEnum.PROCESSING_COMPLETE.getCode())) {
         Independent independent = this.independentService.queryEntity(independentWork.getIndependentId());
         log.info("工单处理完成后，变更服务器状态，工单类型={}，独立服务器ID={}", independentWork.getOperateType(), independentWork.getIndependentId());
         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.DELETE.getCode())
            || Objects.equals(independentWork.getOperateType(), OperateTypeEnum.EXPIRE_DELETE.getCode())) {
            independent.setServerStatus(ServerStatusEnum.STOP.getCode());
            independent.setBusinessStatus(BusinessStatusEnum.DELETE.getCode());
            this.independentService.updateById(independent);
            Independent reloaded = this.independentService.queryEntity(independent.getId());
            this.saveIndependentResourceChangeLogForDownstream(reloaded);
            return;
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.CREATE.getCode())) {
            independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.START.getCode())) {
            independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.STOP.getCode())) {
            independent.setServerStatus(ServerStatusEnum.STOP.getCode());
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.EXPIRE_STOP.getCode())) {
            if (BusinessStatusEnum.NORMAL.getCode().equals(independent.getBusinessStatus())) {
               log.info("独立服务器已续费恢复正常，忽略到期关机工单回写关机状态 independentId={}", independent.getId());
            } else {
               independent.setServerStatus(ServerStatusEnum.STOP.getCode());
            }
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.REINSTALL.getCode())) {
            independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.UPGRADE.getCode())) {
            independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.RESET_PASSWORD.getCode())) {
            independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.SHUT_DOWN.getCode())) {
            independent.setServerStatus(ServerStatusEnum.POWER.getCode());
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.REBOOT.getCode())) {
            independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.RECHARGE.getCode())) {
            this.closePendingExpireStopWorks(independent.getId());
            this.independentService
               .update(
                  (Wrapper<Independent>)((LambdaUpdateWrapper<Independent>)((LambdaUpdateWrapper<Independent>)((LambdaUpdateWrapper<Independent>)new LambdaUpdateWrapper<Independent>()
                              .eq(Independent::getId, independent.getId()))
                           .set(Independent::getBusinessStatus, BusinessStatusEnum.NORMAL.getCode()))
                        .set(Independent::getServerStatus, ServerStatusEnum.RUNNING.getCode()))
                     .set(CommonEntity::getUpdateTime, new Date())
               );
            log.info("续费工单完成，独立服务器运行状态已恢复开机 independentId={}", independent.getId());
            Independent reloaded = this.independentService.queryEntity(independent.getId());
            this.saveIndependentResourceChangeLogForDownstream(reloaded);
            return;
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.LOCK.getCode())) {
            independent.setServerStatus(ServerStatusEnum.LOCKED.getCode());
         }

         if (Objects.equals(independentWork.getOperateType(), OperateTypeEnum.UNLOCK.getCode())) {
            independent.setServerStatus(ServerStatusEnum.RUNNING.getCode());
            this.independentService
               .update(
                  (Wrapper<Independent>)((LambdaUpdateWrapper<Independent>)((LambdaUpdateWrapper<Independent>)((LambdaUpdateWrapper<Independent>)new LambdaUpdateWrapper<Independent>()
                              .eq(Independent::getId, independent.getId()))
                           .set(Independent::getServerStatus, ServerStatusEnum.RUNNING.getCode()))
                        .set(Independent::getLockReason, null))
                     .set(CommonEntity::getUpdateTime, new Date())
               );
            Independent reloaded = this.independentService.queryEntity(independent.getId());
            this.saveIndependentResourceChangeLogForDownstream(reloaded);
            return;
         }

         this.independentService.updateById(independent);
         Independent reloaded = this.independentService.queryEntity(independent.getId());
         this.saveIndependentResourceChangeLogForDownstream(reloaded);
      }
   }

   private void saveIndependentResourceChangeLogForDownstream(Independent independent) {
      if (independent != null && !StrUtil.isBlank(independent.getId()) && independent.getMemberNo() != null) {
         String upstreamId = StrUtil.blankToDefault(independent.getUpstreamServerId(), independent.getId());
         String orderNo = this.independentService.findLatestOrderNoByServerId(independent.getId());
         BaseResourceChangeLog changeLog = new BaseResourceChangeLog();
         changeLog.setId(SnowflakeIdGenerator.generateId());
         changeLog.setOrderNo(StrUtil.blankToDefault(orderNo, ""));
         changeLog.setMemberNo(independent.getMemberNo());
         changeLog.setChangeType("UPDATE");
         changeLog.setProcessed(0);
         changeLog.setUpstreamServerId(upstreamId);
         changeLog.setPoolSyncTarget("INDEPENDENT");
         changeLog.setCreateTime(new Date());
         this.resourceChangeLogApi.save(changeLog);
         log.info("✅ 独立服务器工单完成，已写入 UPDATE 资源变更日志，orderNo={}，serverId={}，upstreamServerId={}", new Object[]{orderNo, independent.getId(), upstreamId});
      }
   }

   @Override
   public void closePendingExpireStopWorks(String independentId) {
      if (!StrUtil.isBlank(independentId)) {
         boolean closed = this.update(
            (Wrapper<IndependentWork>)((LambdaUpdateWrapper<IndependentWork>)((LambdaUpdateWrapper<IndependentWork>)((LambdaUpdateWrapper<IndependentWork>)((LambdaUpdateWrapper<IndependentWork>)new LambdaUpdateWrapper<IndependentWork>()
                           .eq(IndependentWork::getIndependentId, independentId))
                        .eq(IndependentWork::getOperateType, OperateTypeEnum.EXPIRE_STOP.getCode()))
                     .in(
                        IndependentWork::getBusinessStatus, new Object[]{ProcessingStatusEnum.UNPROCESSED.getCode(), ProcessingStatusEnum.PROCESSING.getCode()}
                     ))
                  .set(IndependentWork::getBusinessStatus, ProcessingStatusEnum.PROCESSING_COMPLETE.getCode()))
               .set(IndependentWork::getCompleteTime, new Date())
         );
         if (closed) {
            log.info("续费后已关闭未完成的到期关机工单 independentId={}", independentId);
         }
      }
   }

   @Override
   public IndependentWork queryEntity(String id) {
      IndependentWork independentWork = (IndependentWork)this.getById(id);
      if (ObjectUtil.isEmpty(independentWork)) {
         throw new CommonException("独立服务器处理工单不存在，id值为：{}", new Object[]{id});
      } else {
         return independentWork;
      }
   }
}
