package com.rxinyun.admin.production.modular.esc.os.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.os.entity.OsVersion;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionAddParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionEditParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionIdParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsVersionPageParam;
import com.rxinyun.admin.production.modular.esc.os.service.OsVersionService;
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
   name = "操作系统版本管理控制器"
)
@RestController
@RequestMapping({"/admin/os"})
@Validated
public class OsVersionController {
   @Resource
   private OsVersionService osVersionService;

   @Operation(
      summary = "获取操作系统版本分页"
   )
   @GetMapping({"version/page"})
   public CommonResult<Page<OsVersion>> page(OsVersionPageParam pageParam) {
      return CommonResult.data(this.osVersionService.page(pageParam));
   }

   @Operation(
      summary = "新增操作系统版本"
   )
   @PostMapping({"/version/add"})
   public CommonResult<OsVersion> add(OsVersionAddParam osVersionAddParam) {
      this.osVersionService.add(osVersionAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取操作系统版本"
   )
   @GetMapping({"/version/detail"})
   public CommonResult<OsVersion> detail(@Valid OsVersionIdParam osVersionIdParam) {
      return CommonResult.data(this.osVersionService.detail(osVersionIdParam));
   }

   @Operation(
      summary = "编辑操作系统类别"
   )
   @CommonLog("编辑操作系统版本")
   @PostMapping({"/version/edit"})
   public CommonResult<String> edit(@RequestBody OsVersionEditParam osVersionEditParam) {
      this.osVersionService.edit(osVersionEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除操作操作系统版本"
   )
   @CommonLog("删除操作系统版本")
   @PostMapping({"/version/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<OsVersionIdParam> osVersionIdParamList) {
      this.osVersionService.delete(osVersionIdParamList);
      return CommonResult.ok();
   }
}
