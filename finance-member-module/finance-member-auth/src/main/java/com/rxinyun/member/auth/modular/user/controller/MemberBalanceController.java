package com.rxinyun.member.auth.modular.user.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.user.entity.MemberBalance;
import com.rxinyun.member.auth.modular.user.service.MemberBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "C端用户余额控制器"
)
@RestController
@Validated
public class MemberBalanceController {
   @Autowired
   private MemberBalanceService memberBalanceService;

   @Operation(
      summary = "获取C端用户余额详情"
   )
   @GetMapping({"/member/balance/detail"})
   public CommonResult<MemberBalance> detail() {
      return CommonResult.data(this.memberBalanceService.select());
   }
}
