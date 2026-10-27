package com.rxinyun.member.workorder.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.NotificationTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.dev.api.ToolsNotificationApi;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.member.api.MemberSaBaseLoginUserApi;
import com.rxinyun.member.member.pojo.SaBaseClientLoginUser;
import com.rxinyun.member.workorder.mapper.WorkOrderMemberMapper;
import com.rxinyun.member.workorder.mapper.WorkOrderMessageMemberMapper;
import com.rxinyun.member.workorder.pojo.dto.WorkOrderMessageAddParam;
import com.rxinyun.member.workorder.pojo.entity.WorkOrder;
import com.rxinyun.member.workorder.pojo.entity.WorkOrderMessage;
import com.rxinyun.member.workorder.pojo.vo.WorkOrderMessageVO;
import com.rxinyun.member.workorder.service.WorkOrderMessageMemberService;
import com.rxinyun.sys.api.SysUserApi;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkOrderMessageMemberServiceImpl extends ServiceImpl<WorkOrderMessageMemberMapper, WorkOrderMessage> implements WorkOrderMessageMemberService {
   @Resource
   WorkOrderMessageMemberMapper workOrderMessageMemberMapper;
   @Resource
   WorkOrderMemberMapper workOrderMemberMapper;
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
      SaBaseClientLoginUser clientLoginUser = this.currentClient();
      WorkOrder workOrder = (WorkOrder)this.workOrderMemberMapper.selectById(workOrderMessageAddParam.getWorkOrderId());
      if (workOrder != null && clientLoginUser.getId().equals(workOrder.getClientId())) {
         workOrder.setStatusReply(0);
         workOrder.setOrderAnswerTime(TimeUtil.now());
         if (workOrder.getStatusOut() == 1) {
            return CommonResult.error("工单已解决，无法对话");
         } else {
            this.workOrderMemberMapper.updateById(workOrder);
            WorkOrderMessage workOrderMessage = (WorkOrderMessage)BeanUtil.toBean(workOrderMessageAddParam, WorkOrderMessage.class);
            workOrderMessage.setClientId(clientLoginUser.getId());
            workOrderMessage.setStatus(0);
            workOrderMessage.setCreateTime(TimeUtil.now());
            this.workOrderMessageMemberMapper.insert(workOrderMessage);
            Map<String, Object> templateParams = new HashMap<>();
            templateParams.put("workOrderId", workOrder.getId());
            templateParams.put("memberNo", workOrder.getClientId());
            this.toolsNotificationApi.sendAdminNotificationBoth(NotificationTypeEnum.ADMIN_TICKET_SERVICE.getCode(), templateParams);
            return CommonResult.data(workOrderMessage);
         }
      } else {
         return CommonResult.error("工单不存在");
      }
   }

   @Override
   public List<WorkOrderMessageVO> findMessage(String workOrderId) {
      SaBaseClientLoginUser clientLoginUser = this.currentClient();
      WorkOrder workOrder = (WorkOrder)this.workOrderMemberMapper.selectById(workOrderId);
      if (workOrder != null && clientLoginUser.getId().equals(workOrder.getClientId())) {
         QueryWrapper<WorkOrderMessage> queryWrapper = new QueryWrapper();
         ((LambdaQueryWrapper<WorkOrderMessage>)queryWrapper.lambda().eq(null != workOrderId, WorkOrderMessage::getWorkOrderId, workOrderId))
            .orderBy(true, true, WorkOrderMessage::getCreateTime);
         List<WorkOrderMessageVO> list = BeanUtil.copyToList(this.workOrderMessageMemberMapper.selectList(queryWrapper), WorkOrderMessageVO.class);

         for (WorkOrderMessageVO workOrderMessageVO : list) {
            if (null != workOrderMessageVO.getAdminId()) {
               String adminname = this.sysUserApi.getUserByIdWithoutException(workOrderMessageVO.getAdminId()).get("account").toString();
               workOrderMessageVO.setAdminNickName(adminname);
            }

            if (null != workOrderMessageVO.getClientId()) {
               String var9 = this.saBaseLoginUserApi.getClientUserById(workOrderMessageVO.getClientId()).getAccount();
            }
         }

         return list;
      } else {
         throw new CommonException("工单不存在", new Object[0]);
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
}
