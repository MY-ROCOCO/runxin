package com.rxinyun.member.production.modular.domain.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.domain.dto.DomainCheckResultVO;
import com.rxinyun.member.production.modular.domain.param.DomainCheckParam;
import com.rxinyun.member.production.modular.domain.service.MemberDomainCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-域名可注册查询"
)
@RestController
@RequestMapping({"/member/domain"})
@Validated
public class MemberDomainCheckController {
   @Resource
   private MemberDomainCheckService memberDomainCheckService;

   @Operation(
      summary = "查询域名是否可注册"
   )
   @PostMapping({"/check"})
   public CommonResult<DomainCheckResultVO> checkRegister(@RequestBody @Validated DomainCheckParam param) {
      DomainCheckResultVO result = this.memberDomainCheckService.checkRegister(param);
      return CommonResult.data(result);
   }
}
