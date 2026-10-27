package com.rxinyun.member.business.modular.vhost.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.enums.order.ProductTypeEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.vhost.result.VhostPhpVersion;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordAddParam;
import com.rxinyun.member.business.modular.esc.param.MemberServerTransferRecordIdParam;
import com.rxinyun.member.business.modular.esc.service.MemberServerTransferRecordService;
import com.rxinyun.member.business.modular.vhost.entity.MemberVhost;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostDomainParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostIdParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostPageParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostPasswordParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostPhpParam;
import com.rxinyun.member.business.modular.vhost.param.MemberVhostRemarkParam;
import com.rxinyun.member.business.modular.vhost.service.MemberVhostService;
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
   name = "会员端-虚拟主机"
)
@RestController
@RequestMapping({"/member/vhost/host"})
@Validated
public class MemberVhostController {
   @Resource
   private MemberVhostService memberVhostService;
   @Resource
   private MemberServerTransferRecordService memberServerTransferRecordService;

   @Operation(
      summary = "获取虚拟主机分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberVhost>> page(MemberVhostPageParam pageParam) {
      return CommonResult.data(this.memberVhostService.page(pageParam));
   }

   @Operation(
      summary = "获取虚拟主机详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberVhost> detail(@Valid MemberVhostIdParam idParam) {
      return CommonResult.data(this.memberVhostService.detail(idParam));
   }

   @Operation(
      summary = "启动虚拟主机"
   )
   @CommonLog("启动虚拟主机")
   @PostMapping({"/start"})
   public CommonResult<String> start(@RequestBody @Valid MemberVhostIdParam idParam) {
      return this.memberVhostService.start(idParam);
   }

   @Operation(
      summary = "停止虚拟主机"
   )
   @CommonLog("停止虚拟主机")
   @PostMapping({"/stop"})
   public CommonResult<String> stop(@RequestBody @Valid MemberVhostIdParam idParam) {
      return this.memberVhostService.stop(idParam);
   }

   @Operation(
      summary = "获取虚拟主机PHP版本"
   )
   @GetMapping({"/php/versions"})
   public CommonResult<List<VhostPhpVersion>> phpVersions(@Valid MemberVhostIdParam idParam) {
      return CommonResult.data(this.memberVhostService.phpVersions(idParam));
   }

   @Operation(
      summary = "切换虚拟主机PHP版本"
   )
   @CommonLog("切换虚拟主机PHP版本")
   @PostMapping({"/php/change"})
   public CommonResult<String> changePhp(@RequestBody @Valid MemberVhostPhpParam param) {
      return this.memberVhostService.changePhp(param);
   }

   @Operation(
      summary = "添加虚拟主机域名"
   )
   @CommonLog("添加虚拟主机域名")
   @PostMapping({"/domain/add"})
   public CommonResult<String> addDomain(@RequestBody @Valid MemberVhostDomainParam param) {
      return this.memberVhostService.addDomain(param);
   }

   @Operation(
      summary = "删除虚拟主机域名"
   )
   @CommonLog("删除虚拟主机域名")
   @PostMapping({"/domain/delete"})
   public CommonResult<String> deleteDomain(@RequestBody @Valid MemberVhostDomainParam param) {
      return this.memberVhostService.deleteDomain(param);
   }

   @Operation(
      summary = "修改虚拟主机FTP密码"
   )
   @CommonLog("修改虚拟主机FTP密码")
   @PostMapping({"/ftp/password"})
   public CommonResult<String> changeFtpPassword(@RequestBody @Valid MemberVhostPasswordParam param) {
      return this.memberVhostService.changeFtpPassword(param);
   }

   @Operation(
      summary = "修改虚拟主机数据库密码"
   )
   @CommonLog("修改虚拟主机数据库密码")
   @PostMapping({"/database/password"})
   public CommonResult<String> changeDbPassword(@RequestBody @Valid MemberVhostPasswordParam param) {
      return this.memberVhostService.changeDbPassword(param);
   }

   @Operation(
      summary = "用户编辑备注"
   )
   @CommonLog("用户编辑虚拟主机备注")
   @PostMapping({"/editRemark"})
   public CommonResult<String> editRemark(@RequestBody @Valid MemberVhostRemarkParam param) {
      this.memberVhostService.editRemark(param);
      return CommonResult.ok("修改备注成功");
   }

   @Operation(
      summary = "用户虚拟主机过户"
   )
   @CommonLog("用户虚拟主机过户")
   @PostMapping({"/plus"})
   public CommonResult plus(@RequestBody MemberServerTransferRecordAddParam param) {
      param.setProductType(ProductTypeEnum.VHOST.getCode());
      return this.memberServerTransferRecordService.add(param);
   }

   @Operation(
      summary = "转让虚拟主机 通过/拒绝"
   )
   @CommonLog("转让虚拟主机 通过/拒绝")
   @PostMapping({"/updateStatus"})
   public CommonResult updateStatus(@RequestBody MemberServerTransferRecordIdParam param) {
      return this.memberServerTransferRecordService.updateStatus(param);
   }
}
