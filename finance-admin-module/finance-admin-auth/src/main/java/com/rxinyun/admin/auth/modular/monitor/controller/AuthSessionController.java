package com.rxinyun.admin.auth.modular.monitor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.auth.modular.monitor.param.AuthExitSessionParam;
import com.rxinyun.admin.auth.modular.monitor.param.AuthExitTokenParam;
import com.rxinyun.admin.auth.modular.monitor.param.AuthSessionPageParam;
import com.rxinyun.admin.auth.modular.monitor.result.AuthSessionAnalysisResult;
import com.rxinyun.admin.auth.modular.monitor.result.AuthSessionPageResult;
import com.rxinyun.admin.auth.modular.monitor.service.AuthSessionService;
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
   name = "会话治理控制器"
)
@RestController
@Validated
public class AuthSessionController {
   @Resource
   private AuthSessionService authSessionService;

   @Operation(
      summary = "会话统计"
   )
   @GetMapping({"/admin/auth/session/analysis"})
   public CommonResult<AuthSessionAnalysisResult> analysis() {
      return CommonResult.data(this.authSessionService.analysis());
   }

   @Operation(
      summary = "查询B端会话"
   )
   @GetMapping({"/admin/auth/session/b/page"})
   public CommonResult<Page<AuthSessionPageResult>> pageForB(AuthSessionPageParam authSessionPageParam) {
      return CommonResult.data(this.authSessionService.pageForB(authSessionPageParam));
   }

   @Operation(
      summary = "强退B端会话"
   )
   @CommonLog("强退B端会话")
   @PostMapping({"/admin/auth/session/b/exit"})
   public CommonResult<String> exitSessionForB(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<AuthExitSessionParam> authExitSessionParamList) {
      this.authSessionService.exitSessionForB(authExitSessionParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "强退B端token"
   )
   @CommonLog("强退B端token")
   @PostMapping({"/admin/auth/token/b/exit"})
   public CommonResult<String> exitTokenForB(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<AuthExitTokenParam> authExitTokenParamList) {
      this.authSessionService.exitTokenForB(authExitTokenParamList);
      return CommonResult.ok();
   }
}
