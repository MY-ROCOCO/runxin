package com.rxinyun.member.production.modular.independent.cloud.controller;

import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.independent.cloud.entity.MemberLine;
import com.rxinyun.member.production.modular.independent.cloud.param.MemberLineIdParam;
import com.rxinyun.member.production.modular.independent.cloud.service.MemberLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立线路管理控制器"
)
@RestController
@RequestMapping({"/member/independent"})
public class MemberLineController {
   @Resource
   private MemberLineService independentLineService;

   @CommonLog("获取线路")
   @Operation(
      summary = "获取线路"
   )
   @GetMapping({"/line/detail"})
   public CommonResult<MemberLine> detail(MemberLineIdParam memberLineIdParam) {
      return CommonResult.data(this.independentLineService.detail(memberLineIdParam));
   }

   @CommonLog("根据可用地域ID查询线路")
   @Operation(
      summary = "根据可用地域查询线路"
   )
   @GetMapping({"/line/region"})
   public CommonResult<List<MemberLine>> findLine(MemberLineIdParam memberLineIdParam) {
      return CommonResult.data(this.independentLineService.findLine(memberLineIdParam.getRegionId()));
   }
}
