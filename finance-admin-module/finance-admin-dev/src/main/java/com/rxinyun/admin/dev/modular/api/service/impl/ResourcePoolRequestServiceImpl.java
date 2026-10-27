package com.rxinyun.admin.dev.modular.api.service.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePool;
import com.rxinyun.admin.dev.modular.api.entity.ResourcePoolRequest;
import com.rxinyun.admin.dev.modular.api.mapper.ResourcePoolMapper;
import com.rxinyun.admin.dev.modular.api.mapper.ResourcePoolRequestMapper;
import com.rxinyun.admin.dev.modular.api.param.ResourcePoolRequestPageParam;
import com.rxinyun.admin.dev.modular.api.service.ResourcePoolRequestService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.ResourcePoolRequestStatusEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.SnowflakeIdGenerator;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResourcePoolRequestServiceImpl extends ServiceImpl<ResourcePoolRequestMapper, ResourcePoolRequest> implements ResourcePoolRequestService {
   @Resource
   private ResourcePoolMapper resourcePoolMapper;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void create(ResourcePoolRequest resourcePoolRequest) {
      if (resourcePoolRequest.getId() == null) {
         resourcePoolRequest.setId(SnowflakeIdGenerator.generateId());
      }

      if (resourcePoolRequest.getRequestStatus() == null) {
         resourcePoolRequest.setRequestStatus(ResourcePoolRequestStatusEnum.PENDING.getCode());
      }

      this.save(resourcePoolRequest);
   }

   @Override
   public ResourcePoolRequest getByOrderNo(String orderNo) {
      return (ResourcePoolRequest)this.getOne(
         (Wrapper<ResourcePoolRequest>)((LambdaQueryWrapper<ResourcePoolRequest>)((LambdaQueryWrapper<ResourcePoolRequest>)new LambdaQueryWrapper<ResourcePoolRequest>().eq(ResourcePoolRequest::getOrderNo, orderNo))
               .orderByDesc(CommonEntity::getCreateTime))
            .last("LIMIT 1")
      );
   }

   @Override
   public List<ResourcePoolRequest> listPendingRequests() {
      return this.list(
         (Wrapper<ResourcePoolRequest>)((LambdaQueryWrapper<ResourcePoolRequest>)new LambdaQueryWrapper<ResourcePoolRequest>().eq(ResourcePoolRequest::getRequestStatus, ResourcePoolRequestStatusEnum.PENDING.getCode()))
            .orderByAsc(CommonEntity::getCreateTime)
      );
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void updateStatus(String id, Integer status, String callbackData) {
      ResourcePoolRequest request = this.getById(id);
      if (request == null) {
         throw new RuntimeException("资源池请求不存在，ID为：" + id);
      } else {
         request.setRequestStatus(status);
         if (callbackData != null) {
            request.setCallbackData(callbackData);
         }

         this.updateById(request);
      }
   }

   @Override
   public ResourcePoolRequest getById(String id) {
      return (ResourcePoolRequest)super.getById(id);
   }

   @Override
   public Page<ResourcePoolRequest> page(ResourcePoolRequestPageParam param) {
      QueryWrapper<ResourcePoolRequest> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(param.getOrderNo())) {
         queryWrapper.lambda().like(ResourcePoolRequest::getOrderNo, param.getOrderNo());
      }

      if (StrUtil.isNotBlank(param.getResourcePoolId())) {
         queryWrapper.lambda().eq(ResourcePoolRequest::getResourcePoolId, param.getResourcePoolId());
      }

      if (StrUtil.isNotBlank(param.getOperationType())) {
         queryWrapper.lambda().eq(ResourcePoolRequest::getOperationType, param.getOperationType());
      }

      if (param.getRequestStatus() != null) {
         queryWrapper.lambda().eq(ResourcePoolRequest::getRequestStatus, param.getRequestStatus());
      }

      if (param.getMemberNo() != null) {
         queryWrapper.lambda().eq(ResourcePoolRequest::getMemberNo, param.getMemberNo());
      }

      if (StrUtil.isNotBlank(param.getAServerId())) {
         queryWrapper.lambda().eq(ResourcePoolRequest::getAServerId, param.getAServerId());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{param.getSortField(), param.getSortOrder()})) {
         CommonSortOrderEnum.validate(param.getSortOrder());
         queryWrapper.orderBy(true, param.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(param.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      Page<ResourcePoolRequest> page = (Page<ResourcePoolRequest>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      this.fillResourcePoolInfo(page.getRecords());
      return page;
   }

   private void fillResourcePoolInfo(List<ResourcePoolRequest> records) {
      if (!ObjectUtil.isEmpty(records)) {
         List<String> poolIds = records.stream()
            .map(ResourcePoolRequest::getResourcePoolId)
            .filter(CharSequenceUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
         if (!ObjectUtil.isEmpty(poolIds)) {
            List<ResourcePool> pools = this.resourcePoolMapper.selectBatchIds(poolIds);
            if (!ObjectUtil.isEmpty(pools)) {
               Map<String, ResourcePool> poolMap = pools.stream().collect(Collectors.toMap(ResourcePool::getId, p -> (ResourcePool)p, (a, b) -> a));

               for (ResourcePoolRequest record : records) {
                  if (record != null && !StrUtil.isBlank(record.getResourcePoolId())) {
                     ResourcePool pool = poolMap.get(record.getResourcePoolId());
                     if (pool != null) {
                        record.setResourcePoolName(pool.getName());
                        record.setResourcePoolType(pool.getPoolType());
                     }
                  }
               }
            }
         }
      }
   }
}
