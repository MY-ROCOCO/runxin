package com.rxinyun.admin.dev.modular.message.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.message.entity.ToolsNotification;
import com.rxinyun.admin.dev.modular.message.param.NotificationSendParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationAddParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationBindTemplateParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationEditParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationIdParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationPageParam;
import com.rxinyun.admin.dev.modular.message.param.ToolsNotificationStatusParam;
import com.rxinyun.admin.dev.modular.message.service.ToolsNotificationService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "消息通知场景控制器"
)
@RestController
@Validated
public class ToolsNotificationController {
   @Resource
   private ToolsNotificationService toolsNotificationService;

   @Operation(
      summary = "获取消息场景分页"
   )
   @GetMapping({"/admin/tools/notification/page"})
   public CommonResult<Page<ToolsNotification>> page(ToolsNotificationPageParam toolsNotificationPageParam) {
      return CommonResult.data(this.toolsNotificationService.page(toolsNotificationPageParam));
   }

   @Operation(
      summary = "获取消息通知场景详情"
   )
   @GetMapping({"/admin/tools/notification/detail"})
   public CommonResult<ToolsNotification> detail(@Valid ToolsNotificationIdParam toolsNotificationIdParam) {
      return CommonResult.data(this.toolsNotificationService.detail(toolsNotificationIdParam));
   }

