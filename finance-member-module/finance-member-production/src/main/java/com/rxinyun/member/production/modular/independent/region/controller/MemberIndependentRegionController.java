package com.rxinyun.member.production.modular.independent.region.controller;

import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.independent.region.model.entity.Region;
import com.rxinyun.member.production.modular.independent.region.service.IMemberRegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立服务器地域管理控制器"
)
@RestController
@RequestMapping({"/member/independent/region"})
@Validated
public class MemberIndependentRegionController {
   @Resource
   IMemberRegionService regionService;

   @Operation(
      summary = "购买独立服务器时显示地域"
   )
   @CommonLog("购买独立服务器时显示地域")
   @GetMapping({"/purchase"})
   public CommonResult<List<Region>> purchase() {
      return this.regionService.purchase();
   }
}
