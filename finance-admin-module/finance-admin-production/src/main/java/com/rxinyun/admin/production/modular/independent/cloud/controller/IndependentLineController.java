package com.rxinyun.admin.production.modular.independent.cloud.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.independent.cloud.entity.IndependentLine;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLineAddParam;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLineEditParam;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLinePageParam;
import com.rxinyun.admin.production.modular.independent.cloud.param.IndependentLinePageVO;
import com.rxinyun.admin.production.modular.independent.cloud.param.ServerLocalLineUpdateDiscountTemplateParam;
import com.rxinyun.admin.production.modular.independent.cloud.service.IndependentLineService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立线路管理控制器"
)
@RestController
@RequestMapping({"/admin/independent"})
public class IndependentLineController {
   @Resource
   private IndependentLineService independentLineService;

   @CommonLog("添加线路")
   @Operation(
      summary = "添加线路"
   )
   @PostMapping({"/line/add"})
   public CommonResult add(@RequestBody IndependentLineAddParam independentLineParam) {
      if (independentLineParam.getYearNumber() <= 0) {
         return CommonResult.error("有效期无效");
      } else {
         this.independentLineService.add(independentLineParam);
         return CommonResult.ok();
      }
   }

   @CommonLog("获取线路")
   @Operation(
      summary = "获取线路"
   )
   @GetMapping({"/line/detail/{id}"})
   public CommonResult<IndependentLine> detail(@PathVariable @NotBlank String id) {
      return CommonResult.data(this.independentLineService.detail(id));
   }

   @CommonLog("修改线路")
   @Operation(
      summary = "修改线路"
   )
   @PostMapping({"/line/edit"})
   public CommonResult edit(@RequestBody IndependentLineEditParam independentLineParam) {
      this.independentLineService.update(independentLineParam);
      return CommonResult.ok();
   }

   @CommonLog("删除线路")
   @Operation(
      summary = "删除线路"
   )
   @DeleteMapping({"/line/delete/{id}"})
   public CommonResult delete(@PathVariable @NotBlank String id) {
      this.independentLineService.delete(id);
      return CommonResult.ok();
   }

   @CommonLog("线路分页查询")
   @Operation(
      summary = "线路分页查询"
   )
   @GetMapping({"/line/page"})
   public CommonResult<Page<IndependentLinePageVO>> page(IndependentLinePageParam independentLinePageParam) {
      return CommonResult.data(this.independentLineService.page(independentLinePageParam));
   }

   @CommonLog("查询线路名字")
   @Operation(
      summary = "下拉框查询线路"
   )
   @GetMapping({"/line/name"})
   public CommonResult<List<IndependentLine>> getLineName() {
      return CommonResult.data(this.independentLineService.getLineName());
   }

   @Operation(
      summary = "线路修改折扣模板"
   )
   @CommonLog("线路修改折扣模板")
   @PostMapping({"/line/updateTemplate"})
   public CommonResult<String> updateDiscountTemplate(@RequestBody ServerLocalLineUpdateDiscountTemplateParam serverLocalLineUpdateTemplateParam) {
      this.independentLineService.updateDiscountTemplate(serverLocalLineUpdateTemplateParam);
      return CommonResult.ok();
   }
}
