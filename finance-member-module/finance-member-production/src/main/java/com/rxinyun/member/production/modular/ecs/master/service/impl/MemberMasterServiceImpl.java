package com.rxinyun.member.production.modular.ecs.master.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.enums.EnableDisableEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLocalLine;
import com.rxinyun.member.production.modular.ecs.line.service.MemberLocalLineService;
import com.rxinyun.member.production.modular.ecs.master.entity.MemberMaster;
import com.rxinyun.member.production.modular.ecs.master.mapper.MemberMasterMapper;
import com.rxinyun.member.production.modular.ecs.master.param.MemberMasterIdParam;
import com.rxinyun.member.production.modular.ecs.master.service.MemberMasterService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberMasterServiceImpl extends ServiceImpl<MemberMasterMapper, MemberMaster> implements MemberMasterService {
   @Resource
   private MemberMasterMapper memberMasterMapper;
   @Resource
   private MemberLocalLineService memberLocalLineService;

   @Override
   public MemberMaster detail(MemberMasterIdParam serverMasterIdParam) {
      return this.queryEntity(serverMasterIdParam.getId());
   }

   @Override
   public MemberMaster queryEntity(String id) {
      MemberMaster serverMaster = (MemberMaster)this.getById(id);
      if (ObjectUtil.isEmpty(serverMaster)) {
         throw new CommonException("宿主机不存在，id值为：{}", new Object[]{id});
      } else {
         return serverMaster;
      }
   }

   @Override
   public MemberMaster queryByLineId(String lineId) {
      MemberLocalLine memberLocalLine = this.memberLocalLineService.queryEntity(lineId);
      if (ObjectUtil.isEmpty(memberLocalLine)) {
         throw new CommonException("线路不存在，id值为：{}", new Object[]{lineId});
      } else {
         Integer masterIndex = memberLocalLine.getMasterIndex();
         MemberMaster memberMaster = this.memberMasterMapper.selectFirstByLineId(lineId, EnableDisableEnum.ENABLE.getCode(), masterIndex);
         if (ObjectUtil.isEmpty(memberMaster)) {
            throw new CommonException("宿主机不存在，lineId值为：{}", new Object[]{lineId});
         } else {
            return memberMaster;
         }
      }
   }

   @Override
   public void updateHostStats(String masterId, Integer instanceCount, Integer ramRemaining, Long diskRemaining) {
      if (masterId != null) {
         this.memberMasterMapper.updateHostStats(masterId, instanceCount, ramRemaining, diskRemaining);
      }
   }
}
