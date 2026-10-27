package com.rxinyun.member.production.modular.independent.os.controller;

import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.independent.os.entity.MemberIndependentOsClass;
import com.rxinyun.member.production.modular.independent.os.param.OsClassIdParam;
import com.rxinyun.member.production.modular.independent.os.service.MemberIndependentOsClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立服务器操作系统类别管理控制器"
)
@RestController
@RequestMapping({"/member/independent/os/class"})
@Validated
public class MemberIndependentOsClassController {
   @Resource
   private MemberIndependentOsClassService independentOsClassService;

   @Operation(
      summary = "购买独立服务器时获取操作系统类型集合"
   )
   @CommonLog("购买独立服务器时获取操作系统类型集合")
   @GetMapping({"/osTypeList"})
   public CommonResult<List<MemberIndependentOsClass>> typeList() {
      return CommonResult.data(this.independentOsClassService.osTypeList());
   }

   @Operation(
      summary = "获取操作系统类别详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberIndependentOsClass> detail(@Valid OsClassIdParam osClassIdParam) {
      return CommonResult.data(this.independentOsClassService.detail(osClassIdParam));
   }
}
