package com.rxinyun.admin.production.modular.independent.os.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsClass;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassAddParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassEditParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassIdParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsClassPageParam;
import com.rxinyun.admin.production.modular.independent.os.service.OsClassService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "独立服务器操作系统类别管理控制器"
)
@RestController
@RequestMapping({"/admin/independent/os/class"})
@Validated
public class OsIndependentClassController {
   @Resource
   private OsClassService osIndependentClassService;

   @Operation(
      summary = "获取操作系统类别分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<IndependentOsClass>> page(OsClassPageParam pageParam) {
      return CommonResult.data(this.osIndependentClassService.page(pageParam));
   }

   @Operation(
      summary = "新增操作系统类别"
   )
   @PostMapping({"/add"})
   public CommonResult<IndependentOsClass> add(@Valid OsClassAddParam osClassAddParam) {
      this.osIndependentClassService.add(osClassAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取操作系统类别"
   )
   @GetMapping({"/detail"})
   public CommonResult<IndependentOsClass> detail(@Valid OsClassIdParam osClassIdParam) {
      return CommonResult.data(this.osIndependentClassService.detail(osClassIdParam));
   }

   @Operation(
      summary = "编辑操作系统类别"
   )
   @CommonLog("编辑操作系统类别")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@Valid @RequestBody OsClassEditParam osClassEditParam) {
      this.osIndependentClassService.edit(osClassEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除独立服务器操作系统类别集合"
   )
   @CommonLog("删除独立服务器操作系统类别")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<OsClassIdParam> osClassIdParamList) {
      this.osIndependentClassService.delete(osClassIdParamList);
      return CommonResult.ok();
   }
}
