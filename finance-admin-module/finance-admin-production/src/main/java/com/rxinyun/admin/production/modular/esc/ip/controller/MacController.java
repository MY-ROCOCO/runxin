package com.rxinyun.admin.production.modular.esc.ip.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.util.MacAddressGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "Mac地址管理控制器"
)
@RestController
@RequestMapping({"/admin/mac"})
@Validated
public class MacController {
   @Operation(
      summary = "获取Mac地址"
   )
   @GetMapping({"/random"})
   public CommonResult<String> randomMac(@Parameter(name = "type",description = "PUBLIC, PRIVATE, NAT",required = true) @RequestParam String type) {
      return CommonResult.data(MacAddressGenerator.generateMacAddress(type));
   }
}
