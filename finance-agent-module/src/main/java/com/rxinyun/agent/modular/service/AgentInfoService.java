package com.rxinyun.agent.modular.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.agent.modular.entity.AgentInfo;
import com.rxinyun.agent.modular.mapper.AgentInfoMapper;
import com.rxinyun.agent.modular.param.AgentAddParam;
import com.rxinyun.agent.modular.param.AgentEditParam;
import com.rxinyun.agent.modular.param.AgentIdParam;
import com.rxinyun.agent.modular.param.AgentPageParam;
import com.rxinyun.common.enums.CommonSortOrderEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.common.page.CommonPageRequest;
import com.rxinyun.common.pojo.CommonEntity;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class AgentInfoService extends ServiceImpl<AgentInfoMapper, AgentInfo> {
   public Page<AgentInfo> page(AgentPageParam pageParam) {
      QueryWrapper<AgentInfo> queryWrapper = new QueryWrapper().checkSqlInjection();
      if (StrUtil.isNotBlank(pageParam.getAgentCode())) {
         queryWrapper.lambda().like(AgentInfo::getAgentCode, pageParam.getAgentCode().trim());
      }

      if (StrUtil.isNotBlank(pageParam.getAgentName())) {
         queryWrapper.lambda().like(AgentInfo::getAgentName, pageParam.getAgentName().trim());
      }

      if (StrUtil.isNotBlank(pageParam.getStatus())) {
         queryWrapper.lambda().eq(AgentInfo::getStatus, pageParam.getStatus().trim());
      }

      if (ObjectUtil.isAllNotEmpty(new Object[]{pageParam.getSortField(), pageParam.getSortOrder()})) {
         CommonSortOrderEnum.validate(pageParam.getSortOrder());
         queryWrapper.orderBy(true, pageParam.getSortOrder().equals(CommonSortOrderEnum.ASC.getValue()), StrUtil.toUnderlineCase(pageParam.getSortField()));
      } else {
         queryWrapper.lambda().orderByDesc(CommonEntity::getCreateTime);
      }

      return (Page<AgentInfo>)this.page(CommonPageRequest.defaultPage(), queryWrapper);
   }

   public void add(AgentAddParam addParam) {
      AgentInfo agentInfo = new AgentInfo();
      agentInfo.setId(IdWorker.getIdStr());
      agentInfo.setAgentCode(this.buildAgentCode(addParam.getAgentCode()));
      this.ensureUniqueAgentCode(agentInfo.getAgentCode(), null);
      agentInfo.setAgentName(addParam.getAgentName().trim());
      agentInfo.setContactName(this.toNullIfBlank(addParam.getContactName()));
      agentInfo.setContactPhone(this.toNullIfBlank(addParam.getContactPhone()));
      agentInfo.setRemark(this.toNullIfBlank(addParam.getRemark()));
      agentInfo.setStatus("ENABLE");
      agentInfo.setBalance(BigDecimal.ZERO);
      this.save(agentInfo);
   }

   public void edit(AgentEditParam editParam) {
      AgentInfo agentInfo = this.queryEntity(editParam.getId());
      agentInfo.setAgentName(editParam.getAgentName().trim());
      agentInfo.setContactName(this.toNullIfBlank(editParam.getContactName()));
      agentInfo.setContactPhone(this.toNullIfBlank(editParam.getContactPhone()));
      agentInfo.setRemark(this.toNullIfBlank(editParam.getRemark()));
      if (StrUtil.isNotBlank(editParam.getStatus())) {
         agentInfo.setStatus(editParam.getStatus().trim());
      }

      this.updateById(agentInfo);
   }

   public void disable(AgentIdParam agentIdParam) {
      AgentInfo agentInfo = this.queryEntity(agentIdParam.getId());
      agentInfo.setStatus("DISABLED");
      this.updateById(agentInfo);
   }

   public AgentInfo queryEntity(String id) {
      AgentInfo agentInfo = (AgentInfo)this.getById(id);
      if (agentInfo == null) {
         throw new CommonException("代理不存在，id值为：{}", new Object[]{id});
      } else {
         return agentInfo;
      }
   }

   public AgentInfo queryEnabledByCode(String agentCode) {
      AgentInfo agentInfo = (AgentInfo)this.getOne(
         (Wrapper<AgentInfo>)((LambdaQueryWrapper<AgentInfo>)new LambdaQueryWrapper<AgentInfo>().eq(AgentInfo::getAgentCode, agentCode)).eq(AgentInfo::getStatus, "ENABLE"), false
      );
      if (agentInfo == null) {
         throw new CommonException("代理链接无效或代理已停用", new Object[0]);
      } else {
         return agentInfo;
      }
   }

   public BigDecimal queryBalance(String agentId) {
      AgentInfo agentInfo = this.queryEntity(agentId);
      return (BigDecimal)ObjectUtil.defaultIfNull(agentInfo.getBalance(), BigDecimal.ZERO);
   }

   public void adjustBalance(String agentId, BigDecimal amountDelta) {
      if (amountDelta != null && BigDecimal.ZERO.compareTo(amountDelta) != 0) {
         this.queryEntity(agentId);
         boolean success = this.update(
            (Wrapper<AgentInfo>)((LambdaUpdateWrapper<AgentInfo>)new LambdaUpdateWrapper<AgentInfo>().eq(AgentInfo::getId, agentId))
               .setSql("balance = IFNULL(balance, 0) + " + amountDelta.toPlainString(), new Object[0])
         );
         if (!success) {
            throw new CommonException("代理余额更新失败", new Object[0]);
         }
      }
   }

   public void deductBalance(String agentId, BigDecimal amount) {
      if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
         this.queryEntity(agentId);
         boolean success = this.update(
            (Wrapper<AgentInfo>)((LambdaUpdateWrapper<AgentInfo>)((LambdaUpdateWrapper<AgentInfo>)new LambdaUpdateWrapper<AgentInfo>().eq(AgentInfo::getId, agentId)).ge(AgentInfo::getBalance, amount))
               .setSql("balance = IFNULL(balance, 0) - " + amount.toPlainString(), new Object[0])
         );
         if (!success) {
            throw new CommonException("代理余额不足", new Object[0]);
         }
      }
   }

   private String buildAgentCode(String inputAgentCode) {
      return StrUtil.isNotBlank(inputAgentCode) ? inputAgentCode.trim() : "AGT" + IdWorker.getIdStr();
   }

   private void ensureUniqueAgentCode(String agentCode, String excludeId) {
      LambdaQueryWrapper<AgentInfo> wrapper = (LambdaQueryWrapper<AgentInfo>)new LambdaQueryWrapper<AgentInfo>().eq(AgentInfo::getAgentCode, agentCode);
      if (StrUtil.isNotBlank(excludeId)) {
         wrapper.ne(AgentInfo::getId, excludeId);
      }

      if (this.count(wrapper) > 0L) {
         throw new CommonException("存在重复的代理编码，编码为：{}", new Object[]{agentCode});
      }
   }

   private String toNullIfBlank(String value) {
      return StrUtil.isBlank(value) ? null : value.trim();
   }
}
