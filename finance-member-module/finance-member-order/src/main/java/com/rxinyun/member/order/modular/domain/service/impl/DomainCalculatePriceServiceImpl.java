package com.rxinyun.member.order.modular.domain.service.impl;

import cn.hutool.core.util.StrUtil;
import com.rxinyun.common.enums.order.OrderTypeEnum;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.exception.CommonException;
import com.rxinyun.marketing.api.dto.CouponCalculateResponse;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import com.rxinyun.member.order.modular.coupon.OrderCouponSupportService;
import com.rxinyun.member.order.modular.domain.param.DomainCalculatePriceItemParam;
import com.rxinyun.member.order.modular.domain.param.DomainCalculatePriceParam;
import com.rxinyun.member.order.modular.domain.response.DomainCalculatePriceItemVO;
import com.rxinyun.member.order.modular.domain.response.DomainCalculatePriceResponse;
import com.rxinyun.member.order.modular.domain.service.DomainCalculatePriceService;
import com.rxinyun.member.production.modular.domain.entity.DomainTldPrice;
import com.rxinyun.member.production.modular.domain.service.MemberDomainTldPriceService;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DomainCalculatePriceServiceImpl implements DomainCalculatePriceService {
   @Resource
   private MemberDomainTldPriceService memberDomainTldPriceService;
   @Resource
   private OrderCouponSupportService orderCouponSupportService;

   @Override
   public DomainCalculatePriceResponse calculatePrice(DomainCalculatePriceParam param) {
      DomainCalculatePriceResponse response = new DomainCalculatePriceResponse();
      List<DomainCalculatePriceItemVO> items = new ArrayList<>();
      BigDecimal total = BigDecimal.ZERO;

      for (DomainCalculatePriceItemParam p : param.getItems()) {
         String tld = p.getTld() != null ? p.getTld().trim() : "";
         DomainTldPrice priceConfig = this.memberDomainTldPriceService.getActiveByTld(tld);
         if (priceConfig == null || priceConfig.getRegisterPrice() == null) {
            throw new CommonException("后缀【{}】未配置或已关闭，无法计价", new Object[]{tld});
         }

         int years = p.getYears() == null ? 1 : Math.max(1, p.getYears());
         BigDecimal unitPrice = priceConfig.getRegisterPrice();
         BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf((long)years)).setScale(2, RoundingMode.HALF_UP);
         DomainCalculatePriceItemVO vo = new DomainCalculatePriceItemVO();
         vo.setDomainName(p.getDomainName());
         vo.setTld(tld);
         vo.setYears(years);
         vo.setUnitPrice(unitPrice);
         vo.setItemTotalPrice(itemTotal);
         items.add(vo);
         total = total.add(itemTotal);
      }

      response.setItems(items);
      response.setTotalPrice(total.setScale(2, RoundingMode.HALF_UP));
      response.setCouponPrice(BigDecimal.ZERO);
      response.setPayPrice(response.getTotalPrice());
      if (StrUtil.isNotBlank(param.getCouponCode())) {
         CouponCalculateResponse couponResponse = this.orderCouponSupportService
            .calculateCoupon(
               StpClientLoginUserUtil.getClientLoginUser().getId(),
               ProductTypeEnum.DOMAIN.getCode(),
               OrderTypeEnum.NEW_PURCHASE.getCode(),
               null,
               param.getCouponCode(),
               response.getPayPrice()
            );
         if (couponResponse != null) {
            response.setCouponPrice(couponResponse.getCouponPrice());
            response.setPayPrice(couponResponse.getPayPrice());
         }
      }

      return response;
   }
}
