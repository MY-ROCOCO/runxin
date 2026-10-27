package com.rxinyun.admin.business.modular.vhost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordAddParam;
import com.rxinyun.admin.business.modular.esc.param.ServerTransferRecordIdParam;
import com.rxinyun.admin.business.modular.esc.service.ServerTransferRecordService;
import com.rxinyun.admin.business.modular.vhost.entity.AdminVhost;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostDomainParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostIdParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostPageParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostPasswordParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostPhpParam;
import com.rxinyun.admin.business.modular.vhost.param.AdminVhostRemarkParam;
import com.rxinyun.admin.business.modular.vhost.service.AdminVhostService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.vhost.result.VhostPhpVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "后台虚拟主机产品"
)
@RestController
@RequestMapping({"/admin/vhost/host"})
@Validated
public class AdminVhostController {
   @Resource
   private AdminVhostService adminVhostService;
   @Resource
   private ServerTransferRecordService serverTransferRecordService;

   @Operation(
      summary = "虚拟主机产品分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<AdminVhost>> page(AdminVhostPageParam pageParam) {
      return CommonResult.data(this.adminVhostService.page(pageParam));
   }

   @Operation(
      summary = "虚拟主机产品详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<AdminVhost> detail(@Valid AdminVhostIdParam idParam) {
      return CommonResult.data(this.adminVhostService.detail(idParam));
   }

   @Operation(
      summary = "启动虚拟主机"
   )
   @CommonLog("启动虚拟主机")
   @PostMapping({"/start"})
   public CommonResult<String> start(@RequestBody @Valid AdminVhostIdParam idParam) {
      return this.adminVhostService.start(idParam);
   }

   @Operation(
      summary = "停止虚拟主机"
   )
   @CommonLog("停止虚拟主机")
   @PostMapping({"/stop"})
   public CommonResult<String> stop(@RequestBody @Valid AdminVhostIdParam idParam) {
      return this.adminVhostService.stop(idParam);
   }

   @Operation(
      summary = "获取虚拟主机PHP版本"
   )
   @GetMapping({"/php/versions"})
   public CommonResult<List<VhostPhpVersion>> phpVersions(@Valid AdminVhostIdParam idParam) {
      return CommonResult.data(this.adminVhostService.phpVersions(idParam));
   }

   @Operation(
      summary = "切换虚拟主机PHP版本"
   )
   @CommonLog("切换虚拟主机PHP版本")
   @PostMapping({"/php/change"})
   public CommonResult<String> changePhp(@RequestBody @Valid AdminVhostPhpParam param) {
      return this.adminVhostService.changePhp(param);
   }

   @Operation(
      summary = "添加虚拟主机域名"
   )
   @CommonLog("添加虚拟主机域名")
   @PostMapping({"/domain/add"})
   public CommonResult<String> addDomain(@RequestBody @Valid AdminVhostDomainParam param) {
      return this.adminVhostService.addDomain(param);
   }

   @Operation(
      summary = "删除虚拟主机域名"
   )
   @CommonLog("删除虚拟主机域名")
   @PostMapping({"/domain/delete"})
   public CommonResult<String> deleteDomain(@RequestBody @Valid AdminVhostDomainParam param) {
      return this.adminVhostService.deleteDomain(param);
   }

   @Operation(
      summary = "修改虚拟主机FTP密码"
   )
   @CommonLog("修改虚拟主机FTP密码")
   @PostMapping({"/ftp/password"})
   public CommonResult<String> changeFtpPassword(@RequestBody @Valid AdminVhostPasswordParam param) {
      return this.adminVhostService.changeFtpPassword(param);
   }

   @Operation(
      summary = "修改虚拟主机数据库密码"
   )
   @CommonLog("修改虚拟主机数据库密码")
   @PostMapping({"/database/password"})
   public CommonResult<String> changeDbPassword(@RequestBody @Valid AdminVhostPasswordParam param) {
      return this.adminVhostService.changeDbPassword(param);
   }

   @Operation(
      summary = "编辑虚拟主机备注（管理员）"
   )
   @CommonLog("编辑虚拟主机备注")
   @PostMapping({"/editRemark"})
   public CommonResult<String> editRemark(@RequestBody @Valid AdminVhostRemarkParam param) {
      this.adminVhostService.editRemark(param);
      return CommonResult.ok("修改备注成功");
   }

   @Operation(
      summary = "虚拟主机过户申请"
   )
   @CommonLog("虚拟主机过户")
   @PostMapping({"/plus"})
   public CommonResult plus(@RequestBody ServerTransferRecordAddParam param) {
      param.setProductType(ProductTypeEnum.VHOST.getCode());
      return this.serverTransferRecordService.add(param);
   }

   @Operation(
      summary = "虚拟主机过户审核"
   )
   @CommonLog("虚拟主机过户审核")
   @PostMapping({"/updateStatus"})
   public CommonResult updateStatus(@RequestBody ServerTransferRecordIdParam param) {
      return this.serverTransferRecordService.updateStatus(param);
   }
}
