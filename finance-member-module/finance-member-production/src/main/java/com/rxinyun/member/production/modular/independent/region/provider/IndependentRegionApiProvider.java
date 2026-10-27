package com.rxinyun.member.production.modular.independent.region.provider;

import cn.hutool.core.bean.BeanUtil;
import com.rxinyun.member.production.modular.independent.region.model.entity.Region;
import com.rxinyun.member.production.modular.independent.region.service.IMemberRegionService;
import com.rxinyun.production.api.IndependentRegionApi;
import com.rxinyun.production.core.pojo.BaseIndependentRegion;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IndependentRegionApiProvider implements IndependentRegionApi {
   private static final Logger log = LoggerFactory.getLogger(IndependentRegionApiProvider.class);
   @Resource
   private IMemberRegionService memberRegionService;

   public BaseIndependentRegion selectByRegionId(String LineId) {
      Region region = this.memberRegionService.searchRegionById(LineId);
      return region != null ? (BaseIndependentRegion)BeanUtil.toBean(region, BaseIndependentRegion.class) : null;
   }
}
