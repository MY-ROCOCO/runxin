package com.rxinyun.admin.member.modular.agent.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.agent.modular.entity.AgentInfo;
import com.rxinyun.agent.modular.entity.AgentProductRatio;
import com.rxinyun.agent.modular.param.AgentAddParam;
import com.rxinyun.agent.modular.param.AgentBindIdParam;
import com.rxinyun.agent.modular.param.AgentBindParam;
import com.rxinyun.agent.modular.param.AgentCommissionPageParam;
import com.rxinyun.agent.modular.param.AgentEditParam;
import com.rxinyun.agent.modular.param.AgentIdParam;
import com.rxinyun.agent.modular.param.AgentMemberPageParam;
import com.rxinyun.agent.modular.param.AgentPageParam;
import com.rxinyun.agent.modular.param.AgentRatioListParam;
import com.rxinyun.agent.modular.param.AgentRatioSaveParam;
import com.rxinyun.agent.modular.param.AgentUserAddParam;
import com.rxinyun.agent.modular.param.AgentUserPageParam;
import com.rxinyun.agent.modular.param.AgentUserResetPasswordParam;
import com.rxinyun.agent.modular.param.AgentUserStatusParam;
import com.rxinyun.agent.modular.param.AgentWithdrawPageParam;
import com.rxinyun.agent.modular.param.AgentWithdrawReviewParam;
import com.rxinyun.agent.modular.response.AgentCommissionPageResponse;
import com.rxinyun.agent.modular.response.AgentMemberPageResponse;
import com.rxinyun.agent.modular.response.AgentUserPageResponse;
import com.rxinyun.agent.modular.response.AgentWithdrawPageResponse;
import com.rxinyun.agent.modular.service.AgentCommissionService;
import com.rxinyun.agent.modular.service.AgentInfoService;
import com.rxinyun.agent.modular.service.AgentMemberBindService;
import com.rxinyun.agent.modular.service.AgentProductRatioService;
import com.rxinyun.agent.modular.service.AgentUserService;
import com.rxinyun.agent.modular.service.AgentWithdrawService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "代理分销后台控制器"
)
@RestController
@RequestMapping({"/admin/agent"})
@Validated
public class AdminAgentController {
   @Resource
   private AgentInfoService agentInfoService;
   @Resource
   private AgentUserService agentUserService;
   @Resource
   private AgentMemberBindService agentMemberBindService;
   @Resource
   private AgentProductRatioService agentProductRatioService;
   @Resource
   private AgentCommissionService agentCommissionService;
   @Resource
   private AgentWithdrawService agentWithdrawService;

   @Operation(
      summary = "新增代理"
   )
   @CommonLog("新增代理")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid AgentAddParam addParam) {
      this.agentInfoService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑代理"
   )
   @CommonLog("编辑代理")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid AgentEditParam editParam) {
      this.agentInfoService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "停用代理"
   )
   @CommonLog("停用代理")
   @PostMapping({"/disable"})
   public CommonResult<String> disable(@RequestBody @Valid AgentIdParam idParam) {
      this.agentInfoService.disable(idParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "代理分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<AgentInfo>> page(AgentPageParam pageParam) {
      return CommonResult.data(this.agentInfoService.page(pageParam));
   }

   @Operation(
      summary = "新增代理账号"
   )
   @CommonLog("新增代理账号")
   @PostMapping({"/user/add"})
   public CommonResult<String> addUser(@RequestBody @Valid AgentUserAddParam addParam) {
      this.agentUserService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "代理账号分页"
   )
   @GetMapping({"/user/page"})
   public CommonResult<Page<AgentUserPageResponse>> userPage(AgentUserPageParam pageParam) {
      return CommonResult.data(this.agentUserService.page(pageParam));
   }

   @Operation(
      summary = "重置代理账号密码"
   )
   @CommonLog("重置代理账号密码")
   @PostMapping({"/user/resetPassword"})
   public CommonResult<String> resetPassword(@RequestBody @Valid AgentUserResetPasswordParam resetPasswordParam) {
      this.agentUserService.resetPassword(resetPasswordParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "停用代理账号"
   )
   @CommonLog("停用代理账号")
   @PostMapping({"/user/disable"})
   public CommonResult<String> disableUser(@RequestBody @Valid AgentUserStatusParam statusParam) {
      this.agentUserService.updateStatus(statusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "绑定会员到代理"
   )
   @CommonLog("绑定会员到代理")
   @PostMapping({"/member/bind"})
   public CommonResult<String> bindMember(@RequestBody @Valid AgentBindParam bindParam) {
      this.agentMemberBindService.bindByAdmin(bindParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "解除会员代理绑定"
   )
   @CommonLog("解除会员代理绑定")
   @PostMapping({"/member/unbind"})
   public CommonResult<String> unbindMember(@RequestBody @Valid AgentBindIdParam bindIdParam) {
      this.agentMemberBindService.unbind(bindIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "查看代理直属会员分页"
   )
   @GetMapping({"/member/page"})
   public CommonResult<Page<AgentMemberPageResponse>> memberPage(AgentMemberPageParam pageParam) {
      return CommonResult.data(this.agentMemberBindService.page(pageParam));
   }

   @Operation(
      summary = "新增或更新代理产品分成比例"
   )
   @CommonLog("新增或更新代理产品分成比例")
   @PostMapping({"/ratio/save"})
   public CommonResult<String> saveRatio(@RequestBody @Valid AgentRatioSaveParam saveParam) {
      this.agentProductRatioService.saveRatio(saveParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "查询代理产品分成比例"
   )
   @GetMapping({"/ratio/list"})
   public CommonResult<List<AgentProductRatio>> ratioList(@Valid AgentRatioListParam listParam) {
      return CommonResult.data(this.agentProductRatioService.listByAgentId(listParam.getAgentId()));
   }

   @Operation(
      summary = "查询代理分成流水"
   )
   @GetMapping({"/commission/page"})
   public CommonResult<Page<AgentCommissionPageResponse>> commissionPage(AgentCommissionPageParam pageParam) {
      return CommonResult.data(this.agentCommissionService.page(pageParam));
   }

   @Operation(
      summary = "查询代理提现记录"
   )
   @GetMapping({"/withdraw/page"})
   public CommonResult<Page<AgentWithdrawPageResponse>> withdrawPage(AgentWithdrawPageParam pageParam) {
      return CommonResult.data(this.agentWithdrawService.page(pageParam));
   }

   @Operation(
      summary = "审核代理提现"
   )
   @CommonLog("审核代理提现")
   @PostMapping({"/withdraw/review"})
   public CommonResult<String> reviewWithdraw(@RequestBody @Valid AgentWithdrawReviewParam reviewParam) {
      this.agentWithdrawService.review(reviewParam);
      return CommonResult.ok();
   }
}
