package com.rxinyun.admin.workorder.modular.workorder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrder;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrderMessage;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrderType;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderPageParam;
import com.rxinyun.admin.workorder.modular.workorder.mapper.WorkOrderMapper;
import com.rxinyun.admin.workorder.modular.workorder.mapper.WorkOrderMessageMapper;
import com.rxinyun.admin.workorder.modular.workorder.mapper.WorkOrderTypeMapper;
import com.rxinyun.admin.workorder.modular.workorder.response.WorkOrderFindResponse;
import com.rxinyun.admin.workorder.modular.workorder.response.WorkOrderPageResponse;
import com.rxinyun.admin.workorder.modular.workorder.service.WorkOrderService;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.common.util.date.enums.DateResponseKeyEnum;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.sys.api.SysUserApi;
import com.rxinyun.workorder.api.AdminWorkOrederApi;
import com.rxinyun.workorder.pojo.WorkOrderStatisticsResponse;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService, AdminWorkOrederApi {
   @Resource
   private WorkOrderMapper workOrderMapper;
   @Resource
   private MemberSaBaseLoginUserApi saBaseLoginUserApi;
   @Resource
   private SysUserApi sysUserApi;
   @Resource
   private WorkOrderTypeMapper workOrderTypeMapper;
   @Resource
   private WorkOrderMessageMapper workOrderMessageMapper;

   @Override
   public CommonResult<WorkOrderFindResponse> findMessage(String workOrderId) {
      WorkOrderFindResponse workOrderFindResponse = new WorkOrderFindResponse();
      if (null == workOrderId) {
         return CommonResult.error("工单不存在");
      } else {
         WorkOrder workOrder = (WorkOrder)this.workOrderMapper.selectById(workOrderId);
         if (null == workOrder) {
            return CommonResult.error("工单id不存在");
         } else {
            BeanUtil.copyProperties(workOrder, workOrderFindResponse, new String[0]);
            SaBaseClientLoginUser saBaseClientLoginUser = this.saBaseLoginUserApi.getClientUserById(workOrderFindResponse.getClientId());
            workOrderFindResponse.setPhone(saBaseClientLoginUser.getPhone());
            if (null != workOrder.getAdminId()) {
               JSONObject adminNick = this.sysUserApi.getUserByIdWithoutException(workOrder.getAdminId());
               if (null == adminNick) {
                  return CommonResult.error("管理昵称为空");
               }

               workOrderFindResponse.setNickname(adminNick.get("account").toString());
            }

            return CommonResult.data(workOrderFindResponse);
         }
      }
   }

   @Override
   public CommonResult setStatusOut(String orderId) {
      WorkOrder workOrder = (WorkOrder)this.workOrderMapper.selectById(orderId);
      if (null == workOrder) {
         return CommonResult.error("该数据不存在");
      } else if (workOrder.getStatusOut() == 0) {
         workOrder.setStatusOut(1);
         workOrder.setOrderEndTime(TimeUtil.now());
         this.workOrderMapper.updateById(workOrder);
         return CommonResult.data(workOrder);
      } else {
         return CommonResult.ok("工单已解决");
      }
   }

   @Override
   public Page<WorkOrderPageResponse> page(WorkOrderPageParam workOrderPageParam) {
      QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (null != workOrderPageParam.getStatusOut()) {
         queryWrapper.eq("STATUS_OUT", workOrderPageParam.getStatusOut());
      }

      if (null != workOrderPageParam.getKeyWord()) {
         String keyword = workOrderPageParam.getKeyWord();
         ((LambdaQueryWrapper<WorkOrder>)((LambdaQueryWrapper<WorkOrder>)queryWrapper.lambda().like(WorkOrder::getId, keyword)).or()).like(WorkOrder::getTitle, keyword);
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{workOrderPageParam.getSortField(), workOrderPageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(workOrderPageParam.getSortOrder());
         queryWrapper.orderBy(
            true,
            workOrderPageParam.getSortOrder().toUpperCase().equals(CommonSortOrderEnum.ASC.getValue()),
            StrUtil.toUnderlineCase(workOrderPageParam.getSortField())
         );
      } else {
         queryWrapper.lambda().orderByDesc(WorkOrder::getOrderCreateTime);
      }

      Page<WorkOrder> page = (Page<WorkOrder>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      List<WorkOrderPageResponse> list = BeanUtil.copyToList(page.getRecords(), WorkOrderPageResponse.class);
      List<WorkOrderType> workOrderTypeList = this.workOrderTypeMapper.selectList(new QueryWrapper(WorkOrderType.class));
      Map<String, String> workOrderTypeMap = workOrderTypeList.stream().collect(Collectors.toMap(WorkOrderType::getId, WorkOrderType::getTypeName));

      for (WorkOrderPageResponse workOrderPageResponse : list) {
         if (null != workOrderPageResponse.getClientId()) {
            SaBaseClientLoginUser saBaseClientLoginUser = this.saBaseLoginUserApi.getClientUserById(workOrderPageResponse.getClientId());
            if (null != saBaseClientLoginUser) {
               workOrderPageResponse.setClientNickName(saBaseClientLoginUser.getAccount());
            }

            String typeName = workOrderTypeMap.get(workOrderPageResponse.getTypeId());
            if (null != typeName) {
               workOrderPageResponse.setTypeName(typeName);
            }
         }
      }

      Page<WorkOrderPageResponse> responsePage = new Page();
      responsePage.setCurrent(page.getCurrent());
      responsePage.setSize(page.getSize());
      responsePage.setTotal(page.getTotal());
      responsePage.setPages(page.getPages());
      responsePage.setRecords(list);
      return responsePage;
   }

   @Override
   public List<WorkOrder> getOrderIsExist(String typeId) {
      List<WorkOrder> list = this.workOrderMapper.selectByTypeId(typeId);
      return list.size() > 0 ? list : null;
   }

   public WorkOrderStatisticsResponse getInstance(HashMap<String, LocalDateTime> map) {
      WorkOrderStatisticsResponse workOrderStatisticsResponse = new WorkOrderStatisticsResponse();

      for (WorkOrder workOrder : this.workOrderMapper
         .selectList(
            (Wrapper<WorkOrder>)((LambdaQueryWrapper<WorkOrder>)new QueryWrapper<WorkOrder>()
                  .lambda()
                  .ge(WorkOrder::getOrderCreateTime, map.get(DateResponseKeyEnum.START_TIME_NAME.getValue())))
               .le(WorkOrder::getOrderCreateTime, map.get(DateResponseKeyEnum.END_TIME_NAME.getValue()))
         )) {
         if (workOrder.getStatusOut() == 1) {
            workOrderStatisticsResponse.setAccepted(workOrderStatisticsResponse.getAccepted() + 1L);
         } else {
            Long count = this.workOrderMessageMapper
               .selectCount(
                  (Wrapper<WorkOrderMessage>)((LambdaQueryWrapper<WorkOrderMessage>)new QueryWrapper<WorkOrderMessage>().lambda().eq(WorkOrderMessage::getWorkOrderId, workOrder.getId()))
                     .isNotNull(WorkOrderMessage::getAdminId)
               );
            if (count > 1L) {
               workOrderStatisticsResponse.setCommunicate(workOrderStatisticsResponse.getCommunicate() + 1L);
            } else {
               workOrderStatisticsResponse.setNoAccepted(workOrderStatisticsResponse.getNoAccepted() + 1L);
            }
         }
      }

      return workOrderStatisticsResponse;
   }

   public Long getUserOrderCount(Long userId) {
      QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper();
      queryWrapper.lambda().eq(WorkOrder::getClientId, userId);
      return this.count(queryWrapper);
   }

   public Long getUserUnprocessedOrderCount(Long userId) {
      QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper();
      ((LambdaQueryWrapper<WorkOrder>)queryWrapper.lambda().eq(WorkOrder::getClientId, userId)).eq(WorkOrder::getStatusOut, 0);
      return this.count(queryWrapper);
   }
}
