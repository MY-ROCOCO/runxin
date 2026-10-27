package com.rxinyun.member.auth.modular.monitor.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.monitor.param.AuthExitSessionParam;
import com.rxinyun.member.auth.modular.monitor.param.AuthExitTokenParam;
import com.rxinyun.member.auth.modular.monitor.param.AuthSessionPageParam;
import com.rxinyun.member.auth.modular.monitor.result.AuthSessionAnalysisResult;
import com.rxinyun.member.auth.modular.monitor.result.AuthSessionPageResult;
import com.rxinyun.member.auth.modular.monitor.service.MemberAuthSessionService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会话治理控制器"
)
@RestController
@RequestMapping({"/member"})
@Validated
public class MemberAuthSessionController {
   @Resource
   private MemberAuthSessionService authSessionService;

   @Operation(
      summary = "会话统计"
   )
   @GetMapping({"/auth/session/analysis"})
   public CommonResult<AuthSessionAnalysisResult> analysis() {
      StpUtil.checkLogin();
      return CommonResult.data(this.authSessionService.analysis());
   }

   @Operation(
      summary = "查询C端会话"
   )
   @GetMapping({"/auth/session/c/page"})
   public CommonResult<Page<AuthSessionPageResult>> pageForC(AuthSessionPageParam authSessionPageParam) {
      StpUtil.checkLogin();
      return CommonResult.data(this.authSessionService.pageForC(authSessionPageParam));
   }

   @Operation(
      summary = "强退B端会话"
   )
   @CommonLog("强退B端会话")
   @PostMapping({"/auth/session/b/exit"})
   public CommonResult<String> exitSessionForB(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<AuthExitSessionParam> authExitSessionParamList) {
      StpUtil.checkLogin();
      this.authSessionService.exitSessionForB(authExitSessionParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "强退C端会话"
   )
   @CommonLog("强退C端会话")
   @PostMapping({"/auth/session/c/exit"})
   public CommonResult<String> exitSessionForC(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<AuthExitSessionParam> authExitSessionParamList) {
      StpUtil.checkLogin();
      this.authSessionService.exitSessionForC(authExitSessionParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "强退B端token"
   )
   @CommonLog("强退B端token")
   @PostMapping({"/auth/token/b/exit"})
   public CommonResult<String> exitTokenForB(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<AuthExitTokenParam> authExitTokenParamList) {
      StpUtil.checkLogin();
      this.authSessionService.exitTokenForB(authExitTokenParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "强退C端token"
   )
   @CommonLog("强退C端token")
   @PostMapping({"/auth/token/c/exit"})
   public CommonResult<String> exitTokenForC(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<AuthExitTokenParam> authExitTokenParamList) {
      StpUtil.checkLogin();
      this.authSessionService.exitTokenForC(authExitTokenParamList);
      return CommonResult.ok();
   }
}
