package com.rxinyun.admin.dev.modular.telegram.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.telegram.entity.DevTelegram;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramIdParam;
import com.rxinyun.admin.dev.modular.telegram.param.DevTelegramPageParam;
import com.rxinyun.admin.dev.modular.telegram.service.DevTelegramService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "Telegram通知控制器"
)
@RestController
@Validated
public class DevTelegramController {
   @Resource
   private DevTelegramService devTelegramService;

   @Operation(
      summary = "获取Telegram发送记录分页"
   )
   @GetMapping({"/admin/dev/telegram/page"})
   public CommonResult<Page<DevTelegram>> page(DevTelegramPageParam devTelegramPageParam) {
      return CommonResult.data(this.devTelegramService.page(devTelegramPageParam));
   }

   @Operation(
      summary = "删除Telegram发送记录"
   )
   @CommonLog("删除Telegram发送记录")
   @PostMapping({"/admin/dev/telegram/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DevTelegramIdParam> devTelegramIdParamList) {
      this.devTelegramService.delete(devTelegramIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取Telegram发送记录详情"
   )
   @GetMapping({"/admin/dev/telegram/detail"})
   public CommonResult<DevTelegram> detail(@Valid DevTelegramIdParam devTelegramIdParam) {
      return CommonResult.data(this.devTelegramService.detail(devTelegramIdParam));
   }
}
