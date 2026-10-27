package com.rxinyun.admin.workorder.modular.workorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.workorder.modular.workorder.entity.WorkOrderType;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.TypeRelevanceParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.TypeStaticParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderTypeAddParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderTypeEditParam;
import com.rxinyun.admin.workorder.modular.workorder.entity.param.WorkOrderTypePageParam;
import com.rxinyun.admin.workorder.modular.workorder.service.WorkOrderTypeService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "工单类型管理控制器"
)
@RestController
@RequestMapping({"/admin/workorder/type"})
public class WorkOrderTypeController {
   @Resource
   private WorkOrderTypeService workOrderTypeService;

   @CommonLog("新增工单类型")
   @Operation(
      summary = "新增工单类型"
   )
   @PostMapping({"/add"})
   public CommonResult add(@RequestBody WorkOrderTypeAddParam workOrderTypeAddParam) {
      this.workOrderTypeService.save(workOrderTypeAddParam);
      return CommonResult.ok();
   }

   @CommonLog("工单分页类型查询")
   @Operation(
      summary = "工单分页类型查询"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<WorkOrderType>> page(WorkOrderTypePageParam workOrderTypePageParam) {
      return CommonResult.data(this.workOrderTypeService.page(workOrderTypePageParam));
   }

   @CommonLog("修改工单是否关联产品")
   @Operation(
      summary = "修改工单是否关联产品"
   )
   @PostMapping({"/isRelevance"})
   public CommonResult isRelevance(@RequestBody TypeRelevanceParam typeRelevanceParam) {
      return this.workOrderTypeService.isRelevnce(typeRelevanceParam);
   }

   @CommonLog("修改工单是否可见")
   @Operation(
      summary = "修改工单是否可见"
   )
   @PostMapping({"/isVisible"})
   public CommonResult isVisible(@RequestBody TypeStaticParam typeStaticParam) {
      return this.workOrderTypeService.isVisible(typeStaticParam);
   }

   @CommonLog("修改工单类型")
   @Operation(
      summary = "修改工单类型"
   )
   @PostMapping({"/edit"})
   public CommonResult edit(@RequestBody WorkOrderTypeEditParam workOrderTypeEditParam) {
      return this.workOrderTypeService.update(workOrderTypeEditParam);
   }

   @CommonLog("删除工单类型")
   @Operation(
      summary = "删除工单类型"
   )
   @DeleteMapping({"/delete/{id}"})
   public CommonResult delete(@PathVariable @NotBlank String id) {
      return this.workOrderTypeService.delete(id);
   }
}
