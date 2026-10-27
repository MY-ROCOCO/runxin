package com.rxinyun.admin.production.modular.independent.os.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.independent.os.entity.IndependentOsVersion;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionAddParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionEditParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionIdParam;
import com.rxinyun.admin.production.modular.independent.os.param.OsVersionPageParam;
import com.rxinyun.admin.production.modular.independent.os.service.OsVersionService;
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
   name = "独立服务器操作系统版本管理控制器"
)
@RestController
@RequestMapping({"/admin/independent/os/version"})
@Validated
public class OsIndependentVersionController {
   @Resource
   private OsVersionService osIndependentVersionService;

   @Operation(
      summary = "获取操作系统版本分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<IndependentOsVersion>> page(OsVersionPageParam pageParam) {
      return CommonResult.data(this.osIndependentVersionService.page(pageParam));
   }

   @Operation(
      summary = "新增操作系统版本"
   )
   @PostMapping({"/add"})
   public CommonResult<IndependentOsVersion> add(@Valid OsVersionAddParam osVersionAddParam) {
      this.osIndependentVersionService.add(osVersionAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取操作系统版本"
   )
   @GetMapping({"/detail"})
   public CommonResult<IndependentOsVersion> detail(@Valid OsVersionIdParam osVersionIdParam) {
      return CommonResult.data(this.osIndependentVersionService.detail(osVersionIdParam));
   }

   @Operation(
      summary = "编辑操作系统版本"
   )
   @CommonLog("编辑操作系统版本")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@Valid OsVersionEditParam osVersionEditParam) {
      this.osIndependentVersionService.edit(osVersionEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除操作操作系统版本"
   )
   @CommonLog("删除操作系统版本")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<OsVersionIdParam> osVersionIdParamList) {
      this.osIndependentVersionService.delete(osVersionIdParamList);
      return CommonResult.ok();
   }
}
