package com.rxinyun.admin.production.modular.dns.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.production.modular.dns.entity.DnsLineGroup;
import com.rxinyun.admin.production.modular.dns.entity.DnsNode;
import com.rxinyun.admin.production.modular.dns.entity.DnsNodeCallbackLog;
import com.rxinyun.admin.production.modular.dns.entity.DnsNodeTask;
import com.rxinyun.admin.production.modular.dns.entity.DnsPackage;
import com.rxinyun.admin.production.modular.dns.entity.DnsPackageInstance;
import com.rxinyun.admin.production.modular.dns.param.DnsAdminParam;
import com.rxinyun.admin.production.modular.dns.service.DnsProductionService;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeInstallCommandResult;
import com.rxinyun.admin.production.modular.dns.vo.DnsNodeTokenResult;
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
   name = "DNS服务生产管理"
)
@RestController
@RequestMapping({"/admin/dns"})
@Validated
public class DnsProductionController {
   @Resource
   private DnsProductionService dnsProductionService;

   @Operation(
      summary = "DNS节点分页"
   )
   @GetMapping({"/node/page"})
   public CommonResult<Page<DnsNode>> nodePage(DnsAdminParam.BasePageParam pageParam) {
      return CommonResult.data(this.dnsProductionService.nodePage(pageParam));
   }

   @Operation(
      summary = "新增DNS节点"
   )
   @PostMapping({"/node/add"})
   public CommonResult<DnsNodeTokenResult> addNode(@RequestBody @Valid DnsAdminParam.NodeAddParam addParam) {
      return CommonResult.data(this.dnsProductionService.addNode(addParam));
   }

