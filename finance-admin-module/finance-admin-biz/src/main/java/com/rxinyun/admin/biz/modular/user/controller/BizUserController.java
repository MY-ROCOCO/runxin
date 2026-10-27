package com.rxinyun.admin.biz.modular.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.biz.modular.org.entity.BizOrg;
import com.rxinyun.admin.biz.modular.position.entity.BizPosition;
import com.rxinyun.admin.biz.modular.user.entity.BizUser;
import com.rxinyun.admin.biz.modular.user.param.BizUserAddParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserEditParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserExportParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserGrantRoleParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserIdParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserPageParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorOrgListParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorPositionParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorRoleParam;
import com.rxinyun.admin.biz.modular.user.param.BizUserSelectorUserParam;
import com.rxinyun.admin.biz.modular.user.result.BizUserRoleResult;
import com.rxinyun.admin.biz.modular.user.service.BizUserService;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "人员控制器"
)
@RestController
@Validated
public class BizUserController {
   @Resource
   private BizUserService bizUserService;

   @Operation(
      summary = "获取人员分页"
   )
   @SaCheckPermission({"/admin/biz/user/page"})
   @GetMapping({"/admin/biz/user/page"})
   public CommonResult<Page<BizUser>> page(BizUserPageParam bizUserPageParam) {
      return CommonResult.data(this.bizUserService.page(bizUserPageParam));
   }

   @Operation(
      summary = "添加人员"
   )
   @CommonLog("添加人员")
   @SaCheckPermission({"/admin/biz/user/add"})
   @PostMapping({"/admin/biz/user/add"})
   public CommonResult<String> add(@RequestBody @Valid BizUserAddParam bizUserAddParam) {
      this.bizUserService.add(bizUserAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑人员"
   )
   @CommonLog("编辑人员")
   @SaCheckPermission({"/admin/biz/user/edit"})
   @PostMapping({"/admin/biz/user/edit"})
   public CommonResult<String> edit(@RequestBody @Valid BizUserEditParam bizUserEditParam) {
      this.bizUserService.edit(bizUserEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除人员"
   )
   @CommonLog("删除人员")
   @SaCheckPermission({"/admin/biz/user/delete"})
   @PostMapping({"/admin/biz/user/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<BizUserIdParam> bizUserIdParamList) {
      this.bizUserService.delete(bizUserIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取人员详情"
   )
   @SaCheckPermission({"/admin/biz/user/detail"})
   @GetMapping({"/admin/biz/user/detail"})
   public CommonResult<BizUser> detail(@Valid BizUserIdParam bizUserIdParam) {
      return CommonResult.data(this.bizUserService.detail(bizUserIdParam));
   }

   @Operation(
      summary = "禁用人员"
   )
   @CommonLog("禁用人员")
   @SaCheckPermission({"/admin/biz/user/disableUser"})
   @PostMapping({"/admin/biz/user/disableUser"})
   public CommonResult<String> disableUser(@RequestBody BizUserIdParam bizUserIdParam) {
      this.bizUserService.disableUser(bizUserIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "启用人员"
   )
   @CommonLog("启用人员")
   @SaCheckPermission({"/admin/biz/user/enableUser"})
   @PostMapping({"/admin/biz/user/enableUser"})
   public CommonResult<String> enableUser(@RequestBody @Valid BizUserIdParam bizUserIdParam) {
      this.bizUserService.enableUser(bizUserIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "重置人员密码"
   )
   @CommonLog("重置人员密码")
   @SaCheckPermission({"/admin/biz/user/resetPassword"})
   @PostMapping({"/admin/biz/user/resetPassword"})
   public CommonResult<String> resetPassword(@RequestBody @Valid BizUserIdParam bizUserIdParam) {
      this.bizUserService.resetPassword(bizUserIdParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取人员拥有角色"
   )
   @SaCheckPermission({"/admin/biz/user/ownRole"})
   @GetMapping({"/admin/biz/user/ownRole"})
   public CommonResult<List<String>> ownRole(@Valid BizUserIdParam bizUserIdParam) {
      return CommonResult.data(this.bizUserService.ownRole(bizUserIdParam));
   }

   @Operation(
      summary = "给人员授权角色"
   )
   @CommonLog("给人员授权角色")
   @SaCheckPermission({"/admin/biz/user/grantRole"})
   @PostMapping({"/admin/biz/user/grantRole"})
   public CommonResult<String> grantRole(@RequestBody @Valid BizUserGrantRoleParam bizUserGrantRoleParam) {
      this.bizUserService.grantRole(bizUserGrantRoleParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "人员导出"
   )
   @CommonLog("人员导出")
   @SaCheckPermission({"/admin/biz/user/export"})
   @GetMapping(
      value = {"/admin/biz/user/export"},
      produces = {"application/octet-stream"}
   )
   public void exportUser(BizUserExportParam bizUserExportParam, HttpServletResponse response) throws IOException {
      this.bizUserService.exportUser(bizUserExportParam, response);
   }

   @Operation(
      summary = "导出人员个人信息"
   )
   @CommonLog("导出人员个人信息")
   @SaCheckPermission({"/admin/biz/user/exportUserInfo"})
   @GetMapping(
      value = {"/admin/biz/user/exportUserInfo"},
      produces = {"application/octet-stream"}
   )
   public void exportUserInfo(BizUserIdParam bizUserIdParam, HttpServletResponse response) throws IOException {
      this.bizUserService.exportUserInfo(bizUserIdParam, response);
   }

   @Operation(
      summary = "获取机构树选择器"
   )
   @SaCheckPermission({"/admin/biz/user/orgTreeSelector"})
   @GetMapping({"/admin/biz/user/orgTreeSelector"})
   public CommonResult<List<Tree<String>>> orgTreeSelector() {
      return CommonResult.data(this.bizUserService.orgTreeSelector());
   }

   @Operation(
      summary = "获取机构列表选择器"
   )
   @SaCheckPermission({"/admin/biz/user/orgListSelector"})
   @GetMapping({"/admin/biz/user/orgListSelector"})
   public CommonResult<Page<BizOrg>> orgListSelector(BizUserSelectorOrgListParam bizUserSelectorOrgListParam) {
      return CommonResult.data(this.bizUserService.orgListSelector(bizUserSelectorOrgListParam));
   }

   @Operation(
      summary = "获取岗位选择器"
   )
   @SaCheckPermission({"/admin/biz/user/positionSelector"})
   @GetMapping({"/admin/biz/user/positionSelector"})
   public CommonResult<Page<BizPosition>> positionSelector(BizUserSelectorPositionParam bizUserSelectorPositionParam) {
      return CommonResult.data(this.bizUserService.positionSelector(bizUserSelectorPositionParam));
   }

   @Operation(
      summary = "获取角色选择器"
   )
   @SaCheckPermission({"/admin/biz/user/roleSelector"})
   @GetMapping({"/admin/biz/user/roleSelector"})
   public CommonResult<Page<BizUserRoleResult>> roleSelector(BizUserSelectorRoleParam bizUserSelectorRoleParam) {
      return CommonResult.data(this.bizUserService.roleSelector(bizUserSelectorRoleParam));
   }

   @Operation(
      summary = "获取人员选择器"
   )
   @SaCheckPermission({"/admin/biz/user/userSelector"})
   @GetMapping({"/admin/biz/user/userSelector"})
   public CommonResult<Page<BizUser>> userSelector(BizUserSelectorUserParam bizUserSelectorUserParam) {
      return CommonResult.data(this.bizUserService.userSelector(bizUserSelectorUserParam));
   }
}
