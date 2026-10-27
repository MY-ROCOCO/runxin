package com.rxinyun.agent.modular.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.agent.core.util.StpAgentLoginUserUtil;
import com.rxinyun.agent.modular.auth.result.AgentLoginUser;
import com.rxinyun.agent.modular.entity.AgentClientUser;
import com.rxinyun.agent.modular.entity.AgentInfo;
import com.rxinyun.agent.modular.entity.AgentMemberBind;
import com.rxinyun.agent.modular.entity.AgentOrder;
import com.rxinyun.agent.modular.mapper.AgentOrderMapper;
import com.rxinyun.agent.modular.param.AgentMemberBalanceAddParam;
import com.rxinyun.agent.modular.param.AgentMemberPageParam;
import com.rxinyun.agent.modular.param.AgentOrderPageParam;
import com.rxinyun.agent.modular.response.AgentMemberPageResponse;
import com.rxinyun.agent.modular.response.AgentOrderPageResponse;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.member.admin.api.BalanceApi;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentPortalService {
   private final AgentMemberBindService agentMemberBindService;
   private final AgentOrderMapper agentOrderMapper;
   private final AgentInfoService agentInfoService;
   private final BalanceApi balanceApi;

   public AgentPortalService(
      AgentMemberBindService agentMemberBindService, AgentOrderMapper agentOrderMapper, AgentInfoService agentInfoService, BalanceApi balanceApi
   ) {
      this.agentMemberBindService = agentMemberBindService;
      this.agentOrderMapper = agentOrderMapper;
      this.agentInfoService = agentInfoService;
      this.balanceApi = balanceApi;
   }

   public Page<AgentMemberPageResponse> myMembers(AgentMemberPageParam pageParam) {
      AgentLoginUser loginUser = StpAgentLoginUserUtil.getLoginUser();
      pageParam.setAgentId(loginUser.getAgentId());
      return this.agentMemberBindService.page(pageParam);
   }

   public Page<AgentOrderPageResponse> myOrders(AgentOrderPageParam pageParam) {
      AgentLoginUser loginUser = StpAgentLoginUserUtil.getLoginUser();
      List<Long> memberNos = this.agentMemberBindService.listEnabledMemberNosByAgentId(loginUser.getAgentId());
      Page<AgentOrderPageResponse> resultPage = new Page();
      if (CollUtil.isEmpty(memberNos)) {
         return resultPage;
      } else {
         QueryWrapper<AgentOrder> queryWrapper = new QueryWrapper().checkSqlInjection();
         queryWrapper.lambda().in(AgentOrder::getMemberNo, memberNos);
         if (StrUtil.isNotBlank(pageParam.getOrderNo())) {
            queryWrapper.lambda().eq(AgentOrder::getOrderNo, pageParam.getOrderNo());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
            queryWrapper.lambda().eq(AgentOrder::getMemberNo, pageParam.getMemberNo());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getOrderStatus())) {
            queryWrapper.lambda().eq(AgentOrder::getOrderStatus, pageParam.getOrderStatus());
         }

         if (ObjectUtil.isNotEmpty(pageParam.getProductType())) {
            queryWrapper.lambda().eq(AgentOrder::getProductType, pageParam.getProductType());
         }

         queryWrapper.lambda().orderByDesc(AgentOrder::getCreateTime);
         Page<AgentOrder> orderPage = (Page<AgentOrder>)this.agentOrderMapper.selectPage(CommonPageRequest.defaultPage(), queryWrapper);
         resultPage = new Page(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
         if (CollUtil.isEmpty(orderPage.getRecords())) {
            resultPage.setRecords(Collections.emptyList());
            return resultPage;
         } else {
            Map<Long, AgentClientUser> userMap = this.agentMemberBindService
               .loadUserMap(orderPage.getRecords().stream().map(AgentOrder::getMemberNo).distinct().toList());
            List<AgentOrderPageResponse> records = orderPage.getRecords().stream().map(order -> {
               AgentOrderPageResponse response = new AgentOrderPageResponse();
               response.setOrderNo(order.getOrderNo());
               response.setMemberNo(order.getMemberNo());
               response.setMemberName(order.getMemberName());
               response.setProductType(order.getProductType());
               response.setOrderStatus(order.getOrderStatus());
               response.setPayPrice(order.getPayPrice());
               response.setPayTime(order.getPayTime());
               response.setCreateTime(order.getCreateTime());
               AgentClientUser clientUser = userMap.get(order.getMemberNo());
               if (clientUser != null) {
                  response.setMemberAccount(clientUser.getAccount());
               }

               return response;
            }).toList();
            resultPage.setRecords(records);
            return resultPage;
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void addMemberBalance(String agentId, AgentMemberBalanceAddParam addParam) {
      AgentInfo agentInfo = this.agentInfoService.queryEntity(agentId);
      if (!"ENABLE".equals(agentInfo.getStatus())) {
         throw new CommonException("代理已停用，暂不可给会员增加余额", new Object[0]);
      } else {
         AgentMemberBind bind = this.agentMemberBindService.getEnabledBindByMemberNo(addParam.getMemberNo());
         if (bind != null && StrUtil.equals(bind.getAgentId(), agentId)) {
            BigDecimal amount = addParam.getAmount().setScale(2, RoundingMode.HALF_UP);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
               throw new CommonException("充值金额必须大于0", new Object[0]);
            } else {
               Date now = new Date();
               String orderNo = this.buildMemberBalanceOrderNo();
               String remark = StrUtil.blankToDefault(addParam.getRemark(), "代理给会员增加余额");
               this.agentInfoService.deductBalance(agentId, amount);
               this.balanceApi
                  .creditAndRecord(
                     addParam.getMemberNo(),
                     amount,
                     orderNo,
                     "代理余额转入",
                     JSONUtil.createObj()
                        .set("agentId", agentId)
                        .set("agentCode", agentInfo.getAgentCode())
                        .set("agentName", agentInfo.getAgentName())
                        .set("remark", addParam.getRemark())
                        .toString(),
                     now,
                     remark
                  );
            }
         } else {
            throw new CommonException("该会员不属于当前代理，无法增加余额", new Object[0]);
         }
      }
   }

   private String buildMemberBalanceOrderNo() {
      return "AB" + IdWorker.getIdStr();
   }
}
