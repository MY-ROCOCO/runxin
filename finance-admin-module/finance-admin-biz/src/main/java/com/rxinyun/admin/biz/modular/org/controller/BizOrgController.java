package com.rxinyun.admin.biz.modular.org.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.biz.modular.org.entity.BizOrg;
import com.rxinyun.admin.biz.modular.org.param.BizOrgAddParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgEditParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgIdParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgPageParam;
import com.rxinyun.admin.biz.modular.org.param.BizOrgSelectorUserParam;
import com.rxinyun.admin.biz.modular.org.service.BizOrgService;
import com.rxinyun.admin.biz.modular.user.entity.BizUser;
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
   name = "机构控制器"
)
@RestController
@Validated
public class BizOrgController {
   @Resource
   private BizOrgService bizOrgService;

   @Operation(
      summary = "获取机构分页"
   )
   @SaCheckPermission({"/admin/biz/org/page"})
   @GetMapping({"/admin/biz/org/page"})
   public CommonResult<Page<BizOrg>> page(BizOrgPageParam bizOrgPageParam) {
      return CommonResult.data(this.bizOrgService.page(bizOrgPageParam));
   }

   @Operation(
      summary = "获取机构树"
   )
   @SaCheckPermission({"/admin/biz/org/tree"})
   @GetMapping({"/admin/biz/org/tree"})
   public CommonResult<List<Tree<String>>> tree() {
      return CommonResult.data(this.bizOrgService.tree());
   }

   @Operation(
      summary = "添加机构"
   )
   @CommonLog("添加机构")
   @SaCheckPermission({"/admin/biz/org/add"})
   @PostMapping({"/admin/biz/org/add"})
   public CommonResult<String> add(@RequestBody @Valid BizOrgAddParam bizOrgAddParam) {
      this.bizOrgService.add(bizOrgAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑机构"
   )
   @CommonLog("编辑机构")
   @SaCheckPermission({"/admin/biz/org/edit"})
   @PostMapping({"/admin/biz/org/edit"})
   public CommonResult<String> edit(@RequestBody @Valid BizOrgEditParam bizOrgEditParam) {
      this.bizOrgService.edit(bizOrgEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除机构"
   )
   @CommonLog("删除机构")
   @SaCheckPermission({"/admin/biz/org/delete"})
   @PostMapping({"/admin/biz/org/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<BizOrgIdParam> bizOrgIdParamList) {
      this.bizOrgService.delete(bizOrgIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取机构详情"
   )
   @SaCheckPermission({"/admin/biz/org/detail"})
   @GetMapping({"/admin/biz/org/detail"})
   public CommonResult<BizOrg> detail(@Valid BizOrgIdParam bizOrgIdParam) {
      return CommonResult.data(this.bizOrgService.detail(bizOrgIdParam));
   }

   @Operation(
      summary = "获取机构树选择器"
   )
   @SaCheckPermission({"/admin/biz/org/orgTreeSelector"})
   @GetMapping({"/admin/biz/org/orgTreeSelector"})
   public CommonResult<List<Tree<String>>> orgTreeSelector() {
      return CommonResult.data(this.bizOrgService.orgTreeSelector());
   }

   @Operation(
      summary = "获取人员选择器"
   )
   @SaCheckPermission({"/admin/biz/org/userSelector"})
   @GetMapping({"/admin/biz/org/userSelector"})
   public CommonResult<Page<BizUser>> userSelector(BizOrgSelectorUserParam bizOrgSelectorUserParam) {
      return CommonResult.data(this.bizOrgService.userSelector(bizOrgSelectorUserParam));
   }
}
