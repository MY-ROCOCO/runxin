package com.rxinyun.member.business.modular.dns.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.business.modular.dns.entity.MemberDnsRecord;
import com.rxinyun.member.business.modular.dns.param.MemberDnsRecordParam;
import com.rxinyun.member.business.modular.dns.service.MemberDnsRecordService;
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
   name = "会员端-DNS解析记录"
)
@RestController
@RequestMapping({"/member/dns/record"})
@Validated
public class MemberDnsRecordController {
   @Resource
   private MemberDnsRecordService memberDnsRecordService;

   @Operation(
      summary = "DNS解析记录分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<MemberDnsRecord>> page(MemberDnsRecordParam.PageParam pageParam) {
      return CommonResult.data(this.memberDnsRecordService.page(pageParam));
   }

   @Operation(
      summary = "新增DNS解析记录"
   )
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid MemberDnsRecordParam.AddParam addParam) {
      this.memberDnsRecordService.add(addParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑DNS解析记录"
   )
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid MemberDnsRecordParam.EditParam editParam) {
      this.memberDnsRecordService.edit(editParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "DNS解析记录详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<MemberDnsRecord> detail(@Valid MemberDnsRecordParam.IdParam idParam) {
      return CommonResult.data(this.memberDnsRecordService.detail(idParam));
   }

   @Operation(
      summary = "修改DNS解析记录启用状态"
   )
   @PostMapping({"/status"})
   public CommonResult<String> updateStatus(@RequestBody @Valid MemberDnsRecordParam.StatusParam statusParam) {
      this.memberDnsRecordService.updateStatus(statusParam);
      return CommonResult.ok();
   }

   @Operation(
      summary = "删除DNS解析记录"
   )
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<MemberDnsRecordParam.IdParam> idParams) {
      this.memberDnsRecordService.delete(idParams);
      return CommonResult.ok();
   }
}
