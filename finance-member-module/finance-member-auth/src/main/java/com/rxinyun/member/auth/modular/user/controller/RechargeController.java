package com.rxinyun.member.auth.modular.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.user.entity.Recharge;
import com.rxinyun.member.auth.modular.user.param.RechargeIdParam;
import com.rxinyun.member.auth.modular.user.param.RechargePageParam;
import com.rxinyun.member.auth.modular.user.service.RechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "C端用户充值记录"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class RechargeController {
   @Autowired
   private RechargeService rechargeService;

   @Operation(
      summary = "获取用户充值记录分页"
   )
   @GetMapping({"/recharge/page"})
   public CommonResult<Page<Recharge>> page(RechargePageParam rechargePageParam) {
      return CommonResult.data(this.rechargeService.page(rechargePageParam));
   }

   @Operation(
      summary = "获取当前用户充值记录详情"
   )
   @GetMapping({"/recharge/detail"})
   public CommonResult<Recharge> detail(@Valid RechargeIdParam rechargeIdParam) {
      return CommonResult.data(this.rechargeService.detailForCurrentMember(rechargeIdParam));
   }
}
