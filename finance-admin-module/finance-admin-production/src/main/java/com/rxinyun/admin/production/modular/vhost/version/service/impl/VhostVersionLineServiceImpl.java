package com.rxinyun.admin.production.modular.vhost.version.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.admin.production.modular.vhost.version.entity.VhostVersionLine;
import com.rxinyun.admin.production.modular.vhost.version.mapper.VhostVersionLineMapper;
import com.rxinyun.admin.production.modular.vhost.version.service.VhostVersionLineService;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VhostVersionLineServiceImpl extends ServiceImpl<VhostVersionLineMapper, VhostVersionLine> implements VhostVersionLineService {
   @Transactional(
      rollbackFor = {Exception.class}
   )
   @Override
   public void replace(String versionId, List<String> lineIds) {
      if (!StrUtil.isBlank(versionId)) {
         ((VhostVersionLineMapper)this.baseMapper).physicalDeleteByVersionId(versionId);
         if (!CollUtil.isEmpty(lineIds)) {
            List<VhostVersionLine> relations = lineIds.stream().filter(CharSequenceUtil::isNotBlank).distinct().map(lineId -> {
               VhostVersionLine relation = new VhostVersionLine();
               relation.setId(IdUtil.getSnowflakeNextIdStr());
               relation.setVersionId(versionId);
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
   public Map<String, List<String>> groupLineIds(Collection<String> versionIds) {
      return CollUtil.isEmpty(versionIds)
         ? Map.of()
         : this.list((Wrapper<VhostVersionLine>)new LambdaQueryWrapper<VhostVersionLine>().in(VhostVersionLine::getVersionId, versionIds))
            .stream()
            .collect(Collectors.groupingBy(VhostVersionLine::getVersionId, Collectors.mapping(VhostVersionLine::getLineId, Collectors.toList())));
   }

   @Override
   public boolean existsByLineId(String lineId) {
      return StrUtil.isBlank(lineId) ? false : this.count((Wrapper<VhostVersionLine>)new LambdaQueryWrapper<VhostVersionLine>().eq(VhostVersionLine::getLineId, lineId)) > 0L;
   }

   @Override
   public void physicalDeleteByVersionIds(Collection<String> versionIds) {
      if (!CollUtil.isEmpty(versionIds)) {
         versionIds.forEach(((VhostVersionLineMapper)this.baseMapper)::physicalDeleteByVersionId);
      }
   }
}
