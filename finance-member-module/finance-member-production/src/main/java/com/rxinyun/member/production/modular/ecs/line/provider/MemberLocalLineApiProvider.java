package com.rxinyun.member.production.modular.ecs.line.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.member.production.modular.ecs.available.entity.MemberAvailableRegion;
import com.rxinyun.member.production.modular.ecs.available.mapper.MemberAvailableRegionMapper;
import com.rxinyun.member.production.modular.ecs.line.entity.MemberLocalLine;
import com.rxinyun.member.production.modular.ecs.line.mapper.MemberLocalLineMapper;
import com.rxinyun.member.production.modular.ecs.line.service.MemberLocalLineService;
import com.rxinyun.production.api.MemberLocalLineApi;
import com.rxinyun.production.core.pojo.BaseMemberLocalLine;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MemberLocalLineApiProvider implements MemberLocalLineApi {
   private static final Logger log = LoggerFactory.getLogger(MemberLocalLineApiProvider.class);
   @Resource
   private MemberLocalLineService memberLocalLineService;
   @Resource
   private MemberLocalLineMapper memberLocalLineMapper;
   @Resource
   private MemberAvailableRegionMapper memberAvailableRegionMapper;

   public BaseMemberLocalLine selectByLineId(String LineId) {
      MemberLocalLine memberLocalLine = this.memberLocalLineService.queryEntity(LineId);
      if (memberLocalLine == null) {
         return null;
      } else {
         BaseMemberLocalLine baseMemberLocalLine = (BaseMemberLocalLine)BeanUtil.toBean(memberLocalLine, BaseMemberLocalLine.class);
         if (StrUtil.isNotBlank(baseMemberLocalLine.getAvailableId())) {
            MemberAvailableRegion availableRegion = (MemberAvailableRegion)this.memberAvailableRegionMapper.selectById(baseMemberLocalLine.getAvailableId());
            if (availableRegion != null) {
               baseMemberLocalLine.setAvailableName(availableRegion.getAvailableName());
            }
         }

         return baseMemberLocalLine;
      }
   }

   public Map<String, BaseMemberLocalLine> selectByLineIds(List<String> lineIds) {
      if (CollUtil.isEmpty(lineIds)) {
         return Collections.emptyMap();
      } else {
         List<MemberLocalLine> lines = this.memberLocalLineMapper.selectList((Wrapper<MemberLocalLine>)new LambdaQueryWrapper<MemberLocalLine>().in(MemberLocalLine::getId, lineIds));
         if (CollUtil.isEmpty(lines)) {
            return Collections.emptyMap();
         } else {
            List<String> availableIds = lines.stream()
               .map(MemberLocalLine::getAvailableId)
               .filter(CharSequenceUtil::isNotBlank)
               .distinct()
               .collect(Collectors.toList());
            Map<String, String> availableNameMap = CollUtil.isEmpty(availableIds)
               ? Collections.emptyMap()
               : this.memberAvailableRegionMapper
                  .selectBatchIds(availableIds)
                  .stream()
                  .collect(Collectors.toMap(MemberAvailableRegion::getId, MemberAvailableRegion::getAvailableName, (a, b) -> a));
            return lines.stream().collect(Collectors.toMap(MemberLocalLine::getId, line -> {
               BaseMemberLocalLine baseLocalLine = (BaseMemberLocalLine)BeanUtil.toBean(line, BaseMemberLocalLine.class);
               baseLocalLine.setAvailableName(availableNameMap.get(line.getAvailableId()));
               return baseLocalLine;
            }, (a, b) -> a));
         }
      }
   }
}
