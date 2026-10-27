package com.rxinyun.admin.production.modular.vhost.master.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.vhost.master.entity.VhostMaster;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostControlledVersionResult;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterAddParam;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterEditParam;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterIdParam;
import com.rxinyun.admin.production.modular.vhost.master.param.VhostMasterPageParam;
import com.rxinyun.admin.production.modular.vhost.master.service.VhostMasterService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.vhost.result.VhostNetWork;
import com.rxinyun.common.vhost.result.VhostSystemTotal;
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
   name = "虚拟主机主控管理"
)
@RestController
@RequestMapping({"/admin/vhost/master"})
@Validated
public class VhostMasterController {
   @Resource
   private VhostMasterService vhostMasterService;

   @Operation(
      summary = "虚拟主机主控分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<VhostMaster>> page(VhostMasterPageParam pageParam) {
      return CommonResult.data(this.vhostMasterService.page(pageParam));
   }

   @Operation(
      summary = "受控版本选项，当前仅宝塔虚拟主机"
   )
   @GetMapping({"/controlledVersion"})
   public CommonResult<List<VhostControlledVersionResult>> controlledVersion() {
      return CommonResult.data(this.vhostMasterService.controlledVersions());
   }

   @Operation(
      summary = "新增虚拟主机主控"
   )
   @CommonLog("新增虚拟主机主控")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid VhostMasterAddParam addParam) {
      this.vhostMasterService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑虚拟主机主控"
   )
   @CommonLog("编辑虚拟主机主控")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid VhostMasterEditParam editParam) {
      this.vhostMasterService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "虚拟主机主控详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<VhostMaster> detail(@Valid VhostMasterIdParam idParam) {
      return CommonResult.data(this.vhostMasterService.detail(idParam));
   }

   @Operation(
      summary = "获取主控系统基础统计"
   )
   @GetMapping({"/systemTotal"})
   public CommonResult<VhostSystemTotal> systemTotal(@Valid VhostMasterIdParam idParam) {
      return CommonResult.data(this.vhostMasterService.systemTotal(idParam));
   }

   @Operation(
      summary = "获取主控实时状态（CPU、内存、网络）"
   )
   @GetMapping({"/netWork"})
   public CommonResult<VhostNetWork> netWork(@Valid VhostMasterIdParam idParam) {
      return CommonResult.data(this.vhostMasterService.netWork(idParam));
   }

   @Operation(
      summary = "删除虚拟主机主控"
   )
   @CommonLog("删除虚拟主机主控")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<VhostMasterIdParam> idParamList) {
      this.vhostMasterService.delete(idParamList);
      return CommonResult.ok();
   }
}
