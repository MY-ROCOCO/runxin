package com.rxinyun.admin.dev.modular.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.message.entity.ToolsEmailTemplate;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplateAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplateEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplateIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsEmailTemplatePageParam;
import com.rxinyun.admin.dev.modular.message.service.ToolsEmailTemplateService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "消息通知邮件模板控制器"
)
@RestController
@Validated
public class ToolsEmailTemplateController {
   @Resource
   private ToolsEmailTemplateService toolsEmailTemplateService;

   @Operation(
      summary = "获取消息通知邮件模板分页"
   )
   @GetMapping({"/admin/tools/email/page"})
   public CommonResult<Page<ToolsEmailTemplate>> page(ToolsEmailTemplatePageParam toolsEmailTemplatePageParam) {
      return CommonResult.data(this.toolsEmailTemplateService.page(toolsEmailTemplatePageParam));
   }

   @Operation(
      summary = "获取消息通知邮件模板详情"
   )
   @GetMapping({"/admin/tools/email/detail"})
   public CommonResult<ToolsEmailTemplate> detail(@Valid ToolsEmailTemplateIdParam toolsEmailTemplateIdParam) {
      return CommonResult.data(this.toolsEmailTemplateService.detail(toolsEmailTemplateIdParam));
   }

   @Operation(
      summary = "添加消息通知邮件模板"
   )
   @CommonLog("添加消息通知邮件模板")
   @PostMapping({"/admin/tools/email/add"})
   public CommonResult<String> add(@RequestBody @Valid ToolsEmailTemplateAddParam toolsEmailTemplateAddParam) {
      this.toolsEmailTemplateService.add(toolsEmailTemplateAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑消息通知邮件模板"
   )
   @CommonLog("编辑消息通知邮件模板")
   @PostMapping({"/admin/tools/email/edit"})
   public CommonResult<String> edit(@RequestBody @Valid ToolsEmailTemplateEditParam toolsEmailTemplateEditParam) {
      this.toolsEmailTemplateService.edit(toolsEmailTemplateEditParam);
      return CommonResult.ok();
   }
}
