package com.rxinyun.member.workorder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.business.api.MemberIndependentApi;
import com.rxinyun.business.api.MemberServerApi;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.dev.api.DevSseApi;
import com.rxinyun.dev.api.ToolsNotificationApi;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.workorder.mapper.WorkOrderMemberMapper;
import com.rxinyun.member.workorder.mapper.WorkOrderMessageMemberMapper;
import com.rxinyun.member.workorder.mapper.WorkOrderTypeMemberMapper;
import com.rxinyun.member.workorder.pojo.dto.WorkOrderAddParam;
import com.rxinyun.member.workorder.pojo.dto.WorkOrderPageParam;
import com.rxinyun.member.workorder.pojo.entity.WorkOrder;
import com.rxinyun.member.workorder.pojo.entity.WorkOrderMessage;
import com.rxinyun.member.workorder.pojo.entity.WorkOrderType;
import com.rxinyun.member.workorder.pojo.vo.WorkOrderFindVO;
import com.rxinyun.member.workorder.pojo.vo.WorkOrderPageResponse;
import com.rxinyun.member.workorder.service.WorkOrderMemberService;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkOrderMemberServiceImpl extends ServiceImpl<WorkOrderMemberMapper, WorkOrder> implements WorkOrderMemberService {
   @Resource
   private WorkOrderMemberMapper workOrderMemberMapper;
   @Resource
   private WorkOrderTypeMemberMapper workOrderTypeMemberMapper;
   @Resource
   private WorkOrderMessageMemberMapper workOrderMessageMemberMapper;
   @Autowired
   private ToolsNotificationApi toolsNotificationApi;
   @Resource
   private DevSseApi devSseApi;
   @Resource
   private MemberServerApi memberServerApi;
   @Resource
   private MemberIndependentApi memberIndependentApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult add(WorkOrderAddParam workOrderAddParam) {
      SaBaseClientLoginUser clientLoginUser = this.currentClient();
      WorkOrder workOrder = (WorkOrder)BeanUtil.toBean(workOrderAddParam, WorkOrder.class);
      workOrder.setClientId(clientLoginUser.getId());
      workOrder.setProductIp(this.resolveProductIp(workOrderAddParam.getProductName(), workOrderAddParam.getProductId(), clientLoginUser.getId()));
      workOrder.setOrderCreateTime(TimeUtil.now());
      this.workOrderMemberMapper.insert(workOrder);
      WorkOrderMessage workOrderMessage = new WorkOrderMessage();
      workOrderMessage.setClientId(workOrder.getClientId());
      workOrderMessage.setWorkOrderId(workOrder.getId());
      workOrderMessage.setMessageContent(workOrder.getContent());
      workOrderMessage.setStatus(0);
      workOrderMessage.setCreateTime(TimeUtil.now());
      this.workOrderMessageMemberMapper.insert(workOrderMessage);
      Map<String, Object> sseMessage = new HashMap<>();
      sseMessage.put("type", "NEW_WORK_ORDER");
      sseMessage.put("workOrderId", workOrder.getId());
      this.devSseApi.sendMessageToAllClient(JSONUtil.toJsonStr(sseMessage));
      Map<String, Object> templateParams = new HashMap<>();
      templateParams.put("workOrderId", workOrder.getId());
      templateParams.put("memberNo", workOrder.getClientId());
      this.toolsNotificationApi.sendAdminNotificationBoth(NotificationTypeEnum.ADMIN_TICKET_SERVICE.getCode(), templateParams);
      return CommonResult.data("添加成功");
   }

   @Override
   public Page<WorkOrderPageResponse> page(WorkOrderPageParam workOrderPageParam) {
      SaBaseClientLoginUser clientLoginUser = this.currentClient();
      QueryWrapper<WorkOrder> queryWrapper = new QueryWrapper().checkSqlInjection();
      queryWrapper.lambda().eq(WorkOrder::getClientId, clientLoginUser.getId());
      if (null != workOrderPageParam.getStatusOut()) {
         queryWrapper.eq("STATUS_OUT", workOrderPageParam.getStatusOut());
      }

      if (StrUtil.isNotBlank(workOrderPageParam.getKeyWord())) {
         String keyword = workOrderPageParam.getKeyWord();
         queryWrapper.lambda()
            .and(wrapper -> ((LambdaQueryWrapper<WorkOrder>)((LambdaQueryWrapper<WorkOrder>)wrapper.like(WorkOrder::getId, keyword)).or()).like(WorkOrder::getTitle, keyword));
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

      for (WorkOrderPageResponse workOrderPageResponse : list) {
         if (null != workOrderPageResponse.getClientId()) {
            WorkOrderType workOrderType = (WorkOrderType)this.workOrderTypeMemberMapper.selectById(workOrderPageResponse.getTypeId());
            if (null == workOrderType) {
               throw new CommonException("存在工单没有类型", new Object[0]);
            }

            workOrderPageResponse.setTypeName(workOrderType.getTypeName());
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
   public CommonResult setStatusOut(String orderId) {
      WorkOrder workOrder = this.ownWorkOrder(orderId);
      if (null == workOrder) {
         return CommonResult.error("该数据不存在");
      } else if (workOrder.getStatusOut() == 0) {
         workOrder.setOrderEndTime(TimeUtil.now());
         workOrder.setStatusOut(1);
         this.workOrderMemberMapper.updateById(workOrder);
         return CommonResult.data(workOrder);
      } else {
         return CommonResult.error("工单已解决，请勿重复操作");
      }
   }

   @Override
   public CommonResult getMessById(String id) {
      WorkOrder workOrder = this.ownWorkOrder(id);
      if (null == workOrder) {
         return CommonResult.error("工单不存在，无法查询");
      } else {
         WorkOrderFindVO workOrderFindVO = (WorkOrderFindVO)BeanUtil.toBean(workOrder, WorkOrderFindVO.class);
         return CommonResult.data(workOrderFindVO);
      }
   }

   private String resolveProductIp(String productName, String productId, Long memberNo) {
      ProductTypeEnum productType = this.matchServerProduct(productName);
      if (StrUtil.isBlank(productId)) {
         if (productType != null) {
            throw new CommonException("请选择关联产品", new Object[0]);
         } else {
            return null;
         }
      } else if (productType == null) {
         throw new CommonException("关联产品仅支持云服务器或独立服务器", new Object[0]);
      } else {
         String ip = ProductTypeEnum.CLOUD_SERVER == productType
            ? this.memberServerApi.selectMainIp(productId, memberNo)
            : this.memberIndependentApi.selectMainIp(productId, memberNo);
         if (StrUtil.isBlank(ip)) {
            throw new CommonException("未找到该产品的主IP", new Object[0]);
         } else {
            return ip;
         }
      }
   }

   private ProductTypeEnum matchServerProduct(String productName) {
      if (StrUtil.isBlank(productName)) {
         return null;
      } else {
         String name = productName.trim();
         if (ProductTypeEnum.CLOUD_SERVER.getMessage().equals(name)
            || ProductTypeEnum.CLOUD_SERVER.getKey().equalsIgnoreCase(name)
            || String.valueOf(ProductTypeEnum.CLOUD_SERVER.getCode()).equals(name)) {
            return ProductTypeEnum.CLOUD_SERVER;
         } else {
            return !ProductTypeEnum.INDEPENDENT_SERVER.getMessage().equals(name)
                  && !ProductTypeEnum.INDEPENDENT_SERVER.getKey().equalsIgnoreCase(name)
                  && !String.valueOf(ProductTypeEnum.INDEPENDENT_SERVER.getCode()).equals(name)
               ? null
               : ProductTypeEnum.INDEPENDENT_SERVER;
         }
      }
   }

   private SaBaseClientLoginUser currentClient() {
      SaBaseClientLoginUser clientLoginUser = StpClientLoginUserUtil.getClientLoginUser();
      if (clientLoginUser != null && clientLoginUser.getId() != null) {
         return clientLoginUser;
      } else {
         throw new CommonException("请先登录", new Object[0]);
      }
   }

   private WorkOrder ownWorkOrder(String id) {
      SaBaseClientLoginUser clientLoginUser = this.currentClient();
      WorkOrder workOrder = (WorkOrder)this.workOrderMemberMapper.selectById(id);
      return workOrder != null && clientLoginUser.getId().equals(workOrder.getClientId()) ? workOrder : null;
   }
}
