package com.rxinyun.admin.system.modular.resource.controller;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.system.modular.resource.entity.SysMenu;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuAddParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuEditParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuIdParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuPageParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuSelectorMenuParam;
import com.rxinyun.admin.system.modular.resource.param.menu.SysMenuTreeParam;
import com.rxinyun.admin.system.modular.resource.service.SysMenuService;
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
   name = "菜单控制器"
)
@RestController
@Validated
public class SysMenuController {
   @Resource
   private SysMenuService sysMenuService;

   @Operation(
      summary = "获取菜单分页"
   )
   @GetMapping({"/admin/sys/menu/page"})
   public CommonResult<Page<SysMenu>> page(SysMenuPageParam sysMenuPageParam) {
      return CommonResult.data(this.sysMenuService.page(sysMenuPageParam));
   }

   @Operation(
      summary = "获取菜单树"
   )
   @GetMapping({"/admin/sys/menu/tree"})
   public CommonResult<List<Tree<String>>> tree(SysMenuTreeParam sysMenuTreeParam) {
      return CommonResult.data(this.sysMenuService.tree(sysMenuTreeParam));
   }

   @Operation(
      summary = "添加菜单"
   )
   @CommonLog("添加菜单")
   @PostMapping({"/admin/sys/menu/add"})
   public CommonResult<String> add(@RequestBody @Valid SysMenuAddParam sysMenuAddParam) {
      this.sysMenuService.add(sysMenuAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑菜单"
   )
   @CommonLog("编辑菜单")
   @PostMapping({"/admin/sys/menu/edit"})
   public CommonResult<String> edit(@RequestBody @Valid SysMenuEditParam sysMenuEditParam) {
      this.sysMenuService.edit(sysMenuEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除菜单"
   )
   @CommonLog("删除菜单")
   @PostMapping({"/admin/sys/menu/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<SysMenuIdParam> sysMenuIdParamList) {
      this.sysMenuService.delete(sysMenuIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取菜单详情"
   )
   @GetMapping({"/admin/sys/menu/detail"})
   public CommonResult<SysMenu> detail(@Valid SysMenuIdParam sysMenuIdParam) {
      return CommonResult.data(this.sysMenuService.detail(sysMenuIdParam));
   }

   @Operation(
      summary = "获取菜单树选择器"
   )
   @GetMapping({"/admin/sys/menu/menuTreeSelector"})
   public CommonResult<List<Tree<String>>> menuTreeSelector(SysMenuSelectorMenuParam sysMenuSelectorMenuParam) {
      return CommonResult.data(this.sysMenuService.menuTreeSelector(sysMenuSelectorMenuParam));
   }
}
