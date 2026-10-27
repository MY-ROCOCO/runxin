package com.rxinyun.admin.production.modular.vhost.version.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.vhost.version.entity.VhostVersion;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionAddParam;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionEditParam;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionIdParam;
import com.rxinyun.admin.production.modular.vhost.version.param.VhostVersionPageParam;
import com.rxinyun.admin.production.modular.vhost.version.service.VhostVersionService;
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
   name = "虚拟主机版本管理"
)
@RestController
@RequestMapping({"/admin/vhost/version"})
@Validated
public class VhostVersionController {
   @Resource
   private VhostVersionService vhostVersionService;

   @Operation(
      summary = "虚拟主机版本分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<VhostVersion>> page(VhostVersionPageParam pageParam) {
      return CommonResult.data(this.vhostVersionService.page(pageParam));
   }

   @Operation(
      summary = "新增虚拟主机版本"
   )
   @CommonLog("新增虚拟主机版本")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid VhostVersionAddParam addParam) {
      this.vhostVersionService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑虚拟主机版本"
   )
   @CommonLog("编辑虚拟主机版本")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid VhostVersionEditParam editParam) {
      this.vhostVersionService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "虚拟主机版本详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<VhostVersion> detail(@Valid VhostVersionIdParam idParam) {
      return CommonResult.data(this.vhostVersionService.detail(idParam));
   }

   @Operation(
      summary = "删除虚拟主机版本"
   )
   @CommonLog("删除虚拟主机版本")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<VhostVersionIdParam> idParamList) {
      this.vhostVersionService.delete(idParamList);
      return CommonResult.ok();
   }
}
