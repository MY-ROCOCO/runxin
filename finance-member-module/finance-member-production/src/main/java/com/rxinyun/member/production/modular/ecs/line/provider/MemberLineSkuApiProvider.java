package com.rxinyun.member.production.modular.ecs.line.provider;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLineSku;
import com.rxinyun.member.production.modular.ecs.line.service.MemberLineSkuService;
import com.rxinyun.production.api.MemberLineSkuApi;
import com.rxinyun.production.core.pojo.BaseMemberLineSku;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberLineSkuApiProvider implements MemberLineSkuApi {
   private static final Logger log = LoggerFactory.getLogger(MemberLineSkuApiProvider.class);
   @Resource
   private MemberLineSkuService memberLineSkuService;

   public BaseMemberLineSku selectByLineSku(String lineId, int cpu, int ram) {
      MemberLineSku memberLineSku = this.memberLineSkuService.selectByLineSku(lineId, cpu, ram);
      log.info("memberLineSku:{}", JSONObject.toJSONString(memberLineSku));
      return memberLineSku != null ? (BaseMemberLineSku)BeanUtil.toBean(memberLineSku, BaseMemberLineSku.class) : null;
   }
}
