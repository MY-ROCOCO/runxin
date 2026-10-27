package com.rxinyun.admin.member.modular.third.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.member.modular.third.entity.AuthThirdUser;
import com.rxinyun.admin.member.modular.third.param.AuthThirdUserPageParam;
import com.rxinyun.admin.member.modular.third.service.AdminAuthThirdService;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "三方登录控制器"
)
@RestController
@RequestMapping({"/admin"})
@Validated
public class AdminAuthThirdController {
   @Resource
   private AdminAuthThirdService adminAuthThirdService;

   @Operation(
      summary = "获取三方用户分页"
   )
   @GetMapping({"/auth/third/page"})
   public CommonResult<Page<AuthThirdUser>> page(AuthThirdUserPageParam authThirdUserPageParam) {
      return CommonResult.data(this.adminAuthThirdService.page(authThirdUserPageParam));
   }
}
