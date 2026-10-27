package com.rxinyun.admin.business.modular.dns.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.dns.entity.DnsDomain;
import com.rxinyun.admin.business.modular.dns.param.DnsDomainParam;
import com.rxinyun.admin.business.modular.dns.service.DnsDomainService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.pojo.DnsNsRecordResult;
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
   name = "DNS托管域名管理"
)
@RestController
@RequestMapping({"/admin/dns/domain"})
@Validated
public class DnsDomainController {
   @Resource
   private DnsDomainService dnsDomainService;

   @Operation(
      summary = "DNS托管域名分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<DnsDomain>> page(DnsDomainParam.PageParam pageParam) {
      return CommonResult.data(this.dnsDomainService.page(pageParam));
   }

   @Operation(
      summary = "新增DNS托管域名"
   )
   @CommonLog("新增DNS托管域名")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid DnsDomainParam.AddParam addParam) {
      this.dnsDomainService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑DNS托管域名"
   )
   @CommonLog("编辑DNS托管域名")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid DnsDomainParam.EditParam editParam) {
      this.dnsDomainService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "DNS托管域名详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<DnsDomain> detail(@Valid DnsDomainParam.IdParam idParam) {
      return CommonResult.data(this.dnsDomainService.detail(idParam));
   }

   @Operation(
      summary = "批量查询域名当前NS记录"
   )
   @PostMapping({"/ns-record"})
   public CommonResult<List<DnsNsRecordResult>> nsRecord(@RequestBody @Valid DnsDomainParam.NsRecordParam nsRecordParam) {
      return CommonResult.data(this.dnsDomainService.nsRecord(nsRecordParam));
   }

   @Operation(
      summary = "修改DNS托管域名状态"
   )
   @CommonLog("修改DNS托管域名状态")
   @PostMapping({"/status"})
   public CommonResult<String> updateStatus(@RequestBody @Valid DnsDomainParam.StatusParam statusParam) {
      this.dnsDomainService.updateStatus(statusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除DNS托管域名"
   )
   @CommonLog("删除DNS托管域名")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DnsDomainParam.IdParam> idParams) {
      this.dnsDomainService.delete(idParams);
      return CommonResult.ok();
   }
}
