package com.rxinyun.admin.production.modular.vhost.master.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.vhost.master.entity.VhostMasterLine;
import com.rxinyun.admin.production.modular.vhost.master.mapper.VhostMasterLineMapper;
import com.rxinyun.admin.production.modular.vhost.master.service.VhostMasterLineService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VhostMasterLineServiceImpl extends ServiceImpl<VhostMasterLineMapper, VhostMasterLine> implements VhostMasterLineService {
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void replace(String masterId, List<String> lineIds) {
      if (!StrUtil.isBlank(masterId)) {
         ((VhostMasterLineMapper)this.baseMapper).physicalDeleteByMasterId(masterId);
         if (!CollUtil.isEmpty(lineIds)) {
            List<VhostMasterLine> relations = lineIds.stream().filter(CharSequenceUtil::isNotBlank).distinct().map(lineId -> {
               VhostMasterLine relation = new VhostMasterLine();
               relation.setId(IdUtil.getSnowflakeNextIdStr());
               relation.setMasterId(masterId);
               relation.setLineId(lineId);
               return relation;
            }).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(relations)) {
               this.saveBatch(relations);
            }
         }
      }
   }

   @Override
   public Map<String, List<String>> groupLineIds(Collection<String> masterIds) {
      return CollUtil.isEmpty(masterIds)
         ? Map.of()
         : this.list((Wrapper<VhostMasterLine>)new LambdaQueryWrapper<VhostMasterLine>().in(VhostMasterLine::getMasterId, masterIds))
            .stream()
            .collect(Collectors.groupingBy(VhostMasterLine::getMasterId, Collectors.mapping(VhostMasterLine::getLineId, Collectors.toList())));
   }

   @Override
   public boolean existsByLineId(String lineId) {
      return StrUtil.isBlank(lineId) ? false : this.count((Wrapper<VhostMasterLine>)new LambdaQueryWrapper<VhostMasterLine>().eq(VhostMasterLine::getLineId, lineId)) > 0L;
   }

   @Override
   public void physicalDeleteByMasterIds(Collection<String> masterIds) {
      if (!CollUtil.isEmpty(masterIds)) {
         masterIds.forEach(((VhostMasterLineMapper)this.baseMapper)::physicalDeleteByMasterId);
      }
   }
}