   @Operation(
      summary = "添加消息通知场景"
   )
   @CommonLog("添加消息通知场景")
   @PostMapping({"/admin/tools/notification/add"})
   public CommonResult<String> add(@RequestBody @Valid ToolsNotificationAddParam toolsNotificationAddParam) {
      this.toolsNotificationService.add(toolsNotificationAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑消息通知场景"
   )
   @CommonLog("编辑消息通知场景")
   @PostMapping({"/admin/tools/notification/edit"})
   public CommonResult<String> edit(@RequestBody @Valid ToolsNotificationEditParam toolsNotificationEditParam) {
      this.toolsNotificationService.edit(toolsNotificationEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除消息通知场景"
   )
   @CommonLog("删除消息通知场景")
   @PostMapping({"/admin/tools/notification/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<ToolsNotificationIdParam> toolsNotificationIdParamList) {
      this.toolsNotificationService.delete(toolsNotificationIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "公众号模板开关"
   )
   @CommonLog("公众号模板开关")
   @PostMapping({"/admin/tools/notification/wxTemplateSwitch"})
   public CommonResult<String> wxTemplateSwitch(@RequestBody ToolsNotificationStatusParam toolsNotificationStatusParam) {
      this.toolsNotificationService.wxTemplateSwitch(toolsNotificationStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "绑定公众号模板"
   )
   @CommonLog("绑定公众号模板")
   @PostMapping({"/admin/tools/notification/bindWxTemplate"})
   public CommonResult<String> bindWxTemplate(@RequestBody ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      this.toolsNotificationService.bindWxTemplate(toolsNotificationBindTemplateParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "小程序订阅模板开关"
   )
   @CommonLog("小程序订阅模板开关")
   @PostMapping({"/admin/tools/notification/wxSubscribeTemplateSwitch"})
   public CommonResult<String> wxSubscribeTemplateSwitch(@RequestBody ToolsNotificationStatusParam toolsNotificationStatusParam) {
      this.toolsNotificationService.wxSubscribeTemplateSwitch(toolsNotificationStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "绑定小程序订阅模板"
   )
   @CommonLog("绑定小程序订阅模板")
   @PostMapping({"/admin/tools/notification/bindWxSubscribeTemplate"})
   public CommonResult<String> bindWxSubscribeTemplate(@RequestBody ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      this.toolsNotificationService.bindWxSubscribeTemplate(toolsNotificationBindTemplateParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "短信模板开关"
   )
   @CommonLog("短信模板开关")
   @PostMapping({"/admin/tools/notification/smsTemplateSwitch"})
   public CommonResult<String> smsTemplateSwitch(@RequestBody ToolsNotificationStatusParam toolsNotificationStatusParam) {
      this.toolsNotificationService.smsTemplateSwitch(toolsNotificationStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "绑定短信模板"
   )
   @CommonLog("绑定短信模板")
   @PostMapping({"/admin/tools/notification/bindSmsTemplate"})
   public CommonResult<String> bindSmsTemplate(@RequestBody ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      this.toolsNotificationService.bindSmsTemplate(toolsNotificationBindTemplateParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "钉钉模板开关"
   )
   @CommonLog("钉钉模板开关")
   @PostMapping({"/admin/tools/notification/dingTemplateSwitch"})
   public CommonResult<String> dingTemplateSwitch(@RequestBody ToolsNotificationStatusParam toolsNotificationStatusParam) {
      this.toolsNotificationService.dingTemplateSwitch(toolsNotificationStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "绑定钉钉模板"
   )
   @CommonLog("绑定钉钉模板")
   @PostMapping({"/admin/tools/notification/bindDingTemplate"})
   public CommonResult<String> bindDingTemplate(@RequestBody ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      this.toolsNotificationService.bindDingTemplate(toolsNotificationBindTemplateParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "企业微信模板开关"
   )
   @CommonLog("企业微信模板开关")
   @PostMapping({"/admin/tools/notification/wxWorkTemplateSwitch"})
   public CommonResult<String> wxWorkTemplateSwitch(@RequestBody ToolsNotificationStatusParam toolsNotificationStatusParam) {
      this.toolsNotificationService.wxWorkTemplateSwitch(toolsNotificationStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "绑定企业微信模板"
   )
   @CommonLog("绑定企业微信模板")
   @PostMapping({"/admin/tools/notification/bindWxWorkTemplate"})
   public CommonResult<String> bindWxWorkTemplate(@RequestBody ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      this.toolsNotificationService.bindWxWorkTemplate(toolsNotificationBindTemplateParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "邮箱模板开关"
   )
   @CommonLog("邮箱模板开关")
   @PostMapping({"/admin/tools/notification/emailTemplateSwitch"})
   public CommonResult<String> emailTemplateSwitch(@RequestBody ToolsNotificationStatusParam toolsNotificationStatusParam) {
      this.toolsNotificationService.emailTemplateSwitch(toolsNotificationStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "绑定邮箱模板"
   )
   @CommonLog("绑定邮箱模板")
   @PostMapping({"/admin/tools/notification/bindEmailTemplate"})
   public CommonResult<String> bindEmailTemplate(@RequestBody ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      this.toolsNotificationService.bindEmailTemplate(toolsNotificationBindTemplateParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "Telegram模板开关"
   )
   @CommonLog("Telegram模板开关")
   @PostMapping({"/admin/tools/notification/telegramTemplateSwitch"})
   public CommonResult<String> telegramTemplateSwitch(@RequestBody ToolsNotificationStatusParam toolsNotificationStatusParam) {
      this.toolsNotificationService.telegramTemplateSwitch(toolsNotificationStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "绑定Telegram模板"
   )
   @CommonLog("绑定Telegram模板")
   @PostMapping({"/admin/tools/notification/bindTelegramTemplate"})
   public CommonResult<String> bindTelegramTemplate(@RequestBody ToolsNotificationBindTemplateParam toolsNotificationBindTemplateParam) {
      this.toolsNotificationService.bindTelegramTemplate(toolsNotificationBindTemplateParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "场景启用禁用开关"
   )
   @CommonLog("场景启用禁用开关")
   @PostMapping({"/admin/tools/notification/status"})
   public CommonResult<String> status(@RequestBody ToolsNotificationStatusParam toolsNotificationStatusParam) {
      this.toolsNotificationService.status(toolsNotificationStatusParam);
      return CommonResult.ok();
   }

   @PostMapping({"/sendSms"})
   @Operation(
      summary = "根据场景发送短信"
   )
   @CommonLog("根据场景发送短信")
   public CommonResult<Void> sendNotificationSms(@RequestBody @Valid NotificationSendParam param) {
      this.toolsNotificationService.sendNotificationSms(param.getNotificationId(), param.getPhoneNumbers(), param.getTemplateParams());
      return CommonResult.ok();
   }

   @PostMapping({"/sendEmail"})
   @Operation(
      summary = "根据场景发送邮件"
   )
   @CommonLog("根据场景发送邮件")
   public CommonResult<String> sendNotificationEmail(@RequestBody @Valid NotificationSendParam param) {
      this.toolsNotificationService.sendNotificationEmail(param.getNotificationId(), param.getEmailAddresses(), param.getTemplateParams());
      return CommonResult.ok();
   }

   @PostMapping({"/sendTelegram"})
   @Operation(
      summary = "根据场景发送Telegram消息"
   )
   @CommonLog("根据场景发送Telegram消息")
   public CommonResult<String> sendNotificationTelegram(@RequestBody @Valid NotificationSendParam param) {
      this.toolsNotificationService.sendNotificationTelegram(param.getNotificationId(), param.getChatId(), param.getTemplateParams());
      return CommonResult.ok();
   }

   @GetMapping({"/getSceneParams/{notificationId}"})
   @Operation(
      summary = "获取场景支持的参数说明"
   )
   public CommonResult<Map<String, String>> getSceneParamDescription(@PathVariable String notificationId) {
      Map<String, String> paramDescriptions = this.toolsNotificationService.getSceneParamDescription(notificationId);
      return CommonResult.data(paramDescriptions);
   }
}
