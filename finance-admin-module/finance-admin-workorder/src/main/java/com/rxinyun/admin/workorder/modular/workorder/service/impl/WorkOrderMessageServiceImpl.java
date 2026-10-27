package com.rxinyun.admin.workorder.modular.workorder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrder;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrderMessage;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderMessageAddParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.vo.WorkOrderMessageVO;
import com.rxinyun.admin.workorder.modular.workorder.mapper.WorkOrderMapper;
import com.rxinyun.admin.workorder.modular.workorder.mapper.WorkOrderMessageMapper;
import com.rxinyun.admin.workorder.modular.workorder.service.WorkOrderMessageService;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.dev.api.ToolsNotificationApi;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.sys.api.SysUserApi;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkOrderMessageServiceImpl extends ServiceImpl<WorkOrderMessageMapper, WorkOrderMessage> implements WorkOrderMessageService {
   @Resource
   WorkOrderMapper workOrderMapper;
   @Autowired
   private WorkOrderMessageMapper workOrderMessageMapper;
   @Resource
   MemberSaBaseLoginUserApi saBaseLoginUserApi;
   @Resource
   private SysUserApi sysUserApi;
   @Autowired
   private ToolsNotificationApi toolsNotificationApi;

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public CommonResult add(WorkOrderMessageAddParam workOrderMessageAddParam) {
      if (null == workOrderMessageAddParam.getWorkOrderId()) {
         return CommonResult.error("工单ID不能为空");
      } else {
         WorkOrder workOrder = (WorkOrder)this.workOrderMapper.selectById(workOrderMessageAddParam.getWorkOrderId());
         if (null == workOrder) {
            return CommonResult.ok("工单不存在");
         } else if (workOrder.getStatusOut().equals(1)) {
            return CommonResult.error("工单已解决，无法对话");
         } else {
            if (null == workOrder.getOrderAnswerTime()) {
               workOrder.setAdminId(workOrderMessageAddParam.getAdminId());
            }

            workOrder.setOrderAnswerTime(TimeUtil.now());
            WorkOrderMessage workOrderMessage = (WorkOrderMessage)BeanUtil.toBean(workOrderMessageAddParam, WorkOrderMessage.class);
            workOrderMessage.setCreateTime(TimeUtil.now());
            workOrderMessage.setStatus(1);
            this.workOrderMessageMapper.insert(workOrderMessage);
            workOrder.setStatusReply(1);
            this.workOrderMapper.updateById(workOrder);
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("workOrderId", workOrder.getId());
            templateParams.put("memberNo", workOrder.getClientId());
            this.toolsNotificationApi.sendMemberNotificationBoth(NotificationTypeEnum.MEMBER_TICKET_SERVICE.getCode(), workOrder.getClientId(), templateParams);
            return CommonResult.data(workOrderMessage);
         }
      }
   }

   @Override
   public List<WorkOrderMessageVO> getMessage(String workOrderId) {
      QueryWrapper<WorkOrderMessage> queryWrapper = new QueryWrapper();
      ((LambdaQueryWrapper<WorkOrderMessage>)queryWrapper.lambda().eq(null != workOrderId, WorkOrderMessage::getWorkOrderId, workOrderId))
         .orderBy(true, true, WorkOrderMessage::getCreateTime);
      List<WorkOrderMessageVO> list = BeanUtil.copyToList(this.workOrderMessageMapper.selectList(queryWrapper), WorkOrderMessageVO.class);

      for (WorkOrderMessageVO workOrderMessageVO : list) {
         if (null != workOrderMessageVO.getAdminId()) {
            String adminname = this.sysUserApi.getUserByIdWithoutException(workOrderMessageVO.getAdminId()).get("account").toString();
            workOrderMessageVO.setAdminNickName(adminname);
         }

         if (null != workOrderMessageVO.getClientId()) {
            String clientname = this.saBaseLoginUserApi.getClientUserById(workOrderMessageVO.getClientId()).getAccount();
            workOrderMessageVO.setClientNickName(clientname);
         }
      }

      return list;
   }
}
