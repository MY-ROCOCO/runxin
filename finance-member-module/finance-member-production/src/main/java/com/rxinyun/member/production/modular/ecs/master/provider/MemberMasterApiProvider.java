package com.rxinyun.member.production.modular.ecs.master.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.rxinyun.member.production.modular.ecs.master.entity.MemberMaster;
import com.rxinyun.member.production.modular.ecs.master.param.MemberMasterIdParam;
import com.rxinyun.member.production.modular.ecs.master.service.MemberMasterService;
import com.rxinyun.production.api.MemberMasterApi;
import com.rxinyun.production.core.pojo.BaseMemberMaster;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberMasterApiProvider implements MemberMasterApi {
   private static final Logger log = LoggerFactory.getLogger(MemberMasterApiProvider.class);
   @Resource
   private MemberMasterService memberMasterService;

   public BaseMemberMaster selectByLineId(String LineId) {
      MemberMaster memberMaster = this.memberMasterService.queryByLineId(LineId);
      log.info("selectByLineId:{}", JSONObject.toJSONString(memberMaster));
      return memberMaster != null ? (BaseMemberMaster)BeanUtil.toBean(memberMaster, BaseMemberMaster.class) : null;
   }

   public BaseMemberMaster selectByMasterId(String masterId) {
      if (StrUtil.isBlank(masterId)) {
         return null;
      } else {
         MemberMasterIdParam memberMasterIdParam = new MemberMasterIdParam();
         memberMasterIdParam.setId(masterId);
         MemberMaster memberMaster = this.memberMasterService.detail(memberMasterIdParam);
         return memberMaster != null ? (BaseMemberMaster)BeanUtil.toBean(memberMaster, BaseMemberMaster.class) : null;
      }
   }

   public void updateHostStats(String masterId, Integer instanceCount, Integer ramRemaining, Long diskRemaining) {
      this.memberMasterService.updateHostStats(masterId, instanceCount, ramRemaining, diskRemaining);
   }
}
