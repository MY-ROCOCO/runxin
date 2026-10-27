package com.rxinyun.admin.member.modular.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.member.modular.user.entity.AdminClientUser;
import com.rxinyun.admin.member.modular.user.param.AdminMemberAddParam;
import com.rxinyun.admin.member.modular.user.param.ClientEmailUpdateParam;
import com.rxinyun.admin.member.modular.user.param.ClientPhoneUpdateParam;
import com.rxinyun.admin.member.modular.user.param.ClientResetPasswordParam;
import com.rxinyun.admin.member.modular.user.param.ClientSwitchLoginTicketParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserDataStatisticsParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserIdParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserPageParam;
import com.rxinyun.admin.member.modular.user.param.ClientUserStatusParam;
import com.rxinyun.admin.member.modular.user.param.UpdateMemberGroupParam;
import com.rxinyun.admin.member.modular.user.response.ClientUserResponse;
import com.rxinyun.admin.member.modular.user.service.AdminClientUserService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.payments.api.pojo.PaymentsAdminBalancePram;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "会员管理控制器"
)
@RestController
@RequestMapping({"/admin"})
@Validated
public class AdminClientUserController {
   @Resource
   private AdminClientUserService clientUserService;

   @Operation(
      summary = "获取C端用户分页"
   )
   @GetMapping({"/member/page"})
   public CommonResult<Page<ClientUserResponse>> page(ClientUserPageParam clientUserPageParam) {
      return CommonResult.data(this.clientUserService.page(clientUserPageParam));
   }

   @Operation(
      summary = "C端用户重置密码"
   )
   @CommonLog("C端用户重置密码")
   @PostMapping({"/member/reset/password"})
   public CommonResult<String> resetPassword(@RequestBody @Valid ClientResetPasswordParam clientResetPasswordParam) {
      this.clientUserService.resetPassword(clientResetPasswordParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "C端用户修改手机号"
   )
   @CommonLog("C端用户修改手机号")
   @PostMapping({"/member/update/phone"})
   public CommonResult<String> updatePhone(@RequestBody @Valid ClientPhoneUpdateParam updatePhoneParam) {
      this.clientUserService.updatePhone(updatePhoneParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改会员邮箱"
   )
   @CommonLog("修改会员邮箱")
   @PostMapping({"/member/update/email"})
   public CommonResult<String> updateEmail(@RequestBody @Valid ClientEmailUpdateParam updateEmailParam) {
      this.clientUserService.updateEmail(updateEmailParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "后台添加会员"
   )
   @CommonLog("后台添加会员")
   @PostMapping({"/member/add"})
   public CommonResult<String> addMember(@RequestBody @Valid AdminMemberAddParam addParam) {
      this.clientUserService.addMemberByAdmin(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "用户状态修改"
   )
   @CommonLog("用户状态修改")
   @PostMapping({"/member/updateStatus"})
   public CommonResult<String> updateStatus(@RequestBody @Valid ClientUserStatusParam clientUserStatusParam) {
      this.clientUserService.updateStatus(clientUserStatusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "查询指定用户数据统计"
   )
   @CommonLog("查询指定用户数据统计")
   @GetMapping({"/member/statistics"})
   public CommonResult<ClientUserDataStatisticsParam> statistics(@RequestParam @NotNull Long userId) {
      return CommonResult.data(this.clientUserService.findDatastatistics(userId));
   }

   @Operation(
      summary = "删除C端用户"
   )
   @CommonLog("删除C端用户")
   @PostMapping({"/member/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<ClientUserIdParam> clientUserIdParamList) {
      this.clientUserService.delete(clientUserIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取C端用户详情"
   )
   @GetMapping({"/member/detail"})
   public CommonResult<AdminClientUser> detail(@Valid ClientUserIdParam clientUserIdParam) {
      return CommonResult.data(this.clientUserService.detail(clientUserIdParam));
   }

   @Operation(
      summary = "管理员财务调整余额功能"
   )
   @PostMapping({"/member/adjust"})
   public CommonResult adminAdjustAdd(@RequestBody PaymentsAdminBalancePram paymentsAdminAddBalancePram) {
      this.clientUserService.adminAdjustAdd(paymentsAdminAddBalancePram);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改会员所在会员组"
   )
   @PostMapping({"/member/update/group"})
   public CommonResult updateMemberGroup(@RequestBody UpdateMemberGroupParam updateMemberGroupParam) {
      this.clientUserService.updateMemberGroup(updateMemberGroupParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "生成后台切换会员前台登录ticket"
   )
   @CommonLog("后台切换会员前台登录")
   @PostMapping({"/member/switch-login-ticket"})
   public CommonResult<String> createSwitchLoginTicket(@RequestBody @Valid ClientSwitchLoginTicketParam param) {
      return CommonResult.data(this.clientUserService.createSwitchLoginTicket(param));
   }
}
