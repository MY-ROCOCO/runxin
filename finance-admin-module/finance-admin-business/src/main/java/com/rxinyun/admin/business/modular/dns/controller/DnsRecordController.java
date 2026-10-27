package com.rxinyun.admin.business.modular.dns.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.business.modular.dns.entity.DnsRecord;
import com.rxinyun.admin.business.modular.dns.param.DnsRecordParam;
import com.rxinyun.admin.business.modular.dns.service.DnsRecordService;
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
   name = "DNS解析记录管理"
)
@RestController
@RequestMapping({"/admin/dns/record"})
@Validated
public class DnsRecordController {
   @Resource
   private DnsRecordService dnsRecordService;

   @Operation(
      summary = "DNS解析记录分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<DnsRecord>> page(DnsRecordParam.PageParam pageParam) {
      return CommonResult.data(this.dnsRecordService.page(pageParam));
   }

   @Operation(
      summary = "DNS解析记录详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<DnsRecord> detail(@Valid DnsRecordParam.IdParam idParam) {
      return CommonResult.data(this.dnsRecordService.detail(idParam));
   }

   @Operation(
      summary = "修改DNS解析记录启用状态"
   )
   @CommonLog("修改DNS解析记录启用状态")
   @PostMapping({"/status"})
   public CommonResult<String> updateStatus(@RequestBody @Valid DnsRecordParam.StatusParam statusParam) {
      this.dnsRecordService.updateStatus(statusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除DNS解析记录"
   )
   @CommonLog("删除DNS解析记录")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<DnsRecordParam.IdParam> idParams) {
      this.dnsRecordService.delete(idParams);
      return CommonResult.ok();
   }
}
