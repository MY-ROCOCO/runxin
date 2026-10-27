package com.rxinyun.member.production.modular.domain.template.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.member.production.modular.domain.template.dto.ChinaRegionsVO;
import com.rxinyun.member.production.modular.domain.template.entity.DomainTemplate;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplateAddParam;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplateEditParam;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplateIdParam;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplatePageParam;

public interface MemberDomainTemplateService {
   Page<DomainTemplate> page(MemberDomainTemplatePageParam pageParam);

   void add(MemberDomainTemplateAddParam param);

   void edit(MemberDomainTemplateEditParam param);

   DomainTemplate getById(MemberDomainTemplateIdParam param);

   XinwangSubmitResult submitToUpstream(MemberDomainTemplateIdParam param);

   XinwangSubmitResult submitTemplate(DomainTemplate template);

   ChinaRegionsVO getChinaRegions();

   DomainTemplate syncTemplateStatus(MemberDomainTemplateIdParam param);

   DomainTemplate syncTemplateStatusByTemplateId(String templateId);

   void delete(MemberDomainTemplateIdParam param);
}
