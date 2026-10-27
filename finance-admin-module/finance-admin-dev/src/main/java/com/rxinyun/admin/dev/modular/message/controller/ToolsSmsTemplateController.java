package com.rxinyun.admin.dev.modular.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.message.entity.ToolsSmsTemplate;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplateAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplateEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplateIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsSmsTemplatePageParam;
import com.rxinyun.admin.dev.modular.message.service.ToolsSmsTemplateService;
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
   name = "消息通知短信模板控制器"
)
@RestController
@Validated
public class ToolsSmsTemplateController {
   @Resource
   private ToolsSmsTemplateService toolsSmsTemplateService;

   @Operation(
      summary = "获取消息通知短信模板分页"
   )
   @GetMapping({"/admin/tools/sms/page"})
   public CommonResult<Page<ToolsSmsTemplate>> page(ToolsSmsTemplatePageParam toolsSmsTemplatePageParam) {
      return CommonResult.data(this.toolsSmsTemplateService.page(toolsSmsTemplatePageParam));
   }

   @Operation(
      summary = "获取消息通知短信模板详情"
   )
   @GetMapping({"/admin/tools/sms/detail"})
   public CommonResult<ToolsSmsTemplate> detail(@Valid ToolsSmsTemplateIdParam toolsSmsTemplateIdParam) {
      return CommonResult.data(this.toolsSmsTemplateService.detail(toolsSmsTemplateIdParam));
   }

   @Operation(
      summary = "添加消息通知短信模板"
   )
   @CommonLog("添加消息通知短信模板")
   @PostMapping({"/admin/tools/sms/add"})
   public CommonResult<String> add(@RequestBody @Valid ToolsSmsTemplateAddParam toolsSmsTemplateAddParam) {
      this.toolsSmsTemplateService.add(toolsSmsTemplateAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑消息通知短信模板"
   )
   @CommonLog("编辑消息通知短信模板")
   @PostMapping({"/admin/tools/sms/edit"})
   public CommonResult<String> edit(@RequestBody @Valid ToolsSmsTemplateEditParam toolsSmsTemplateEditParam) {
      this.toolsSmsTemplateService.edit(toolsSmsTemplateEditParam);
      return CommonResult.ok();
   }
}
