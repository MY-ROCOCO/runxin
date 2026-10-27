package com.rxinyun.admin.production.modular.esc.os.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.esc.os.entity.OsClass;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassAddParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassEditParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassIdParam;
import com.rxinyun.admin.production.modular.esc.os.param.OsClassPageParam;
import com.rxinyun.admin.production.modular.esc.os.service.OsClassService;
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
   name = "操作系统类别管理控制器"
)
@RestController
@RequestMapping({"/admin/os"})
@Validated
public class OsClassController {
   @Resource
   private OsClassService osClassService;

   @Operation(
      summary = "获取操作系统类别分页"
   )
   @GetMapping({"class/page"})
   public CommonResult<Page<OsClass>> page(OsClassPageParam pageParam) {
      return CommonResult.data(this.osClassService.page(pageParam));
   }

   @Operation(
      summary = "新增操作系统类别"
   )
   @PostMapping({"/class/add"})
   public CommonResult<OsClass> add(OsClassAddParam osClassAddParam) {
      this.osClassService.add(osClassAddParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "获取操作系统类别"
   )
   @GetMapping({"/class/detail"})
   public CommonResult<OsClass> detail(@Valid OsClassIdParam osClassIdParam) {
      return CommonResult.data(this.osClassService.detail(osClassIdParam));
   }

   @Operation(
      summary = "编辑操作系统类别"
   )
   @CommonLog("编辑操作系统类别")
   @PostMapping({"/class/edit"})
   public CommonResult<String> edit(@RequestBody OsClassEditParam osClassEditParam) {
      this.osClassService.edit(osClassEditParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除操作系统类别"
   )
   @CommonLog("删除操作系统类别")
   @PostMapping({"/class/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<OsClassIdParam> osClassIdParamList) {
      this.osClassService.delete(osClassIdParamList);
      return CommonResult.ok();
   }
}
