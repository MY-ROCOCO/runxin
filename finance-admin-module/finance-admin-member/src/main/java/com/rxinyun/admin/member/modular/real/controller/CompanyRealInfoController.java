package com.rxinyun.admin.member.modular.real.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.admin.member.modular.real.entity.CompanyRealInfo;
import com.rxinyun.admin.member.modular.real.param.CompanyRealInfoAuditParam;
import com.rxinyun.admin.member.modular.real.param.CompanyRealInfoIdParam;
import com.rxinyun.admin.member.modular.real.param.CompanyRealInfoPageParam;
import com.rxinyun.admin.member.modular.real.service.CompanyRealInfoService;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.enums.CertificationStatusEnum;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.member.api.CompanyRealApi;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "企业实名认证控制器"
)
@RestController
@RequestMapping({"/admin/real"})
@Validated
public class CompanyRealInfoController {
   @Resource
   private CompanyRealInfoService companyRealInfoService;
   @Resource
   private CompanyRealApi companyRealApi;

   @Operation(
      summary = "获取企业实名认证分页"
   )
   @GetMapping({"/company/page"})
   public CommonResult<Page<CompanyRealInfo>> page(CompanyRealInfoPageParam pageParam) {
      return CommonResult.data(this.companyRealInfoService.page(pageParam));
   }

   @Operation(
      summary = "获取企业实名认证详情"
   )
   @GetMapping({"/company/detail"})
   public CommonResult<CompanyRealInfo> detail(@Valid CompanyRealInfoIdParam companyRealInfoIdParam) {
      return CommonResult.data(this.companyRealInfoService.detail(companyRealInfoIdParam));
   }

   @Operation(
      summary = "企业实名认证状态审核"
   )
   @CommonLog("企业实名认证状态审核")
   @PostMapping({"/company/audit"})
   public CommonResult audit(@RequestBody @Valid CompanyRealInfoAuditParam auditParam) {
      String status = auditParam.getStatus();
      return !CertificationStatusEnum.APPROVED.getKey().equals(status) && !CertificationStatusEnum.REJECTED.getKey().equals(status)
         ? CommonResult.error("审核状态只能为 APPROVED 或 REJECTED")
         : this.companyRealApi.updateByStatus(auditParam.getMemberNo(), status, auditParam.getReason());
   }
}
