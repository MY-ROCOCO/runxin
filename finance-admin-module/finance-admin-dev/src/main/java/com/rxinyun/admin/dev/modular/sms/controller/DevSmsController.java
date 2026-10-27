package com.rxinyun.admin.dev.modular.sms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.dev.modular.sms.entity.DevSms;
import com.rxinyun.admin.dev.modular.sms.param.DevSmsIdParam;
import com.rxinyun.admin.dev.modular.sms.param.DevSmsPageParam;
import com.rxinyun.admin.dev.modular.sms.service.DevSmsService;
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
   name = "短信控制器"
)
@RestController
@Validated
public class DevSmsController {
   @Resource
   private DevSmsService devSmsService;

   @Operation(
      summary = "获取短信分页"
   )
   @GetMapping({"/admin/dev/sms/page"})
   public CommonResult<Page<DevSms>> page(DevSmsPageParam devSmsPageParam) {
      return CommonResult.data(this.devSmsService.page(devSmsPageParam));
   }

   @Operation(
      summary = "删除短信"
   )
   @CommonLog("删除短信")
   @PostMapping({"/admin/dev/sms/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DevSmsIdParam> devSmsIdParamList) {
      this.devSmsService.delete(devSmsIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取短信详情"
   )
   @GetMapping({"/admin/dev/sms/detail"})
   public CommonResult<DevSms> detail(@Valid DevSmsIdParam devSmsIdParam) {
      return CommonResult.data(this.devSmsService.detail(devSmsIdParam));
   }
}
