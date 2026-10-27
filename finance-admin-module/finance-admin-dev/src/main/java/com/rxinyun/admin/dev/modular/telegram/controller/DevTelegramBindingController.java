package com.rxinyun.admin.dev.modular.telegram.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.telegram.entity.DevTelegramBinding;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindLinkResult;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindTokenParam;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramBindingPageParam;
import com.rxinyun.admin.dev.modular.telegram.service.DevTelegramBindingService;
import com.rxinyun.auth.core.util.StpLoginUserUtil;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.core.util.StpClientLoginUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "Telegram绑定控制器"
)
@RestController
@Validated
public class DevTelegramBindingController {
   @Resource
   private DevTelegramBindingService devTelegramBindingService;

   @Operation(
      summary = "获取Telegram绑定分页"
   )
   @GetMapping({"/admin/dev/telegram/binding/page"})
   public CommonResult<Page<DevTelegramBinding>> page(DevTelegramBindingPageParam devTelegramBindingPageParam) {
      return CommonResult.data(this.devTelegramBindingService.page(devTelegramBindingPageParam));
   }

   @Operation(
      summary = "为指定用户生成Telegram绑定链接"
   )
   @CommonLog("为指定用户生成Telegram绑定链接")
   @PostMapping({"/admin/dev/telegram/binding/createLink"})
   public CommonResult<DevTelegramBindLinkResult> createLink(@RequestBody @Valid DevTelegramBindTokenParam devTelegramBindTokenParam) {
      return CommonResult.data(this.devTelegramBindingService.createBindLink(devTelegramBindTokenParam));
   }

   @Operation(
      summary = "当前管理员生成Telegram绑定链接"
   )
   @CommonLog("当前管理员生成Telegram绑定链接")
   @PostMapping({"/admin/dev/telegram/binding/currentAdminLink"})
   public CommonResult<DevTelegramBindLinkResult> currentAdminLink() {
      DevTelegramBindTokenParam param = new DevTelegramBindTokenParam();
      param.setUserType("ADMIN");
      param.setUserId(StpLoginUserUtil.getLoginUser().getId());
      return CommonResult.data(this.devTelegramBindingService.createBindLink(param));
   }

   @Operation(
      summary = "当前会员生成Telegram绑定链接"
   )
   @CommonLog("当前会员生成Telegram绑定链接")
   @PostMapping({"/client/dev/telegram/binding/currentMemberLink"})
   public CommonResult<DevTelegramBindLinkResult> currentMemberLink() {
      DevTelegramBindTokenParam param = new DevTelegramBindTokenParam();
      param.setUserType("MEMBER");
      param.setUserId(String.valueOf(StpClientLoginUserUtil.getClientLoginUser().getId()));
      return CommonResult.data(this.devTelegramBindingService.createBindLink(param));
   }

   @Operation(
      summary = "Telegram Webhook回调"
   )
   @PostMapping({"/dev/telegram/webhook"})
   public CommonResult<String> webhook(
      @RequestBody JSONObject update, @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token",required = false) String secretToken
   ) {
      this.devTelegramBindingService.handleWebhook(update, secretToken);
      return CommonResult.ok();
   }
}
