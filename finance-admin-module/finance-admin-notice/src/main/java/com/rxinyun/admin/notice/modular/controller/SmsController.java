package com.rxinyun.admin.notice.modular.controller;

import com.rxinyun.admin.notice.modular.domain.dto.TemplateSmsDTO;
import com.rxinyun.admin.notice.modular.domain.entity.TemplateSms;
import com.rxinyun.admin.notice.modular.service.SmsService;
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
@RequestMapping({"/admin/notice/sms"})
@Tag(
   name = "短信模版管理",
   description = "短信模版接口"
)
public class SmsController {
   @Resource
   SmsService service;

   @Operation(
      summary = "添加短信模版"
   )
   @CommonLog("添加短信模版")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid TemplateSmsDTO a) {
      this.service.add(a);
      return CommonResult.ok();
   }

   @Operation(
      summary = "edit短信模版"
   )
   @CommonLog("edit短信模版")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid TemplateSmsDTO a) {
      this.service.edit(a);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取sms详情"
   )
   @CommonLog("获取sms详情")
   @GetMapping({"/getById/{id}"})
   public CommonResult<TemplateSms> getById(@PathVariable String id) {
      return CommonResult.data(this.service.getById(id));
   }
}
