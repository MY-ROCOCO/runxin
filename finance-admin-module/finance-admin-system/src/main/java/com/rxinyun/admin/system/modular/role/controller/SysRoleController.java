package com.rxinyun.admin.system.modular.role.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.system.modular.role.entity.SysRole;
import com.rxinyun.admin.system.modular.role.param.SysRoleAddParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleEditParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantMobileMenuParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantPermissionParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantResourceParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleGrantUserParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleIdParam;
import com.rxinyun.admin.system.modular.role.param.SysRolePageParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleSelectorRoleParam;
import com.rxinyun.admin.system.modular.role.param.SysRoleSelectorUserParam;
import com.rxinyun.admin.system.modular.role.result.SysRoleGrantResourceTreeResult;
import com.rxinyun.admin.system.modular.role.result.SysRoleOwnMobileMenuResult;
import com.rxinyun.admin.system.modular.role.result.SysRoleOwnPermissionResult;
import com.rxinyun.admin.system.modular.role.result.SysRoleOwnResourceResult;
import com.rxinyun.admin.system.modular.role.service.SysRoleService;
import com.rxinyun.admin.system.modular.user.entity.SysUser;
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
   name = "角色控制器"
)
@RestController
@Validated
public class SysRoleController {
   @Resource
   private SysRoleService sysRoleService;

   @Operation(
      summary = "获取角色分页"
   )
   @GetMapping({"/admin/sys/role/page"})
   public CommonResult<Page<SysRole>> page(SysRolePageParam sysRolePageParam) {
      return CommonResult.data(this.sysRoleService.page(sysRolePageParam));
   }

   @Operation(
      summary = "添加角色"
   )
   @CommonLog("添加角色")
   @PostMapping({"/admin/sys/role/add"})
   public CommonResult<String> add(@RequestBody @Valid SysRoleAddParam sysRoleAddParam) {
      this.sysRoleService.add(sysRoleAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑角色"
   )
   @CommonLog("编辑角色")
   @PostMapping({"/admin/sys/role/edit"})
   public CommonResult<String> edit(@RequestBody @Valid SysRoleEditParam sysRoleEditParam) {
      this.sysRoleService.edit(sysRoleEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除角色"
   )
   @CommonLog("删除角色")
   @PostMapping({"/admin/sys/role/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<SysRoleIdParam> sysRoleIdParamList) {
      this.sysRoleService.delete(sysRoleIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取角色详情"
   )
   @GetMapping({"/admin/sys/role/detail"})
   public CommonResult<SysRole> detail(@Valid SysRoleIdParam sysRoleIdParam) {
      return CommonResult.data(this.sysRoleService.detail(sysRoleIdParam));
   }

   @Operation(
      summary = "获取角色拥有资源"
   )
   @GetMapping({"/admin/sys/role/ownResource"})
   public CommonResult<SysRoleOwnResourceResult> ownResource(@Valid SysRoleIdParam sysRoleIdParam) {
      return CommonResult.data(this.sysRoleService.ownResource(sysRoleIdParam));
   }

   @Operation(
      summary = "给角色授权资源"
   )
   @CommonLog("给角色授权资源")
   @PostMapping({"/admin/sys/role/grantResource"})
   public CommonResult<String> grantResource(@RequestBody @Valid SysRoleGrantResourceParam sysRoleGrantResourceParam) {
      this.sysRoleService.grantResource(sysRoleGrantResourceParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取角色拥有移动端菜单"
   )
   @GetMapping({"/admin/sys/role/ownMobileMenu"})
   public CommonResult<SysRoleOwnMobileMenuResult> ownMobileMenu(@Valid SysRoleIdParam sysRoleIdParam) {
      return CommonResult.data(this.sysRoleService.ownMobileMenu(sysRoleIdParam));
   }

   @Operation(
      summary = "给角色授权移动端菜单"
   )
   @CommonLog("给角色授权移动端菜单")
   @PostMapping({"/admin/sys/role/grantMobileMenu"})
   public CommonResult<String> grantMobileMenu(@RequestBody @Valid SysRoleGrantMobileMenuParam sysRoleGrantMobileMenuParam) {
      this.sysRoleService.grantMobileMenu(sysRoleGrantMobileMenuParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取角色拥有权限"
   )
   @GetMapping({"/admin/sys/role/ownPermission"})
   public CommonResult<SysRoleOwnPermissionResult> ownPermission(@Valid SysRoleIdParam sysRoleIdParam) {
      return CommonResult.data(this.sysRoleService.ownPermission(sysRoleIdParam));
   }

   @Operation(
      summary = "给角色授权权限"
   )
   @CommonLog("给角色授权权限")
   @PostMapping({"/admin/sys/role/grantPermission"})
   public CommonResult<String> grantPermission(@RequestBody @Valid SysRoleGrantPermissionParam sysRoleGrantPermissionParam) {
      this.sysRoleService.grantPermission(sysRoleGrantPermissionParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取角色下的用户"
   )
   @GetMapping({"/admin/sys/role/ownUser"})
   public CommonResult<List<String>> ownUser(@Valid SysRoleIdParam sysRoleIdParam) {
      return CommonResult.data(this.sysRoleService.ownUser(sysRoleIdParam));
   }

   @Operation(
      summary = "给角色授权用户"
   )
   @CommonLog("给角色授权用户")
   @PostMapping({"/admin/sys/role/grantUser"})
   public CommonResult<String> grantUser(@RequestBody @Valid SysRoleGrantUserParam sysRoleGrantUserParam) {
      this.sysRoleService.grantUser(sysRoleGrantUserParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取资源授权树"
   )
   @GetMapping({"/admin/sys/role/resourceTreeSelector"})
   public CommonResult<List<SysRoleGrantResourceTreeResult>> resourceTreeSelector() {
      return CommonResult.data(this.sysRoleService.resourceTreeSelector());
   }

   @Operation(
      summary = "获取权限授权树"
   )
   @GetMapping({"/admin/sys/role/permissionTreeSelector"})
   public CommonResult<List<String>> permissionTreeSelector() {
      return CommonResult.data(this.sysRoleService.permissionTreeSelector());
   }

   @Operation(
      summary = "获取角色选择器"
   )
   @GetMapping({"/admin/sys/role/roleSelector"})
   public CommonResult<Page<SysRole>> roleSelector(SysRoleSelectorRoleParam sysRoleSelectorRoleParam) {
      return CommonResult.data(this.sysRoleService.roleSelector(sysRoleSelectorRoleParam));
   }

   @Operation(
      summary = "获取用户选择器"
   )
   @GetMapping({"/admin/sys/role/userSelector"})
   public CommonResult<Page<SysUser>> userSelector(SysRoleSelectorUserParam sysRoleSelectorUserParam) {
      return CommonResult.data(this.sysRoleService.userSelector(sysRoleSelectorUserParam));
   }
}
