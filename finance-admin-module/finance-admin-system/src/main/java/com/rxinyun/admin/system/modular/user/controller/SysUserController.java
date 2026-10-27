package com.rxinyun.admin.system.modular.user.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
import com.rxinyun.admin.system.modular.user.param.SysUserAddParam;
import com.rxinyun.admin.system.modular.user.param.SysUserAdminUpdatePwdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserEditParam;
import com.rxinyun.admin.system.modular.user.param.SysUserExportParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantPermissionParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantResourceParam;
import com.rxinyun.admin.system.modular.user.param.SysUserGrantRoleParam;
import com.rxinyun.admin.system.modular.user.param.SysUserIdParam;
import com.rxinyun.admin.system.modular.user.param.SysUserPageParam;
import com.rxinyun.admin.system.modular.user.param.SysUserResetPasswordParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorRoleParam;
import com.rxinyun.admin.system.modular.user.param.SysUserSelectorUserParam;
import com.rxinyun.admin.system.modular.user.result.SysUserOwnPermissionResult;
import com.rxinyun.admin.system.modular.user.result.SysUserOwnResourceResult;
import com.rxinyun.admin.system.modular.user.service.SysUserService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(
   name = "用户控制器"
)
@RestController
@Validated
public class SysUserController {
   @Resource
   private SysUserService sysUserService;

   @Operation(
      summary = "获取用户分页"
   )
   @GetMapping({"/admin/sys/user/page"})
   public CommonResult<Page<SysUser>> page(SysUserPageParam sysUserPageParam) {
      return CommonResult.data(this.sysUserService.page(sysUserPageParam));
   }