   @Operation(
      summary = "编辑DNS节点"
   )
   @CommonLog("编辑DNS节点")
   @PostMapping({"/node/edit"})
   public CommonResult<String> editNode(@RequestBody @Valid DnsAdminParam.NodeEditParam editParam) {
      this.dnsProductionService.editNode(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "DNS节点详情"
   )
   @GetMapping({"/node/detail"})
   public CommonResult<DnsNode> nodeDetail(@Valid DnsAdminParam.IdParam idParam) {
      return CommonResult.data(this.dnsProductionService.nodeDetail(idParam));
   }

   @Operation(
      summary = "删除DNS节点"
   )
   @CommonLog("删除DNS节点")
   @PostMapping({"/node/delete"})
   public CommonResult<String> deleteNode(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DnsAdminParam.IdParam> idParams) {
      this.dnsProductionService.deleteNode(idParams);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改DNS节点状态"
   )
   @CommonLog("修改DNS节点状态")
   @PostMapping({"/node/status"})
   public CommonResult<String> updateNodeStatus(@RequestBody @Valid DnsAdminParam.StatusParam statusParam) {
      this.dnsProductionService.updateNodeStatus(statusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "重置DNS节点API Token"
   )
   @PostMapping({"/node/resetApiToken"})
   public CommonResult<DnsNodeTokenResult> resetApiToken(@RequestBody @Valid DnsAdminParam.TokenResetParam resetParam) {
      return CommonResult.data(this.dnsProductionService.resetApiToken(resetParam));
   }

   @Operation(
      summary = "重置DNS节点回调Token"
   )
   @PostMapping({"/node/resetCallbackToken"})
   public CommonResult<DnsNodeTokenResult> resetCallbackToken(@RequestBody @Valid DnsAdminParam.TokenResetParam resetParam) {
      return CommonResult.data(this.dnsProductionService.resetCallbackToken(resetParam));
   }

   @Operation(
      summary = "查看DNS节点安装命令"
   )
   @PostMapping({"/node/installCommand"})
   public CommonResult<DnsNodeInstallCommandResult> installCommand(@RequestBody @Valid DnsAdminParam.IdParam idParam) {
      return CommonResult.data(this.dnsProductionService.installCommand(idParam));
   }

   @Operation(
      summary = "DNS自动化任务分页"
   )
   @GetMapping({"/task/page"})
   public CommonResult<Page<DnsNodeTask>> nodeTaskPage(DnsAdminParam.NodeTaskPageParam pageParam) {
      return CommonResult.data(this.dnsProductionService.nodeTaskPage(pageParam));
   }

   @Operation(
      summary = "DNS自动化回调日志分页"
   )
   @GetMapping({"/task/callback/page"})
   public CommonResult<Page<DnsNodeCallbackLog>> nodeCallbackLogPage(DnsAdminParam.NodeCallbackLogPageParam pageParam) {
      return CommonResult.data(this.dnsProductionService.nodeCallbackLogPage(pageParam));
   }

   @Operation(
      summary = "DNS线路分组分页"
   )
   @GetMapping({"/line-group/page"})
   public CommonResult<Page<DnsLineGroup>> lineGroupPage(DnsAdminParam.BasePageParam pageParam) {
      return CommonResult.data(this.dnsProductionService.lineGroupPage(pageParam));
   }

   @Operation(
      summary = "新增DNS线路分组"
   )
   @CommonLog("新增DNS线路分组")
   @PostMapping({"/line-group/add"})
   public CommonResult<String> addLineGroup(@RequestBody @Valid DnsAdminParam.LineGroupAddParam addParam) {
      this.dnsProductionService.addLineGroup(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑DNS线路分组"
   )
   @CommonLog("编辑DNS线路分组")
   @PostMapping({"/line-group/edit"})
   public CommonResult<String> editLineGroup(@RequestBody @Valid DnsAdminParam.LineGroupEditParam editParam) {
      this.dnsProductionService.editLineGroup(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "DNS线路分组详情"
   )
   @GetMapping({"/line-group/detail"})
   public CommonResult<DnsLineGroup> lineGroupDetail(@Valid DnsAdminParam.IdParam idParam) {
      return CommonResult.data(this.dnsProductionService.lineGroupDetail(idParam));
   }

   @Operation(
      summary = "删除DNS线路分组"
   )
   @CommonLog("删除DNS线路分组")
   @PostMapping({"/line-group/delete"})
   public CommonResult<String> deleteLineGroup(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DnsAdminParam.IdParam> idParams) {
      this.dnsProductionService.deleteLineGroup(idParams);
      return CommonResult.ok();
   }

   @Operation(
      summary = "DNS套餐分页"
   )
   @GetMapping({"/package/page"})
   public CommonResult<Page<DnsPackage>> packagePage(DnsAdminParam.BasePageParam pageParam) {
      return CommonResult.data(this.dnsProductionService.packagePage(pageParam));
   }

   @Operation(
      summary = "新增DNS套餐"
   )
   @CommonLog("新增DNS套餐")
   @PostMapping({"/package/add"})
   public CommonResult<String> addPackage(@RequestBody @Valid DnsAdminParam.PackageAddParam addParam) {
      this.dnsProductionService.addPackage(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑DNS套餐"
   )
   @CommonLog("编辑DNS套餐")
   @PostMapping({"/package/edit"})
   public CommonResult<String> editPackage(@RequestBody @Valid DnsAdminParam.PackageEditParam editParam) {
      this.dnsProductionService.editPackage(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "DNS套餐详情"
   )
   @GetMapping({"/package/detail"})
   public CommonResult<DnsPackage> packageDetail(@Valid DnsAdminParam.IdParam idParam) {
      return CommonResult.data(this.dnsProductionService.packageDetail(idParam));
   }

   @Operation(
      summary = "删除DNS套餐"
   )
   @CommonLog("删除DNS套餐")
   @PostMapping({"/package/delete"})
   public CommonResult<String> deletePackage(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DnsAdminParam.IdParam> idParams) {
      this.dnsProductionService.deletePackage(idParams);
      return CommonResult.ok();
   }

   @Operation(
      summary = "修改DNS套餐状态"
   )
   @CommonLog("修改DNS套餐状态")
   @PostMapping({"/package/status"})
   public CommonResult<String> updatePackageStatus(@RequestBody @Valid DnsAdminParam.StatusParam statusParam) {
      this.dnsProductionService.updatePackageStatus(statusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "DNS套餐实例分页"
   )
   @GetMapping({"/package-instance/page"})
   public CommonResult<Page<DnsPackageInstance>> packageInstancePage(DnsAdminParam.PackageInstancePageParam pageParam) {
      return CommonResult.data(this.dnsProductionService.packageInstancePage(pageParam));
   }

   @Operation(
      summary = "手工新增DNS套餐实例"
   )
   @CommonLog("手工新增DNS套餐实例")
   @PostMapping({"/package-instance/add"})
   public CommonResult<String> addPackageInstance(@RequestBody @Valid DnsAdminParam.PackageInstanceAddParam addParam) {
      this.dnsProductionService.addPackageInstance(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑DNS套餐实例"
   )
   @CommonLog("编辑DNS套餐实例")
   @PostMapping({"/package-instance/edit"})
   public CommonResult<String> editPackageInstance(@RequestBody @Valid DnsAdminParam.PackageInstanceEditParam editParam) {
      this.dnsProductionService.editPackageInstance(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "DNS套餐实例详情"
   )
   @GetMapping({"/package-instance/detail"})
   public CommonResult<DnsPackageInstance> packageInstanceDetail(@Valid DnsAdminParam.IdParam idParam) {
      return CommonResult.data(this.dnsProductionService.packageInstanceDetail(idParam));
   }

   @Operation(
      summary = "删除DNS套餐实例"
   )
   @CommonLog("删除DNS套餐实例")
   @PostMapping({"/package-instance/delete"})
   public CommonResult<String> deletePackageInstance(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DnsAdminParam.IdParam> idParams) {
      this.dnsProductionService.deletePackageInstance(idParams);
      return CommonResult.ok();
   }
}
