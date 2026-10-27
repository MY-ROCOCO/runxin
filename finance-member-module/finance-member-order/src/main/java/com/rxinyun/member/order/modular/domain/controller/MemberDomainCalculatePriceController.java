package com.rxinyun.member.order.modular.domain.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.domain.param.DomainCalculatePriceParam;
import com.rxinyun.member.order.modular.domain.response.DomainCalculatePriceResponse;
import com.rxinyun.member.order.modular.domain.service.DomainCalculatePriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "域名价格计算控制器"
)
@RestController
@RequestMapping({"/member/domain"})
@Validated
public class MemberDomainCalculatePriceController {
   @Resource
   private DomainCalculatePriceService domainCalculatePriceService;

   @Operation(
      summary = "域名订单计价（按域名、后缀、年限计算总价与单价）"
   )
   @PostMapping({"/calculate"})
   public CommonResult<DomainCalculatePriceResponse> calculatePrice(@RequestBody @Validated DomainCalculatePriceParam param) {
      return CommonResult.data(this.domainCalculatePriceService.calculatePrice(param));
   }
}