   @Operation(
      summary = "添加用户"
   )
   @CommonLog("添加用户")
   @PostMapping({"/admin/sys/user/add"})
   public CommonResult<String> add(@RequestBody @Valid SysUserAddParam sysUserAddParam) {
      this.sysUserService.add(sysUserAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑用户"
   )
   @CommonLog("编辑用户")
   @PostMapping({"/admin/sys/user/edit"})
   public CommonResult<String> edit(@RequestBody @Valid SysUserEditParam sysUserEditParam) {
      this.sysUserService.edit(sysUserEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除用户"
   )
   @CommonLog("删除用户")
   @PostMapping({"/admin/sys/user/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<SysUserIdParam> sysUserIdParamList) {
      this.sysUserService.delete(sysUserIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取用户详情"
   )
   @GetMapping({"/admin/sys/user/detail"})
   public CommonResult<SysUser> detail(@Valid SysUserIdParam sysUserIdParam) {
      return CommonResult.data(this.sysUserService.detail(sysUserIdParam));
   }

   @Operation(
      summary = "禁用用户"
   )
   @CommonLog("禁用用户")
   @PostMapping({"/admin/sys/user/disableUser"})
   public CommonResult<String> disableUser(@RequestBody SysUserIdParam sysUserIdParam) {
      this.sysUserService.disableUser(sysUserIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "启用用户"
   )
   @CommonLog("启用用户")
   @PostMapping({"/admin/sys/user/enableUser"})
   public CommonResult<String> enableUser(@RequestBody @Valid SysUserIdParam sysUserIdParam) {
      this.sysUserService.enableUser(sysUserIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "重置用户密码"
   )
   @CommonLog("重置用户密码")
   @PostMapping({"/admin/sys/user/resetPassword"})
   public CommonResult<String> resetPassword(@RequestBody @Valid SysUserResetPasswordParam sysUserResetPasswordParam) {
      this.sysUserService.resetPassword(sysUserResetPasswordParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取用户拥有角色"
   )
   @GetMapping({"/admin/sys/user/ownRole"})
   public CommonResult<List<String>> ownRole(@Valid SysUserIdParam sysUserIdParam) {
      return CommonResult.data(this.sysUserService.ownRole(sysUserIdParam));
   }

   @Operation(
      summary = "给用户授权角色"
   )
   @CommonLog("给用户授权角色")
   @PostMapping({"/admin/sys/user/grantRole"})
   public CommonResult<String> grantRole(@RequestBody @Valid SysUserGrantRoleParam sysUserGrantRoleParam) {
      this.sysUserService.grantRole(sysUserGrantRoleParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取用户拥有资源"
   )
   @GetMapping({"/admin/sys/user/ownResource"})
   public CommonResult<SysUserOwnResourceResult> ownResource(@Valid SysUserIdParam sysUserIdParam) {
      return CommonResult.data(this.sysUserService.ownResource(sysUserIdParam));
   }

   @Operation(
      summary = "给用户授权资源"
   )
   @CommonLog("给用户授权资源")
   @PostMapping({"/admin/sys/user/grantResource"})
   public CommonResult<String> grantResource(@RequestBody @Valid SysUserGrantResourceParam sysUserGrantResourceParam) {
      this.sysUserService.grantResource(sysUserGrantResourceParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取用户拥有权限"
   )
   @GetMapping({"/admin/sys/user/ownPermission"})
   public CommonResult<SysUserOwnPermissionResult> ownPermission(@Valid SysUserIdParam sysUserIdParam) {
      return CommonResult.data(this.sysUserService.ownPermission(sysUserIdParam));
   }

   @Operation(
      summary = "给用户授权权限"
   )
   @CommonLog("给用户授权权限")
   @PostMapping({"/admin/sys/user/grantPermission"})
   public CommonResult<String> grantPermission(@RequestBody @Valid SysUserGrantPermissionParam sysUserGrantPermissionParam) {
      this.sysUserService.grantPermission(sysUserGrantPermissionParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "下载用户导入模板"
   )
   @CommonLog("下载用户导入模板")
   @GetMapping(
      value = {"/admin/sys/user/downloadImportUserTemplate"},
      produces = {"application/octet-stream"}
   )
   public void downloadImportUserTemplate(HttpServletResponse response) throws IOException {
      this.sysUserService.downloadImportUserTemplate(response);
   }

   @Operation(
      summary = "用户导入"
   )
   @CommonLog("用户导入")
   @PostMapping({"/admin/sys/user/import"})
   public CommonResult<JSONObject> importUser(@RequestPart("file") MultipartFile file) {
      return CommonResult.data(this.sysUserService.importUser(file));
   }

   @Operation(
      summary = "用户导出"
   )
   @CommonLog("用户导出")
   @GetMapping(
      value = {"/admin/sys/user/export"},
      produces = {"application/octet-stream"}
   )
   public void exportUser(SysUserExportParam sysUserExportParam, HttpServletResponse response) throws IOException {
      this.sysUserService.exportUser(sysUserExportParam, response);
   }

   @Operation(
      summary = "导出用户个人信息"
   )
   @CommonLog("导出用户个人信息")
   @GetMapping(
      value = {"/admin/sys/user/exportUserInfo"},
      produces = {"application/octet-stream"}
   )
   public void exportUserInfo(SysUserIdParam sysUserIdParam, HttpServletResponse response) throws IOException {
      this.sysUserService.exportUserInfo(sysUserIdParam, response);
   }

   @Operation(
      summary = "获取角色选择器"
   )
   @GetMapping({"/admin/sys/user/roleSelector"})
   public CommonResult<Page<SysRole>> roleSelector(SysUserSelectorRoleParam sysUserSelectorRoleParam) {
      return CommonResult.data(this.sysUserService.roleSelector(sysUserSelectorRoleParam));
   }

   @Operation(
      summary = "获取用户选择器"
   )
   @GetMapping({"/admin/sys/user/userSelector"})
   public CommonResult<Page<SysUser>> userSelector(SysUserSelectorUserParam sysUserSelectorUserParam) {
      return CommonResult.data(this.sysUserService.userSelector(sysUserSelectorUserParam));
   }

   @Operation(
      summary = "修改密码"
   )
   @PostMapping({"/admin/sys/user/updatePassword"})
   public CommonResult<String> updatePassword(@RequestBody @Valid SysUserAdminUpdatePwdParam sysUserAdminUpdatePwdParam) {
      this.sysUserService.updatePassword(sysUserAdminUpdatePwdParam);
      return CommonResult.ok();
   }
}
