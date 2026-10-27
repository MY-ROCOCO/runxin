package com.rxinyun.admin.production.modular.esc.available.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.available.entity.ServerAvailableRegion;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionAddParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionEditParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionEditSortParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionIdParam;
import com.rxinyun.admin.production.modular.esc.available.param.ServerAvailableRegionPageParam;
import com.rxinyun.admin.production.modular.esc.available.service.ServerAvailableRegionService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "服务器可用区域管理控制器"
)
@RestController
@RequestMapping({"/admin"})
@Validated
public class ServerAvailableRegionController {
   @Resource
   private ServerAvailableRegionService serverAvailableRegionService;

   @Operation(
      summary = "获取可用区域分页"
   )
   @GetMapping({"/available/page"})
   public CommonResult<Page<ServerAvailableRegion>> page(ServerAvailableRegionPageParam pageParam) {
      return CommonResult.data(this.serverAvailableRegionService.page(pageParam));
   }

   @Operation(
      summary = "新增可用区域"
   )
   @PostMapping({"/available/add"})
   public CommonResult<ServerAvailableRegion> add(ServerAvailableRegionAddParam serverAvailableRegionAddParam) {
      this.serverAvailableRegionService.add(serverAvailableRegionAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取可用区域详情"
   )
   @GetMapping({"/available/detail"})
   public CommonResult<ServerAvailableRegion> detail(@Valid ServerAvailableRegionIdParam serverAvailableRegionIdParam) {
      return CommonResult.data(this.serverAvailableRegionService.detail(serverAvailableRegionIdParam));
   }

   @Operation(
      summary = "编辑可用区域"
   )
   @CommonLog("编辑可用区域")
   @PostMapping({"/available/edit"})
   public CommonResult<String> edit(@RequestBody ServerAvailableRegionEditParam serverAvailableRegionEditParam) {
      this.serverAvailableRegionService.edit(serverAvailableRegionEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除可用区域"
   )
   @CommonLog("删除可用区域")
   @PostMapping({"/available/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<ServerAvailableRegionIdParam> serverAvailableRegionIdParamList) {
      this.serverAvailableRegionService.delete(serverAvailableRegionIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改排序"
   )
   @CommonLog("修改排序")
   @PostMapping({"/available/updateSort"})
   public CommonResult<String> updateSort(@RequestBody ServerAvailableRegionEditSortParam serverAvailableRegionEditSortParam) {
      this.serverAvailableRegionService.updateSort(serverAvailableRegionEditSortParam);
      return CommonResult.ok();
   }
}
