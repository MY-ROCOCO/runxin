package com.rxinyun.member.production.modular.domain.provider;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.rxinyun.member.production.modular.domain.template.entity.DomainTemplate;
import com.rxinyun.member.production.modular.domain.template.mapper.MemberDomainTemplateMapper;
import com.rxinyun.production.api.DomainTemplateQueryApi;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DomainTemplateQueryApiProvider implements DomainTemplateQueryApi {
   @Resource
   private MemberDomainTemplateMapper memberDomainTemplateMapper;

   public Map<String, String> getTemplateNamesByIds(List<String> templateIds) {
      if (templateIds != null && !templateIds.isEmpty()) {
         List<String> ids = templateIds.stream().filter(id -> id != null && !id.isBlank()).distinct().collect(Collectors.toList());
         if (ids.isEmpty()) {
            return Collections.emptyMap();
         } else {
            LambdaQueryWrapper<DomainTemplate> wrapper = ((LambdaQueryWrapper<DomainTemplate>)new LambdaQueryWrapper<DomainTemplate>().in(DomainTemplate::getId, ids))
               .select(DomainTemplate::getId, DomainTemplate::getName);
            return this.memberDomainTemplateMapper
               .selectList(wrapper)
               .stream()
               .collect(Collectors.toMap(DomainTemplate::getId, t -> StrUtil.nullToEmpty(t.getName()), (a, b) -> a));
         }
      } else {
         return Collections.emptyMap();
      }
   }
}
