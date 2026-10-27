package com.rxinyun.member.production.modular.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rxinyun.common.pojo.CommonEntity;
import com.rxinyun.member.production.modular.domain.entity.DomainTldPrice;
import com.rxinyun.member.production.modular.domain.mapper.MemberDomainTldPriceMapper;
import com.rxinyun.member.production.modular.domain.service.MemberDomainTldPriceService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MemberDomainTldPriceServiceImpl extends ServiceImpl<MemberDomainTldPriceMapper, DomainTldPrice> implements MemberDomainTldPriceService {
   private static final int STATUS_ACTIVE = 1;

   @Override
   public List<DomainTldPrice> listActive() {
      LambdaQueryWrapper<DomainTldPrice> wrapper = (LambdaQueryWrapper<DomainTldPrice>)((LambdaQueryWrapper<DomainTldPrice>)new LambdaQueryWrapper<DomainTldPrice>()
            .eq(DomainTldPrice::getStatus, 1))
         .orderByDesc(CommonEntity::getCreateTime);
      return this.list(wrapper);
   }

   @Override
   public DomainTldPrice getActiveByTld(String tld) {
      if (tld != null && !tld.isBlank()) {
         String normalizedTld = this.normalizeTld(tld);
         LambdaQueryWrapper<DomainTldPrice> wrapper = (LambdaQueryWrapper<DomainTldPrice>)((LambdaQueryWrapper<DomainTldPrice>)((LambdaQueryWrapper<DomainTldPrice>)((LambdaQueryWrapper<DomainTldPrice>)new LambdaQueryWrapper<DomainTldPrice>()
                     .eq(DomainTldPrice::getStatus, 1))
                  .and(
                     q -> ((LambdaQueryWrapper<DomainTldPrice>)((LambdaQueryWrapper<DomainTldPrice>)q.eq(DomainTldPrice::getTld, normalizedTld)).or())
                        .eq(DomainTldPrice::getTld, StrUtil.removePrefix(normalizedTld, "."))
                  ))
               .orderByDesc(CommonEntity::getCreateTime))
            .last("LIMIT 1");
         return (DomainTldPrice)this.getOne(wrapper);
      } else {
         return null;
      }
   }

   private String normalizeTld(String tld) {
      String value = tld.trim().toLowerCase();
      if (!value.startsWith(".")) {
         value = "." + value;
      }

      return value;
   }
}
