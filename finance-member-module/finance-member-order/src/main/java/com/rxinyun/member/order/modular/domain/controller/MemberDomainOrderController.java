package com.rxinyun.member.order.modular.domain.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.order.modular.domain.param.DomainOrderCreateParam;
import com.rxinyun.member.order.modular.ecs.entity.MemberOrder;
import com.rxinyun.member.order.modular.ecs.service.MemberOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-域名订单"
)
@RestController
@RequestMapping({"/member/domain/order"})
@Validated
public class MemberDomainOrderController {
   @Resource
   private MemberOrderService memberOrderService;

   @Operation(
      summary = "创建域名注册订单（可传多个域名，单价由后端按域名价格配置计算，不传单价）"
   )
   @PostMapping({"/create"})
   public CommonResult<MemberOrder> createOrder(@RequestBody @Validated DomainOrderCreateParam param) {
      return this.memberOrderService.createDomainOrder(param);
   }
}
