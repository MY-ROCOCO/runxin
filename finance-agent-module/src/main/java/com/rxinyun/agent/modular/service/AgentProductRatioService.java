package com.rxinyun.agent.modular.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.agent.modular.entity.AgentProductRatio;
import com.rxinyun.agent.modular.mapper.AgentProductRatioMapper;
import com.rxinyun.agent.modular.param.AgentRatioSaveParam;
import com.rxinyun.common.exception.CommonException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentProductRatioService extends ServiceImpl<AgentProductRatioMapper, AgentProductRatio> {
   private final AgentInfoService agentInfoService;

   public AgentProductRatioService(AgentInfoService agentInfoService) {
      this.agentInfoService = agentInfoService;
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   public void saveRatio(AgentRatioSaveParam saveParam) {
      this.agentInfoService.queryEntity(saveParam.getAgentId());
      if (saveParam.getRatio().compareTo(BigDecimal.ZERO) >= 0 && saveParam.getRatio().compareTo(new BigDecimal("100")) <= 0) {
         AgentProductRatio ratio = (AgentProductRatio)this.getOne(
            (Wrapper<AgentProductRatio>)((LambdaQueryWrapper<AgentProductRatio>)new LambdaQueryWrapper<AgentProductRatio>().eq(AgentProductRatio::getAgentId, saveParam.getAgentId()))
               .eq(AgentProductRatio::getProductType, saveParam.getProductType()),
            false
         );
         if (ratio == null) {
            ratio = new AgentProductRatio();
            ratio.setId(IdWorker.getIdStr());
            ratio.setAgentId(saveParam.getAgentId());
            ratio.setProductType(saveParam.getProductType());
         }

         ratio.setRatio(saveParam.getRatio());
         ratio.setStatus((String)ObjectUtil.defaultIfNull(saveParam.getStatus(), "ENABLE"));
         ratio.setEffectiveTime(saveParam.getEffectiveTime());
         ratio.setExpireTime(saveParam.getExpireTime());
         this.saveOrUpdate(ratio);
      } else {
         throw new CommonException("分成比例必须在0到100之间", new Object[0]);
      }
   }

   public List<AgentProductRatio> listByAgentId(String agentId) {
      return this.list(
         (Wrapper<AgentProductRatio>)((LambdaQueryWrapper<AgentProductRatio>)new LambdaQueryWrapper<AgentProductRatio>().eq(AgentProductRatio::getAgentId, agentId)).orderByAsc(AgentProductRatio::getProductType)
      );
   }

   public AgentProductRatio getEnabledRatio(String agentId, Integer productType) {
      return (AgentProductRatio)this.getOne(
         (Wrapper<AgentProductRatio>)((LambdaQueryWrapper<AgentProductRatio>)((LambdaQueryWrapper<AgentProductRatio>)new LambdaQueryWrapper<AgentProductRatio>().eq(AgentProductRatio::getAgentId, agentId))
               .eq(AgentProductRatio::getProductType, productType))
            .eq(AgentProductRatio::getStatus, "ENABLE"),
         false
      );
   }
}
