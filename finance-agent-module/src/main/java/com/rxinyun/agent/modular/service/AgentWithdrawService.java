package com.rxinyun.agent.modular.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.agent.modular.entity.AgentInfo;
import com.rxinyun.agent.modular.entity.AgentWithdrawRecord;
import com.rxinyun.agent.modular.mapper.AgentWithdrawRecordMapper;
import com.rxinyun.agent.modular.param.AgentWithdrawApplyParam;
import com.rxinyun.agent.modular.param.AgentWithdrawPageParam;
import com.rxinyun.agent.modular.param.AgentWithdrawReviewParam;
import com.rxinyun.agent.modular.response.AgentWithdrawPageResponse;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentWithdrawService extends ServiceImpl<AgentWithdrawRecordMapper, AgentWithdrawRecord> {
   private final AgentInfoService agentInfoService;

   public AgentWithdrawService(AgentInfoService agentInfoService) {
      this.agentInfoService = agentInfoService;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void apply(String agentId, String operatorId, AgentWithdrawApplyParam applyParam) {
      AgentInfo agentInfo = this.agentInfoService.queryEntity(agentId);
      if (!"ENABLE".equals(agentInfo.getStatus())) {
         throw new CommonException("代理已停用，暂不可申请提现", new Object[0]);
      } else {
         BigDecimal amount = applyParam.getAmount().setScale(2, RoundingMode.HALF_UP);
         if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CommonException("提现金额必须大于0", new Object[0]);
         } else {
            this.agentInfoService.deductBalance(agentId, amount);
            AgentWithdrawRecord record = new AgentWithdrawRecord();
            record.setId(IdWorker.getIdStr());
            record.setAgentId(agentId);
            record.setWithdrawNo(this.buildWithdrawNo());
            record.setAmount(amount);
            record.setStatus("PENDING");
            record.setAccountName(this.toNullIfBlank(applyParam.getAccountName()));
            record.setAccountNo(this.toNullIfBlank(applyParam.getAccountNo()));
            record.setBankName(this.toNullIfBlank(applyParam.getBankName()));
            record.setRemark(this.toNullIfBlank(applyParam.getRemark()));
            record.setCreateUser(operatorId);
            this.save(record);
         }
      }
   }

   public Page<AgentWithdrawPageResponse> page(AgentWithdrawPageParam pageParam) {
      QueryWrapper<AgentWithdrawRecord> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(pageParam.getAgentId())) {
         queryWrapper.lambda().eq(AgentWithdrawRecord::getAgentId, pageParam.getAgentId());
      }

      if (StrUtil.isNotBlank(pageParam.getWithdrawNo())) {
         queryWrapper.lambda().eq(AgentWithdrawRecord::getWithdrawNo, pageParam.getWithdrawNo().trim());
      }

      if (StrUtil.isNotBlank(pageParam.getStatus())) {
         queryWrapper.lambda().eq(AgentWithdrawRecord::getStatus, pageParam.getStatus().trim());
      }

      queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      Page<AgentWithdrawRecord> recordPage = (Page<AgentWithdrawRecord>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<AgentWithdrawPageResponse> resultPage = new Page(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
      if (CollUtil.isEmpty(recordPage.getRecords())) {
         resultPage.setRecords(Collections.emptyList());
         return resultPage;
      } else {
         Map<String, AgentInfo> agentMap = this.agentInfoService
            .listByIds(recordPage.getRecords().stream().map(AgentWithdrawRecord::getAgentId).distinct().toList())
            .stream()
            .collect(Collectors.toMap(AgentInfo::getId, item -> (AgentInfo)item));
         List<AgentWithdrawPageResponse> records = recordPage.getRecords().stream().map(item -> {
            AgentWithdrawPageResponse response = (AgentWithdrawPageResponse)BeanUtil.copyProperties(item, AgentWithdrawPageResponse.class, new String[0]);
            AgentInfo agentInfo = agentMap.get(item.getAgentId());
            if (agentInfo != null) {
               response.setAgentCode(agentInfo.getAgentCode());
               response.setAgentName(agentInfo.getAgentName());
            }

            return response;
         }).toList();
         resultPage.setRecords(records);
         return resultPage;
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void review(AgentWithdrawReviewParam reviewParam) {
      AgentWithdrawRecord record = this.queryEntity(reviewParam.getId());
      if (!"PENDING".equals(record.getStatus())) {
         throw new CommonException("该提现申请已审核，请勿重复操作", new Object[0]);
      } else {
         String reviewStatus = reviewParam.getStatus().trim();
         if (!"APPROVED".equals(reviewStatus) && !"REJECTED".equals(reviewStatus)) {
            throw new CommonException("审核状态非法", new Object[0]);
         } else {
            record.setStatus(reviewStatus);
            record.setReviewRemark(this.toNullIfBlank(reviewParam.getReviewRemark()));
            record.setReviewTime(new Date());
            record.setReviewUser(this.getCurrentAdminUserId());
            if ("REJECTED".equals(reviewStatus)) {
               this.agentInfoService.adjustBalance(record.getAgentId(), record.getAmount());
            }

            this.updateById(record);
         }
      }
   }

   public AgentWithdrawRecord queryEntity(String id) {
      AgentWithdrawRecord record = (AgentWithdrawRecord)this.getById(id);
      if (record == null) {
         throw new CommonException("提现记录不存在，id值为：{}", new Object[]{id});
      } else {
         return record;
      }
   }

   private String buildWithdrawNo() {
      return "AW" + IdWorker.getIdStr();
   }

   private String getCurrentAdminUserId() {
      try {
         return StpUtil.getLoginIdAsString();
      } catch (Exception var2) {
         return "-1";
      }
   }

   private String toNullIfBlank(String value) {
      return StrUtil.isBlank(value) ? null : value.trim();
   }
}
