package com.rxinyun.agent.modular.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.agent.modular.entity.AgentClientUser;
import com.rxinyun.agent.modular.entity.AgentCommissionRecord;
import com.rxinyun.agent.modular.entity.AgentInfo;
import com.rxinyun.agent.modular.entity.AgentMemberBind;
import com.rxinyun.agent.modular.entity.AgentOrder;
import com.rxinyun.agent.modular.entity.AgentProductRatio;
import com.rxinyun.agent.modular.mapper.AgentCommissionRecordMapper;
import com.rxinyun.agent.modular.mapper.AgentOrderMapper;
import com.rxinyun.agent.modular.param.AgentCommissionPageParam;
import com.rxinyun.agent.modular.response.AgentCommissionPageResponse;
import com.rxinyun.agent.modular.response.AgentCommissionSummaryResponse;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentCommissionService extends ServiceImpl<AgentCommissionRecordMapper, AgentCommissionRecord> {
   private final AgentOrderMapper agentOrderMapper;
   private final AgentMemberBindService agentMemberBindService;
   private final AgentProductRatioService agentProductRatioService;
   private final AgentInfoService agentInfoService;

   public AgentCommissionService(
      AgentOrderMapper agentOrderMapper,
      AgentMemberBindService agentMemberBindService,
      AgentProductRatioService agentProductRatioService,
      AgentInfoService agentInfoService
   ) {
      this.agentOrderMapper = agentOrderMapper;
      this.agentMemberBindService = agentMemberBindService;
      this.agentProductRatioService = agentProductRatioService;
      this.agentInfoService = agentInfoService;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void createCommissionByOrder(String orderNo) {
      List<AgentOrder> orders = this.agentOrderMapper.selectList((Wrapper<AgentOrder>)new LambdaQueryWrapper<AgentOrder>().eq(AgentOrder::getOrderNo, orderNo));
      AgentOrder order = CollUtil.isEmpty(orders) ? null : orders.get(0);
      if (order != null && order.getMemberNo() != null && order.getPayPrice() != null && order.getPayPrice().compareTo(BigDecimal.ZERO) > 0) {
         String idempotentKey = this.buildCommissionIdempotentKey(orderNo);
         if (this.count((Wrapper<AgentCommissionRecord>)new LambdaQueryWrapper<AgentCommissionRecord>().eq(AgentCommissionRecord::getIdempotentKey, idempotentKey)) <= 0L) {
            AgentMemberBind bind = this.agentMemberBindService.getEnabledBindByMemberNo(order.getMemberNo());
            if (bind != null) {
               AgentInfo agentInfo = this.agentInfoService.queryEntity(bind.getAgentId());
               if ("ENABLE".equals(agentInfo.getStatus())) {
                  AgentProductRatio ratio = this.agentProductRatioService.getEnabledRatio(bind.getAgentId(), order.getProductType());
                  if (ratio != null && ratio.getRatio() != null && ratio.getRatio().compareTo(BigDecimal.ZERO) > 0) {
                     BigDecimal commissionAmount = this.calculateCommission(order.getPayPrice(), ratio.getRatio());
                     if (commissionAmount.compareTo(BigDecimal.ZERO) > 0) {
                        AgentCommissionRecord record = new AgentCommissionRecord();
                        record.setId(IdWorker.getIdStr());
                        record.setAgentId(bind.getAgentId());
                        record.setMemberNo(order.getMemberNo());
                        record.setSourceOrderNo(orderNo);
                        record.setProductType(order.getProductType());
                        record.setBaseAmount(order.getPayPrice());
                        record.setRatio(ratio.getRatio());
                        record.setCommissionAmount(commissionAmount);
                        record.setRecordType("COMMISSION");
                        record.setRecordStatus("EFFECTIVE");
                        record.setIdempotentKey(idempotentKey);
                        record.setRemark("订单支付分成");
                        this.save(record);
                        this.agentInfoService.adjustBalance(bind.getAgentId(), commissionAmount);
                     }
                  }
               }
            }
         }
      }
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void deductCommissionByRefund(String refundBatchNo, List<String> orderNos, BigDecimal refundAmount, String remark) {
      if (!StrUtil.isBlank(refundBatchNo) && !CollUtil.isEmpty(orderNos) && refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0) {
         if (this.count(
               (Wrapper<AgentCommissionRecord>)((LambdaQueryWrapper<AgentCommissionRecord>)new LambdaQueryWrapper<AgentCommissionRecord>().eq(AgentCommissionRecord::getSourceRefundNo, refundBatchNo))
                  .eq(AgentCommissionRecord::getRecordType, "REFUND_DEDUCT")
            )
            <= 0L) {
            List<AgentCommissionRecord> sourceRecords = this.list(
               (Wrapper<AgentCommissionRecord>)((LambdaQueryWrapper<AgentCommissionRecord>)((LambdaQueryWrapper<AgentCommissionRecord>)((LambdaQueryWrapper<AgentCommissionRecord>)new LambdaQueryWrapper<AgentCommissionRecord>()
                           .in(AgentCommissionRecord::getSourceOrderNo, orderNos))
                        .eq(AgentCommissionRecord::getRecordType, "COMMISSION"))
                     .eq(AgentCommissionRecord::getRecordStatus, "EFFECTIVE"))
                  .orderByAsc(CommonEntity::getCreateTime)
            );
            if (!CollUtil.isEmpty(sourceRecords)) {
               Map<String, BigDecimal> deductedBaseMap = this.loadDeductedBaseMap(sourceRecords.stream().map(AgentCommissionRecord::getId).toList());
               List<AgentCommissionService.RefundSource> refundSources = sourceRecords.stream().map(record -> {
                  BigDecimal deductedBase = deductedBaseMap.getOrDefault(record.getId(), BigDecimal.ZERO);
                  BigDecimal remainingBase = record.getBaseAmount().subtract(deductedBase);
                  return new AgentCommissionService.RefundSource(record, remainingBase.max(BigDecimal.ZERO));
               }).filter(item -> item.remainingBase.compareTo(BigDecimal.ZERO) > 0).toList();
               if (!CollUtil.isEmpty(refundSources)) {
                  BigDecimal totalRemainingBase = refundSources.stream().map(item -> item.remainingBase).reduce(BigDecimal.ZERO, BigDecimal::add);
                  BigDecimal refundableBase = refundAmount.min(totalRemainingBase);
                  BigDecimal allocated = BigDecimal.ZERO;

                  for (int i = 0; i < refundSources.size(); i++) {
                     AgentCommissionService.RefundSource source = refundSources.get(i);
                     BigDecimal refundBaseShare;
                     if (i == refundSources.size() - 1) {
                        refundBaseShare = refundableBase.subtract(allocated);
                     } else {
                        refundBaseShare = refundableBase.multiply(source.remainingBase).divide(totalRemainingBase, 2, RoundingMode.HALF_UP);
                        if (refundBaseShare.compareTo(source.remainingBase) > 0) {
                           refundBaseShare = source.remainingBase;
                        }
                     }

                     if (refundBaseShare.compareTo(BigDecimal.ZERO) > 0) {
                        allocated = allocated.add(refundBaseShare);
                        AgentCommissionRecord deductRecord = new AgentCommissionRecord();
                        deductRecord.setId(IdWorker.getIdStr());
                        deductRecord.setAgentId(source.record.getAgentId());
                        deductRecord.setMemberNo(source.record.getMemberNo());
                        deductRecord.setSourceOrderNo(source.record.getSourceOrderNo());
                        deductRecord.setProductType(source.record.getProductType());
                        deductRecord.setBaseAmount(refundBaseShare);
                        deductRecord.setRatio(source.record.getRatio());
                        deductRecord.setCommissionAmount(this.calculateCommission(refundBaseShare, source.record.getRatio()).negate());
                        deductRecord.setRecordType("REFUND_DEDUCT");
                        deductRecord.setRecordStatus("EFFECTIVE");
                        deductRecord.setRelatedRecordId(source.record.getId());
                        deductRecord.setSourceRefundNo(refundBatchNo);
                        deductRecord.setIdempotentKey(this.buildRefundIdempotentKey(refundBatchNo, source.record.getId()));
                        deductRecord.setRemark(StrUtil.blankToDefault(remark, "退款冲减分成"));
                        this.save(deductRecord);
                        this.agentInfoService.adjustBalance(source.record.getAgentId(), deductRecord.getCommissionAmount());
                     }
                  }
               }
            }
         }
      }
   }

   public Page<AgentCommissionPageResponse> page(AgentCommissionPageParam pageParam) {
      QueryWrapper<AgentCommissionRecord> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(pageParam.getAgentId())) {
         queryWrapper.lambda().eq(AgentCommissionRecord::getAgentId, pageParam.getAgentId());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getMemberNo())) {
         queryWrapper.lambda().eq(AgentCommissionRecord::getMemberNo, pageParam.getMemberNo());
      }

      if (StrUtil.isNotBlank(pageParam.getSourceOrderNo())) {
         queryWrapper.lambda().eq(AgentCommissionRecord::getSourceOrderNo, pageParam.getSourceOrderNo());
      }

      if (StrUtil.isNotBlank(pageParam.getRecordType())) {
         queryWrapper.lambda().eq(AgentCommissionRecord::getRecordType, pageParam.getRecordType());
      }

      if (ObjectUtil.isNotEmpty(pageParam.getProductType())) {
         queryWrapper.lambda().eq(AgentCommissionRecord::getProductType, pageParam.getProductType());
      }

      queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      Page<AgentCommissionRecord> recordPage = (Page<AgentCommissionRecord>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
      Page<AgentCommissionPageResponse> resultPage = new Page(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
      if (CollUtil.isEmpty(recordPage.getRecords())) {
         resultPage.setRecords(Collections.emptyList());
         return resultPage;
      } else {
         Map<Long, AgentClientUser> userMap = this.agentMemberBindService
            .loadUserMap(recordPage.getRecords().stream().map(AgentCommissionRecord::getMemberNo).distinct().toList());
         List<AgentCommissionPageResponse> records = recordPage.getRecords().stream().map(item -> {
            AgentCommissionPageResponse response = (AgentCommissionPageResponse)BeanUtil.copyProperties(item, AgentCommissionPageResponse.class, new String[0]);
            AgentClientUser clientUser = userMap.get(item.getMemberNo());
            if (clientUser != null) {
               response.setMemberAccount(clientUser.getAccount());
            }

            return response;
         }).toList();
         resultPage.setRecords(records);
         return resultPage;
      }
   }

   public AgentCommissionSummaryResponse summaryByAgentId(String agentId) {
      AgentCommissionSummaryResponse response = new AgentCommissionSummaryResponse();
      List<AgentCommissionRecord> records = this.list(
         (Wrapper<AgentCommissionRecord>)((LambdaQueryWrapper<AgentCommissionRecord>)new LambdaQueryWrapper<AgentCommissionRecord>().eq(AgentCommissionRecord::getAgentId, agentId))
            .eq(AgentCommissionRecord::getRecordStatus, "EFFECTIVE")
      );
      BigDecimal totalCommission = records.stream()
         .filter(item -> "COMMISSION".equals(item.getRecordType()))
         .map(AgentCommissionRecord::getCommissionAmount)
         .reduce(BigDecimal.ZERO, BigDecimal::add);
      BigDecimal totalRefundDeduct = records.stream()
         .filter(item -> "REFUND_DEDUCT".equals(item.getRecordType()))
         .map(AgentCommissionRecord::getCommissionAmount)
         .reduce(BigDecimal.ZERO, BigDecimal::add)
         .abs();
      response.setTotalCommission(totalCommission);
      response.setTotalRefundDeduct(totalRefundDeduct);
      response.setNetCommission(totalCommission.subtract(totalRefundDeduct));
      return response;
   }

   private BigDecimal calculateCommission(BigDecimal baseAmount, BigDecimal ratio) {
      return baseAmount.multiply(ratio).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
   }

   private String buildCommissionIdempotentKey(String orderNo) {
      return "COMMISSION:" + orderNo;
   }

   private String buildRefundIdempotentKey(String refundBatchNo, String sourceRecordId) {
      return "REFUND_DEDUCT:" + refundBatchNo + ":" + sourceRecordId;
   }

   private Map<String, BigDecimal> loadDeductedBaseMap(List<String> sourceRecordIds) {
      return CollUtil.isEmpty(sourceRecordIds)
         ? Collections.emptyMap()
         : this.list(
               (Wrapper<AgentCommissionRecord>)((LambdaQueryWrapper<AgentCommissionRecord>)new LambdaQueryWrapper<AgentCommissionRecord>().in(AgentCommissionRecord::getRelatedRecordId, sourceRecordIds))
                  .eq(AgentCommissionRecord::getRecordType, "REFUND_DEDUCT")
            )
            .stream()
            .collect(
               Collectors.groupingBy(
                  AgentCommissionRecord::getRelatedRecordId,
                  Collectors.mapping(AgentCommissionRecord::getBaseAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
               )
            );
   }

   private record RefundSource(AgentCommissionRecord record, BigDecimal remainingBase) {
   }
}
