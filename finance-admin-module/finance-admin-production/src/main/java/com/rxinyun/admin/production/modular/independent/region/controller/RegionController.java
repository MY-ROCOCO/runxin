package com.rxinyun.admin.production.modular.independent.region.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.independent.region.model.dto.RegionDTO;
import com.rxinyun.admin.production.modular.independent.region.model.dto.RegionPageParam;
import com.rxinyun.admin.production.modular.independent.region.model.entity.Region;
import com.rxinyun.admin.production.modular.independent.region.model.vo.RegionVO;
import com.rxinyun.admin.production.modular.independent.region.service.impl.RegionServiceImpl;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立服务器地域管理控制器"
)
@RestController
@RequestMapping({"/admin/independent/region"})
@Validated
public class RegionController {
   @Resource
   RegionServiceImpl regionService;

   @Operation(
      summary = "添加地域"
   )
   @CommonLog("添加地域")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid RegionDTO regionDTO) {
      this.regionService.add(regionDTO);
      return CommonResult.ok();
   }

   @Operation(
      summary = "根据id获取地域详情"
   )
   @CommonLog("根据id获取地域详情")
   @GetMapping({"/searchRegionById/{id}"})
   public CommonResult<Region> searchRegionById(@Parameter(description = "地域ID") @NotBlank @PathVariable String id) {
      return CommonResult.data(this.regionService.searchRegionById(id));
   }

   @Operation(
      summary = "编辑地域"
   )
   @CommonLog("编辑地域")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid RegionDTO regionDTO) {
      this.regionService.edit(regionDTO);
      return CommonResult.ok();
   }

   @Operation(
      summary = "根据id删除地域"
   )
   @CommonLog("删除地域")
   @DeleteMapping({"/delete/{id}"})
   public CommonResult<String> delete(@Parameter(description = "地域ID") @NotBlank @PathVariable("id") String id) {
      this.regionService.delete(id);
      return CommonResult.ok();
   }

   @Operation(
      summary = "下拉框选择地域"
   )
   @CommonLog("下拉框选择地域")
   @GetMapping({"/subduct"})
   public CommonResult<List<Region>> subduct() {
      return this.regionService.subduct();
   }

   @Operation(
      summary = "获取地域分页"
   )
   @CommonLog("获取地域分页")
   @GetMapping({"/page"})
   public CommonResult<Page<RegionVO>> page(RegionPageParam pageParam) {
      return CommonResult.data(this.regionService.page(pageParam));
   }
}
