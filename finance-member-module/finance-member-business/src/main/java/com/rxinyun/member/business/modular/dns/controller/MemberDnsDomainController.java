package com.rxinyun.member.business.modular.dns.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.common.pojo.DnsNsRecordResult;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsDomain;
import com.rxinyun.member.business.modular.dns.param.MemberDnsDomainParam;
import com.rxinyun.member.business.modular.dns.service.MemberDnsDomainService;
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
   name = "会员端-DNS托管域名"
)
@RestController
@RequestMapping({"/member/dns/domain"})
@Validated
public class MemberDnsDomainController {
   @Resource
   private MemberDnsDomainService memberDnsDomainService;

   @Operation(
      summary = "DNS托管域名分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberDnsDomain>> page(MemberDnsDomainParam.PageParam pageParam) {
      return CommonResult.data(this.memberDnsDomainService.page(pageParam));
   }

   @Operation(
      summary = "新增DNS托管域名"
   )
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid MemberDnsDomainParam.AddParam addParam) {
      this.memberDnsDomainService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "DNS托管域名详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberDnsDomain> detail(@Valid MemberDnsDomainParam.IdParam idParam) {
      return CommonResult.data(this.memberDnsDomainService.detail(idParam));
   }

   @Operation(
      summary = "批量查询域名当前NS记录"
   )
   @PostMapping({"/ns-record"})
   public CommonResult<List<DnsNsRecordResult>> nsRecord(@RequestBody @Valid MemberDnsDomainParam.NsRecordParam nsRecordParam) {
      return CommonResult.data(this.memberDnsDomainService.nsRecord(nsRecordParam));
   }

   @Operation(
      summary = "删除DNS托管域名"
   )
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<MemberDnsDomainParam.IdParam> idParams) {
      this.memberDnsDomainService.delete(idParams);
      return CommonResult.ok();
   }
}
