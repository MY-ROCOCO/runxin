package com.rxinyun.admin.dev.modular.email.controller;

import com.rxinyun.admin.dev.modular.email.param.DevEmailSendParam;
import com.rxinyun.admin.dev.modular.email.service.DevEmailService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@Tag(
   name = "统一邮件发送控制器"
)
@RequestMapping({"/admin/emailUnified"})
public class DevEmailUnifiedController {
   @Resource
   private DevEmailService devEmailService;

   @PostMapping({"/sendEmail"})
   @Operation(
      summary = "统一发送邮件"
   )
   @CommonLog("统一发送邮件")
   public CommonResult<Void> sendEmail(@RequestBody @Valid DevEmailSendParam param) {
      this.devEmailService.sendEmail(param);
      return CommonResult.ok();
   }
}
