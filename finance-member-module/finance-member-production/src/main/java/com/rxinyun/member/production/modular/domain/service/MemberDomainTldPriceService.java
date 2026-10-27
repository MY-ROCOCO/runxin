package com.rxinyun.member.production.modular.domain.service;

import com.rxinyun.member.production.modular.domain.entity.DomainTldPrice;
import java.util.List;

public interface MemberDomainTldPriceService {
   List<DomainTldPrice> listActive();

   DomainTldPrice getActiveByTld(String tld);
}
