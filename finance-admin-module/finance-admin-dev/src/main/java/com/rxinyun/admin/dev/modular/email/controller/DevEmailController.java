package com.rxinyun.admin.dev.modular.email.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.email.entity.DevEmail;
import com.rxinyun.admin.dev.modular.email.param.DevEmailIdParam;
import com.rxinyun.admin.dev.modular.email.param.DevEmailPageParam;
import com.rxinyun.admin.dev.modular.email.service.DevEmailService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "邮件控制器"
)
@RestController
@Validated
public class DevEmailController {
   @Resource
   private DevEmailService devEmailService;

   @Operation(
      summary = "获取邮件分页"
   )
   @GetMapping({"/admin/dev/email/page"})
   public CommonResult<Page<DevEmail>> page(DevEmailPageParam devEmailPageParam) {
      return CommonResult.data(this.devEmailService.page(devEmailPageParam));
   }

   @Operation(
      summary = "获取邮件详情"
   )
   @GetMapping({"/admin/dev/email/detail"})
   public CommonResult<DevEmail> detail(@Valid DevEmailIdParam devEmailIdParam) {
      return CommonResult.data(this.devEmailService.detail(devEmailIdParam));
   }
}
