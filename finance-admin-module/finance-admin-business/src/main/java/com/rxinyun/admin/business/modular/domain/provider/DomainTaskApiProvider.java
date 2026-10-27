package com.rxinyun.admin.business.modular.domain.provider;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.rxinyun.admin.business.modular.domain.entity.BusinessDomain;
import com.rxinyun.admin.business.modular.domain.mapper.BusinessDomainMapper;
import com.rxinyun.admin.business.modular.domain.service.BusinessDomainService;
import com.rxinyun.business.api.DomainTaskApi;
import com.rxinyun.common.enums.business.DomainStatusEnum;
import com.rxinyun.common.util.TimeUtil;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DomainTaskApiProvider implements DomainTaskApi {
   private static final Logger log = LoggerFactory.getLogger(DomainTaskApiProvider.class);
   private static final String REGISTRATION_STATUS_CREATE_WAITING = "create-waiting";
   @Resource
   private BusinessDomainMapper businessDomainMapper;
   @Resource
   private BusinessDomainService businessDomainService;

   public void markExpiredDomains() {
      Date now = TimeUtil.toDate(TimeUtil.now());
      LambdaQueryWrapper<BusinessDomain> qw = (LambdaQueryWrapper<BusinessDomain>)((LambdaQueryWrapper<BusinessDomain>)new LambdaQueryWrapper<BusinessDomain>()
            .lt(BusinessDomain::getExpireTime, now))
         .ne(BusinessDomain::getStatus, DomainStatusEnum.EXPIRED.getCode());
      long count = this.businessDomainMapper.selectCount(qw);
      if (count != 0L) {
         LambdaUpdateWrapper<BusinessDomain> wrapper = (LambdaUpdateWrapper<BusinessDomain>)((LambdaUpdateWrapper<BusinessDomain>)((LambdaUpdateWrapper<BusinessDomain>)new LambdaUpdateWrapper<BusinessDomain>()
                  .lt(BusinessDomain::getExpireTime, now))
               .ne(BusinessDomain::getStatus, DomainStatusEnum.EXPIRED.getCode()))
            .set(BusinessDomain::getStatus, DomainStatusEnum.EXPIRED.getCode());
         this.businessDomainMapper.update(null, wrapper);
         log.info("域名到期检测: 已将 {} 个域名状态更新为已过期", count);
      }
   }

   public List<String> listCreateWaitingDomainNames() {
      LambdaQueryWrapper<BusinessDomain> wrapper = ((LambdaQueryWrapper<BusinessDomain>)new LambdaQueryWrapper<BusinessDomain>().eq(BusinessDomain::getRegistrationStatus, "create-waiting"))
         .select(BusinessDomain::getDomainName);
      return this.businessDomainMapper
         .selectList(wrapper)
         .stream()
         .map(BusinessDomain::getDomainName)
         .filter(d -> d != null && !d.isBlank())
         .distinct()
         .collect(Collectors.toList());
   }

   public void updateRegistrationStatus(String domainName, String registrationStatus) {
      this.businessDomainService.updateRegistrationStatus(domainName, registrationStatus);
   }
}
