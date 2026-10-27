package com.rxinyun.member.business.modular.domain.provider;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.rxinyun.business.api.BusinessDomainApi;
import com.rxinyun.common.enums.business.DomainStatusEnum;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.common.util.TimeUtil;
import com.rxinyun.member.business.modular.domain.entity.BusinessDomain;
import com.rxinyun.member.business.modular.domain.mapper.MemberBusinessDomainMapper;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BusinessDomainApiProvider implements BusinessDomainApi {
   private static final Logger log = LoggerFactory.getLogger(BusinessDomainApiProvider.class);
   @Resource
   private MemberBusinessDomainMapper memberBusinessDomainMapper;

   public void addRegisteredDomain(String memberId, String domainName, String tld, int years, String templateId, String registrar) {
      if (!StrUtil.isBlank(memberId) && !StrUtil.isBlank(domainName)) {
         String effectiveTld = StrUtil.isNotBlank(tld) ? tld.trim() : extractTld(domainName);
         BusinessDomain domain = new BusinessDomain();
         domain.setId(IdWorker.getIdStr());
         domain.setMemberId(memberId);
         domain.setDomainName(domainName.trim());
         domain.setTld(effectiveTld);
         domain.setRegistrar(StrUtil.isNotBlank(registrar) ? registrar : "XINWANG");
         domain.setStatus(DomainStatusEnum.NORMAL.getCode());
         LocalDateTime expire = TimeUtil.now().plusYears(years);
         domain.setExpireTime(Date.from(expire.atZone(ZoneId.systemDefault()).toInstant()));
         domain.setTemplateId(StrUtil.isNotBlank(templateId) ? templateId : null);
         domain.setDeleteFlag("NOT_DELETE");
         domain.setCreateTime(new Date());
         domain.setUpdateTime(new Date());
         this.memberBusinessDomainMapper.insert(domain);
         log.info("业务域名已写入 memberId={} domainName={}", memberId, domainName);
      } else {
         log.warn("addRegisteredDomain 跳过：memberId 或 domainName 为空");
      }
   }

   public Long getDomainCountByCreateTime(LocalDateTime startTime, LocalDateTime endTime) {
      return this.memberBusinessDomainMapper
         .selectCount(
            new LambdaQueryWrapper<BusinessDomain>().ge(startTime != null, BusinessDomain::getCreateTime, startTime)
               .le(endTime != null, BusinessDomain::getCreateTime, endTime)
         );
   }

   private static String extractTld(String domainName) {
      return domainName != null && domainName.contains(".") ? domainName.substring(domainName.lastIndexOf(46) + 1) : "";
   }
}
