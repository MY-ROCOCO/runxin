package com.rxinyun.admin.biz.modular.position.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.biz.modular.position.entity.BizPosition;
import com.rxinyun.admin.biz.modular.position.param.BizPositionAddParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionEditParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionIdParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionPageParam;
import com.rxinyun.admin.biz.modular.position.param.BizPositionSelectorPositionParam;
import com.rxinyun.admin.biz.modular.position.service.BizPositionService;
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
   name = "岗位控制器"
)
@RestController
@Validated
public class BizPositionController {
   @Resource
   private BizPositionService bizPositionService;

   @Operation(
      summary = "获取岗位分页"
   )
   @SaCheckPermission({"/admin/biz/position/page"})
   @GetMapping({"/admin/biz/position/page"})
   public CommonResult<Page<BizPosition>> page(BizPositionPageParam bizPositionPageParam) {
      return CommonResult.data(this.bizPositionService.page(bizPositionPageParam));
   }

   @Operation(
      summary = "添加岗位"
   )
   @CommonLog("添加岗位")
   @SaCheckPermission({"/admin/biz/position/add"})
   @PostMapping({"/admin/biz/position/add"})
   public CommonResult<String> add(@RequestBody @Valid BizPositionAddParam bizPositionAddParam) {
      this.bizPositionService.add(bizPositionAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑岗位"
   )
   @CommonLog("编辑岗位")
   @SaCheckPermission({"/admin/biz/position/edit"})
   @PostMapping({"/admin/biz/position/edit"})
   public CommonResult<String> edit(@RequestBody @Valid BizPositionEditParam bizPositionEditParam) {
      this.bizPositionService.edit(bizPositionEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除岗位"
   )
   @CommonLog("删除岗位")
   @SaCheckPermission({"/admin/biz/position/delete"})
   @PostMapping({"/admin/biz/position/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<BizPositionIdParam> bizPositionIdParamList) {
      this.bizPositionService.delete(bizPositionIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取岗位详情"
   )
   @SaCheckPermission({"/admin/biz/position/detail"})
   @GetMapping({"/admin/biz/position/detail"})
   public CommonResult<BizPosition> detail(@Valid BizPositionIdParam bizPositionIdParam) {
      return CommonResult.data(this.bizPositionService.detail(bizPositionIdParam));
   }

   @Operation(
      summary = "获取组织树选择器"
   )
   @SaCheckPermission({"/admin/biz/position/orgTreeSelector"})
   @GetMapping({"/admin/biz/position/orgTreeSelector"})
   public CommonResult<List<Tree<String>>> orgTreeSelector() {
      return CommonResult.data(this.bizPositionService.orgTreeSelector());
   }

   @Operation(
      summary = "获取岗位选择器"
   )
   @SaCheckPermission({"/admin/biz/position/positionSelector"})
   @GetMapping({"/admin/biz/position/positionSelector"})
   public CommonResult<Page<BizPosition>> positionSelector(BizPositionSelectorPositionParam bizPositionSelectorPositionParam) {
      return CommonResult.data(this.bizPositionService.positionSelector(bizPositionSelectorPositionParam));
   }
}
