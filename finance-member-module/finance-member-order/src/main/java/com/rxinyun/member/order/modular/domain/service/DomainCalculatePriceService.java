package com.rxinyun.member.order.modular.domain.service;

import com.rxinyun.member.order.modular.domain.param.DomainCalculatePriceParam;
import com.rxinyun.member.order.modular.domain.response.DomainCalculatePriceResponse;

public interface DomainCalculatePriceService {
   DomainCalculatePriceResponse calculatePrice(DomainCalculatePriceParam param);
}
