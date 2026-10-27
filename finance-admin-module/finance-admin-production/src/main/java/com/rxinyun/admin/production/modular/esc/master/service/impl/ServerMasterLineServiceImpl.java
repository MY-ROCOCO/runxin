package com.rxinyun.admin.production.modular.esc.master.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.esc.master.entity.ServerMasterLine;
import com.rxinyun.admin.production.modular.esc.master.mapper.ServerMasterLineMapper;
import com.rxinyun.admin.production.modular.esc.master.service.ServerMasterLineService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServerMasterLineServiceImpl extends ServiceImpl<ServerMasterLineMapper, ServerMasterLine> implements ServerMasterLineService {
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void replaceMasterLines(String masterId, List<String> lineIds) {
      if (!StrUtil.isBlank(masterId)) {
         ((ServerMasterLineMapper)this.baseMapper).physicalDeleteByMasterId(masterId);
         if (!CollUtil.isEmpty(lineIds)) {
            List<ServerMasterLine> relations = lineIds.stream().filter(CharSequenceUtil::isNotBlank).distinct().map(lineId -> {
               ServerMasterLine relation = new ServerMasterLine();
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
   public List<String> listLineIdsByMasterId(String masterId) {
      return this.list((Wrapper<ServerMasterLine>)new LambdaQueryWrapper<ServerMasterLine>().select(ServerMasterLine::getLineId).eq(ServerMasterLine::getMasterId, masterId))
         .stream()
         .map(ServerMasterLine::getLineId)
         .filter(CharSequenceUtil::isNotBlank)
         .collect(Collectors.toList());
   }

   @Override
   public boolean existsByLineId(String lineId) {
      return StrUtil.isBlank(lineId) ? false : this.count((Wrapper<ServerMasterLine>)new LambdaQueryWrapper<ServerMasterLine>().eq(ServerMasterLine::getLineId, lineId)) > 0L;
   }
}
