package com.rxinyun.admin.notice.modular.controller;

import com.rxinyun.admin.notice.modular.domain.dto.TemplateEmailDTO;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateEmail;
import com.rxinyun.admin.notice.modular.service.EmailService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping({"/admin/notice/email"})
@Tag(
   name = "邮件模版管理",
   description = "邮件模版接口"
)
public class EmailController {
   @Resource
   EmailService emailService;

   @Operation(
      summary = "添加邮件模版"
   )
   @CommonLog("添加邮件模版")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid TemplateEmailDTO templateEmailDTO) {
      this.emailService.add(templateEmailDTO);
      return CommonResult.ok();
   }

   @Operation(
      summary = "edit邮件模版"
   )
   @CommonLog("edit邮件模版")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid TemplateEmailDTO templateEmailDTO) {
      this.emailService.edit(templateEmailDTO);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取邮件详情"
   )
   @CommonLog("获取邮件详情")
   @GetMapping({"/getById/{id}"})
   public CommonResult<TemplateEmail> getById(@PathVariable String id) {
      return CommonResult.data(this.emailService.getById(id));
   }
}
