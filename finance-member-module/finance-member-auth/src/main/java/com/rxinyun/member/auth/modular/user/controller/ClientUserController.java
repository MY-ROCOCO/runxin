package com.rxinyun.member.auth.modular.user.controller;

import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.user.service.ClientUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "C端用户控制器"
)
@RestController("/member")
@Validated
public class ClientUserController {
   @Resource
   private ClientUserService clientUserService;

   @Operation(
      summary = "会员通知开关"
   )
   @PostMapping({"/member/client/user/updateNotification"})
   public CommonResult<String> updateNotification(@RequestParam @NotNull Integer notificationEnabled) {
      this.clientUserService.updateNotificationEnabled(notificationEnabled);
      return CommonResult.ok();
   }
}
