package com.rxinyun.admin.dev.modular.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.message.entity.ToolsTelegramTemplate;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplateAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplateEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplateIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsTelegramTemplatePageParam;
import com.rxinyun.admin.dev.modular.message.service.ToolsTelegramTemplateService;
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
   name = "消息通知Telegram模板控制器"
)
@RestController
@Validated
public class ToolsTelegramTemplateController {
   @Resource
   private ToolsTelegramTemplateService toolsTelegramTemplateService;

   @Operation(
      summary = "获取消息通知Telegram模板分页"
   )
   @GetMapping({"/admin/tools/telegram/page"})
   public CommonResult<Page<ToolsTelegramTemplate>> page(ToolsTelegramTemplatePageParam toolsTelegramTemplatePageParam) {
      return CommonResult.data(this.toolsTelegramTemplateService.page(toolsTelegramTemplatePageParam));
   }

   @Operation(
      summary = "获取消息通知Telegram模板详情"
   )
   @GetMapping({"/admin/tools/telegram/detail"})
   public CommonResult<ToolsTelegramTemplate> detail(@Valid ToolsTelegramTemplateIdParam toolsTelegramTemplateIdParam) {
      return CommonResult.data(this.toolsTelegramTemplateService.detail(toolsTelegramTemplateIdParam));
   }

   @Operation(
      summary = "添加消息通知Telegram模板"
   )
   @CommonLog("添加消息通知Telegram模板")
   @PostMapping({"/admin/tools/telegram/add"})
   public CommonResult<String> add(@RequestBody @Valid ToolsTelegramTemplateAddParam toolsTelegramTemplateAddParam) {
      this.toolsTelegramTemplateService.add(toolsTelegramTemplateAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑消息通知Telegram模板"
   )
   @CommonLog("编辑消息通知Telegram模板")
   @PostMapping({"/admin/tools/telegram/edit"})
   public CommonResult<String> edit(@RequestBody @Valid ToolsTelegramTemplateEditParam toolsTelegramTemplateEditParam) {
      this.toolsTelegramTemplateService.edit(toolsTelegramTemplateEditParam);
      return CommonResult.ok();
   }
}
