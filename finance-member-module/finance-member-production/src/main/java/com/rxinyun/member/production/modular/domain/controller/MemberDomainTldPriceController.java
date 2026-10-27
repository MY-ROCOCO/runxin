package com.rxinyun.member.production.modular.domain.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.domain.entity.DomainTldPrice;
import com.rxinyun.member.production.modular.domain.service.MemberDomainTldPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员端-域名价格"
)
@RestController
@RequestMapping({"/member/domain"})
@Validated
public class MemberDomainTldPriceController {
   @Resource
   private MemberDomainTldPriceService memberDomainTldPriceService;

   @Operation(
      summary = "域名价格列表（仅开启的后缀）"
   )
   @GetMapping({"/price/list"})
   public CommonResult<List<DomainTldPrice>> list() {
      return CommonResult.data(this.memberDomainTldPriceService.listActive());
   }
}
