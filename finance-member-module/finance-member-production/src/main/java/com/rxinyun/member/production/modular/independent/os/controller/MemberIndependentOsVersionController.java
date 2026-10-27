package com.rxinyun.member.production.modular.independent.os.controller;

import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.independent.os.entity.MemberIndependentOsVersion;
import com.rxinyun.member.production.modular.independent.os.param.OsVersionIdParam;
import com.rxinyun.member.production.modular.independent.os.service.MemberIndependentOsVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立服务器操作系统版本管理控制器"
)
@RestController
@RequestMapping({"/member/independent/os/version"})
@Validated
public class MemberIndependentOsVersionController {
   @Resource
   private MemberIndependentOsVersionService independentOsVersionService;

   @Operation(
      summary = "根据线路id查询操作系统版本列表（osClassId 可选）"
   )
   @CommonLog("根据线路id查询操作系统版本列表")
   @GetMapping({"/osList/{lineId}"})
   public CommonResult<List<MemberIndependentOsVersion>> osList(
      @PathVariable @Parameter(description = "线路id",required = true) @NotBlank String lineId,
      @RequestParam(value = "osClassId",required = false) @Parameter(description = "操作系统类型id，不传则返回该线路下全部操作系统版本") String osClassId
   ) {
      return CommonResult.data(this.independentOsVersionService.osList(osClassId, lineId));
   }

   @Operation(
      summary = "根据操作系统类型id与线路id查询操作系统版本（兼容旧路径）"
   )
   @CommonLog("根据操作系统类型id与线路id查询操作系统版本")
   @GetMapping({"/osList/{osClassId}/{lineId}"})
   public CommonResult<List<MemberIndependentOsVersion>> osListByClassAndLine(
      @PathVariable @Parameter(description = "操作系统类型id",required = true) @NotBlank String osClassId,
      @PathVariable @Parameter(description = "线路id",required = true) @NotBlank String lineId
   ) {
      return CommonResult.data(this.independentOsVersionService.osList(osClassId, lineId));
   }

   @Operation(
      summary = "获取操作系统版本详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberIndependentOsVersion> detail(@Valid OsVersionIdParam osVersionIdParam) {
      return CommonResult.data(this.independentOsVersionService.detail(osVersionIdParam));
   }
}
