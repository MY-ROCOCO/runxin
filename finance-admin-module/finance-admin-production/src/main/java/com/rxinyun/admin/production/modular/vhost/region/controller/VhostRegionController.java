package com.rxinyun.admin.production.modular.vhost.region.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.vhost.region.entity.VhostRegion;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionAddParam;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionEditParam;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionIdParam;
import com.rxinyun.admin.production.modular.vhost.region.param.VhostRegionPageParam;
import com.rxinyun.admin.production.modular.vhost.region.service.VhostRegionService;
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
   name = "虚拟主机地域管理"
)
@RestController
@RequestMapping({"/admin/vhost/region"})
@Validated
public class VhostRegionController {
   @Resource
   private VhostRegionService vhostRegionService;

   @Operation(
      summary = "虚拟主机地域分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<VhostRegion>> page(VhostRegionPageParam pageParam) {
      return CommonResult.data(this.vhostRegionService.page(pageParam));
   }

   @Operation(
      summary = "新增虚拟主机地域"
   )
   @CommonLog("新增虚拟主机地域")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid VhostRegionAddParam addParam) {
      this.vhostRegionService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑虚拟主机地域"
   )
   @CommonLog("编辑虚拟主机地域")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid VhostRegionEditParam editParam) {
      this.vhostRegionService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "虚拟主机地域详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<VhostRegion> detail(@Valid VhostRegionIdParam idParam) {
      return CommonResult.data(this.vhostRegionService.detail(idParam));
   }

   @Operation(
      summary = "删除虚拟主机地域"
   )
   @CommonLog("删除虚拟主机地域")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<VhostRegionIdParam> idParamList) {
      this.vhostRegionService.delete(idParamList);
      return CommonResult.ok();
   }

   @Operation(
      summary = "虚拟主机地域下拉"
   )
   @GetMapping({"/selector"})
   public CommonResult<List<VhostRegion>> selector() {
      return CommonResult.data(this.vhostRegionService.selector());
   }
}
