package com.rxinyun.admin.production.modular.esc.cloud.provider;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rxinyun.admin.production.modular.esc.available.entity.ServerAvailableRegion;
import com.rxinyun.admin.production.modular.esc.available.mapper.ServerAvailableRegionMapper;
import com.rxinyun.admin.production.modular.esc.available.service.ServerAvailableRegionService;
import com.rxinyun.admin.production.modular.esc.cloud.entity.ServerLocalLine;
import com.rxinyun.admin.production.modular.esc.cloud.mapper.ServerLocalLineMapper;
import com.rxinyun.admin.production.modular.esc.cloud.service.ServerLocalLineService;
import com.rxinyun.production.admin.ServerLocalLineApi;
import com.rxinyun.production.admin.core.pojo.BaseLocalLine;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServerLocalLineApiProvider implements ServerLocalLineApi {
   private static final Logger log = LoggerFactory.getLogger(ServerLocalLineApiProvider.class);
   @Resource
   private ServerLocalLineService serverLocalLineService;
   @Resource
   private ServerLocalLineMapper serverLocalLineMapper;
   @Resource
   private ServerAvailableRegionService serverAvailableRegionService;
   @Resource
   private ServerAvailableRegionMapper serverAvailableRegionMapper;

   public BaseLocalLine selectByLineId(String LineId) {
      ServerLocalLine serverLocalLine = this.serverLocalLineService.queryEntity(LineId);
      if (serverLocalLine != null) {
         BaseLocalLine baseLocalLine = (BaseLocalLine)BeanUtil.toBean(serverLocalLine, BaseLocalLine.class);
         if (StrUtil.isNotBlank(baseLocalLine.getAvailableId())) {
            try {
               ServerAvailableRegion availableRegion = this.serverAvailableRegionService.queryEntity(baseLocalLine.getAvailableId());
               if (availableRegion != null) {
                  baseLocalLine.setAvailableName(availableRegion.getAvailableName());
               }
            } catch (Exception var5) {
               log.warn("查询线路关联可用区名称失败，lineId={}，availableId={}", new Object[]{LineId, baseLocalLine.getAvailableId(), var5});
            }
         }

         return baseLocalLine;
      } else {
         return null;
      }
   }

   public Map<String, BaseLocalLine> selectByLineIds(List<String> lineIds) {
      if (CollUtil.isEmpty(lineIds)) {
         return Collections.emptyMap();
      } else {
         List<ServerLocalLine> lines = this.serverLocalLineMapper.selectList((Wrapper<ServerLocalLine>)new LambdaQueryWrapper<ServerLocalLine>().in(ServerLocalLine::getId, lineIds));
         if (CollUtil.isEmpty(lines)) {
            return Collections.emptyMap();
         } else {
            List<String> availableIds = lines.stream()
               .map(ServerLocalLine::getAvailableId)
               .filter(CharSequenceUtil::isNotBlank)
               .distinct()
               .collect(Collectors.toList());
            Map<String, String> availableNameMap = CollUtil.isEmpty(availableIds)
               ? Collections.emptyMap()
               : this.serverAvailableRegionMapper
                  .selectBatchIds(availableIds)
                  .stream()
                  .collect(Collectors.toMap(ServerAvailableRegion::getId, ServerAvailableRegion::getAvailableName, (a, b) -> a));
            return lines.stream().collect(Collectors.toMap(ServerLocalLine::getId, line -> {
               BaseLocalLine baseLocalLine = (BaseLocalLine)BeanUtil.toBean(line, BaseLocalLine.class);
               baseLocalLine.setAvailableName(availableNameMap.get(line.getAvailableId()));
               return baseLocalLine;
            }, (a, b) -> a));
         }
      }
   }
}
