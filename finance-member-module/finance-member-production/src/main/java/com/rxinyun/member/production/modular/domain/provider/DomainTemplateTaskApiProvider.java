package com.rxinyun.member.production.modular.domain.provider;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.rxinyun.common.enums.business.DomainTemplateAuditStatusEnum;
import com.rxinyun.member.production.modular.domain.template.entity.DomainTemplate;
import com.rxinyun.member.production.modular.domain.template.mapper.MemberDomainTemplateMapper;
import com.rxinyun.member.production.modular.domain.template.service.MemberDomainTemplateService;
import com.rxinyun.production.api.DomainTemplateTaskApi;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DomainTemplateTaskApiProvider implements DomainTemplateTaskApi {
   private static final Logger log = LoggerFactory.getLogger(DomainTemplateTaskApiProvider.class);
   @Resource
   private MemberDomainTemplateMapper memberDomainTemplateMapper;
   @Resource
   private MemberDomainTemplateService memberDomainTemplateService;

   public List<String> listAuditingTemplateIds() {
      LambdaQueryWrapper<DomainTemplate> wrapper = ((LambdaQueryWrapper<DomainTemplate>)new LambdaQueryWrapper<DomainTemplate>()
            .eq(DomainTemplate::getAuditStatus, DomainTemplateAuditStatusEnum.AUDITING.getCode()))
         .select(DomainTemplate::getId);
      return this.memberDomainTemplateMapper
         .selectList(wrapper)
         .stream()
         .map(DomainTemplate::getId)
         .filter(id -> id != null && !id.isBlank())
         .collect(Collectors.toList());
   }

   public void syncTemplateStatusByTemplateId(String templateId) {
      if (templateId != null && !templateId.isBlank()) {
         try {
            this.memberDomainTemplateService.syncTemplateStatusByTemplateId(templateId);
         } catch (Exception var3) {
            log.warn("定时任务同步模板状态失败 templateId={} err={}", templateId, var3.getMessage());
         }
      }
   }
}
