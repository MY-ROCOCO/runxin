package com.rxinyun.admin.workorder.modular.workorder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrder;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrderType;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.TypeRelevanceParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.TypeStaticParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderTypeAddParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderTypeEditParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderTypePageParam;
import com.rxinyun.admin.workorder.modular.workorder.mapper.WorkOrderTypeMapper;
import com.rxinyun.admin.workorder.modular.workorder.service.WorkOrderService;
import com.rxinyun.admin.workorder.modular.workorder.service.WorkOrderTypeService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderTypeServiceImpl extends ServiceImpl<WorkOrderTypeMapper, WorkOrderType> implements WorkOrderTypeService {
   @Resource
   WorkOrderTypeMapper workOrderTypeMapper;
   @Resource
   WorkOrderService workOrderService;

   @Override
   public void save(WorkOrderTypeAddParam workOrderTypeAddParam) {
      WorkOrderType workOrderType = (WorkOrderType)BeanUtil.toBean(workOrderTypeAddParam, WorkOrderType.class);
      workOrderType.setId(null);
      this.workOrderTypeMapper.insert(workOrderType);
   }

   @Override
   public CommonResult delete(String id) {
      List<WorkOrder> workOrder = this.workOrderService.getOrderIsExist(id);
      if (null == workOrder) {
         this.workOrderTypeMapper.deleteById(id);
         return CommonResult.ok("删除成功");
      } else {
         return CommonResult.error("该工单类型下有工单，不能删除");
      }
   }

   @Override
   public Page<WorkOrderType> page(WorkOrderTypePageParam pageParam) {
      QueryWrapper<WorkOrderType> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(
            true, pageParam.getSortOrder().toUpperCase().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(WorkOrderType::getId);
      }

      return (Page<WorkOrderType>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   @Override
   public CommonResult<TypeRelevanceParam> isRelevnce(TypeRelevanceParam typeRelevanceParam) {
      WorkOrderType workOrderType = (WorkOrderType)BeanUtil.toBean(typeRelevanceParam, WorkOrderType.class);
      int t = workOrderType.getIsRelevance();
      if (t == 0) {
         t = 1;
      } else if (t == 1) {
         t = 0;
      }

      workOrderType.setIsRelevance(t);
      System.out.println(t);
      ((this.lambdaUpdate()
               .set(workOrderType.getIsRelevance() != null, WorkOrderType::getIsRelevance, workOrderType.getIsRelevance()))
            .eq(workOrderType.getId() != null, WorkOrderType::getId, workOrderType.getId()))
         .update();
      return CommonResult.data((TypeRelevanceParam)BeanUtil.toBean(workOrderType, TypeRelevanceParam.class));
   }

   @Override
   public CommonResult isVisible(TypeStaticParam typeStaticParam) {
      WorkOrderType workOrderType = (WorkOrderType)BeanUtil.toBean(typeStaticParam, WorkOrderType.class);
      int t = workOrderType.getStatus();
      if (t == 0) {
         t = 1;
      } else if (t == 1) {
         t = 0;
      }

      workOrderType.setStatus(t);
      ((this.lambdaUpdate()
               .set(workOrderType.getStatus() != null, WorkOrderType::getStatus, workOrderType.getStatus()))
            .eq(workOrderType.getId() != null, WorkOrderType::getId, workOrderType.getId()))
         .update();
      return CommonResult.data((TypeStaticParam)BeanUtil.toBean(workOrderType, TypeStaticParam.class));
   }

   @Override
   public CommonResult update(WorkOrderTypeEditParam workOrderTypeParam) {
      WorkOrderType workOrderType = (WorkOrderType)BeanUtil.toBean(workOrderTypeParam, WorkOrderType.class);
      ((((this.lambdaUpdate()
                     .set(workOrderType.getTypeName() != null, WorkOrderType::getTypeName, workOrderType.getTypeName()))
                  .set(workOrderType.getIsRelevance() != null, WorkOrderType::getIsRelevance, workOrderType.getIsRelevance()))
               .set(WorkOrderType::getStatus, 0))
            .eq(workOrderType.getId() != null, WorkOrderType::getId, workOrderType.getId()))
         .update();
      return CommonResult.ok();
   }
}
