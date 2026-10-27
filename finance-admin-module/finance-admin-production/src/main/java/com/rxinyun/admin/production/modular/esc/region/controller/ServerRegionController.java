package com.rxinyun.admin.production.modular.esc.region.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.region.entity.ServerRegion;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionAddParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionEditParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionEditSortParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionIdParam;
import com.rxinyun.admin.production.modular.esc.region.param.ServerRegionPageParam;
import com.rxinyun.admin.production.modular.esc.region.service.ServerRegionService;
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
   name = "服务器地域管理控制器"
)
@RestController
@RequestMapping({"/admin"})
@Validated
public class ServerRegionController {
   @Resource
   private ServerRegionService serverRegionService;

   @Operation(
      summary = "获取地域分页"
   )
   @GetMapping({"/region/page"})
   public CommonResult<Page<ServerRegion>> page(ServerRegionPageParam pageParam) {
      return CommonResult.data(this.serverRegionService.page(pageParam));
   }

   @Operation(
      summary = "新增地域"
   )
   @PostMapping({"/region/add"})
   public CommonResult<ServerRegion> add(ServerRegionAddParam serverRegionAddParam) {
      this.serverRegionService.add(serverRegionAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取地域"
   )
   @GetMapping({"/region/detail"})
   public CommonResult<ServerRegion> detail(@Valid ServerRegionIdParam serverRegionIdParam) {
      return CommonResult.data(this.serverRegionService.detail(serverRegionIdParam));
   }

   @Operation(
      summary = "编辑地域"
   )
   @CommonLog("编辑地域")
   @PostMapping({"/region/edit"})
   public CommonResult<String> edit(@RequestBody ServerRegionEditParam serverRegionEditParam) {
      this.serverRegionService.edit(serverRegionEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除地域"
   )
   @CommonLog("删除地域")
   @PostMapping({"/region/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<ServerRegionIdParam> serverRegionIdParamList) {
      this.serverRegionService.delete(serverRegionIdParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改排序"
   )
   @CommonLog("修改排序")
   @PostMapping({"/region/updateSort"})
   public CommonResult<String> updateSort(@RequestBody ServerRegionEditSortParam serverRegionEditSortParam) {
      this.serverRegionService.updateSort(serverRegionEditSortParam);
      return CommonResult.ok();
   }
}
