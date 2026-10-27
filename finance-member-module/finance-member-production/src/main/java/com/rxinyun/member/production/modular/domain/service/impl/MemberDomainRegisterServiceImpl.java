package com.rxinyun.member.production.modular.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.enums.business.DomainTemplateAuditStatusEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.dev.api.DevConfigApi;
import com.rxinyun.member.production.modular.domain.client.XinwangDomainApiClient;
import com.rxinyun.member.production.modular.domain.service.MemberDomainRegisterService;
import com.rxinyun.member.production.modular.domain.template.entity.DomainTemplate;
import com.rxinyun.member.production.modular.domain.template.mapper.MemberDomainTemplateMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class MemberDomainRegisterServiceImpl implements MemberDomainRegisterService {
   private static final String CONFIG_PRIMARY_PROVIDER = "DOMAIN_REGISTER_PRIMARY_PROVIDER";
   private static final String CONFIG_XINWANG_APP_ID = "DOMAIN_XINWANG_APP_ID";
   private static final String CONFIG_XINWANG_APP_KEY = "DOMAIN_XINWANG_APP_KEY";
   private static final String PROVIDER_XINWANG = "XINWANG";
   @Resource
   private DevConfigApi devConfigApi;
   @Resource
   private XinwangDomainApiClient xinwangDomainApiClient;
   @Resource
   private MemberDomainTemplateMapper memberDomainTemplateMapper;

   @Override
   public XinwangDomainApiClient.DomainCreateResult registerDomain(String domainName, String templateId, int years) {
      if (!StrUtil.isBlank(domainName) && !StrUtil.isBlank(templateId)) {
         String primaryProvider = StrUtil.nullToEmpty(this.devConfigApi.getValueByKey("DOMAIN_REGISTER_PRIMARY_PROVIDER")).trim().toUpperCase();
         if (!"XINWANG".equals(primaryProvider)) {
            throw new CommonException("当前首选通道不是新网，无法注册域名", new Object[0]);
         } else {
            DomainTemplate template = (DomainTemplate)this.memberDomainTemplateMapper.selectById(templateId);
            if (template == null) {
               throw new CommonException("模板不存在", new Object[0]);
            } else if (!DomainTemplateAuditStatusEnum.APPROVED.getCode().equals(template.getAuditStatus())) {
               throw new CommonException("模板未审核通过，无法用于注册", new Object[0]);
            } else {
               String templateName = StrUtil.blankToDefault(template.getName(), templateId);
               String appId = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_ID");
               String appKey = this.devConfigApi.getValueByKey("DOMAIN_XINWANG_APP_KEY");
               return this.xinwangDomainApiClient.createDomain(appId, appKey, domainName, templateName, years);
            }
         }
      } else {
         throw new CommonException("域名或模板不能为空", new Object[0]);
      }
   }
}
