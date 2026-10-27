package com.rxinyun.member.production.modular.independent.cloud.provider;

import cn.hutool.core.bean.BeanUtil;
import com.rxinyun.member.production.modular.independent.cloud.entity.MemberLine;
import com.rxinyun.member.production.modular.independent.cloud.service.MemberLineService;
import com.rxinyun.production.api.IndependentLineApi;
import com.rxinyun.production.core.pojo.BaseIndependentLine;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class IndependentLineApiProvider implements IndependentLineApi {
   private static final Logger log = LoggerFactory.getLogger(IndependentLineApiProvider.class);
   @Resource
   private MemberLineService memberLineService;

   public BaseIndependentLine selectByLineId(String LineId) {
      MemberLine memberLine = this.memberLineService.queryEntity(LineId);
      return memberLine == null ? null : (BaseIndependentLine)BeanUtil.toBean(memberLine, BaseIndependentLine.class);
   }
}
