package com.rxinyun.member.auth.modular.real.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.auth.modular.real.entity.CompanyRealInfo;
import com.rxinyun.member.auth.modular.real.param.CompanyRealInfoAddOrUpdateParam;
import com.rxinyun.member.auth.modular.real.param.CompanyRealInfoIdParam;
import com.rxinyun.member.auth.modular.real.param.CompanyRealInfoPageParam;
import com.rxinyun.member.auth.modular.real.service.MemberCompanyRealInfoService;
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
   name = "企业实名认证控制器"
)
@RestController
@RequestMapping({"/member/real"})
@Validated
public class MemberCompanyRealInfoController {
   @Resource
   private MemberCompanyRealInfoService memberCompanyRealInfoService;

   @Operation(
      summary = "获取企业实名认证分页"
   )
   @GetMapping({"/company/page"})
   public CommonResult<Page<CompanyRealInfo>> page(CompanyRealInfoPageParam pageParam) {
      return CommonResult.data(this.memberCompanyRealInfoService.page(pageParam));
   }

   @Operation(
      summary = "新增企业实名认证"
   )
   @CommonLog("新增企业实名认证")
   @PostMapping({"/company/add"})
   public CommonResult add(@RequestBody CompanyRealInfoAddOrUpdateParam companyRealInfoAddParam) {
      return this.memberCompanyRealInfoService.addOrUpdate(companyRealInfoAddParam);
   }

   @Operation(
      summary = "新增或更新企业实名认证"
   )
   @CommonLog("新增或更新企业实名认证")
   @PostMapping({"/company/addOrUpdate"})
   public CommonResult addOrUpdate(@RequestBody CompanyRealInfoAddOrUpdateParam companyRealInfoAddParam) {
      return this.memberCompanyRealInfoService.addOrUpdate(companyRealInfoAddParam);
   }

   @Operation(
      summary = "获取企业实名认证详情"
   )
   @GetMapping({"/company/detail"})
   public CommonResult<CompanyRealInfo> detail(@Valid CompanyRealInfoIdParam companyRealInfoIdParam) {
      return CommonResult.data(this.memberCompanyRealInfoService.detail(companyRealInfoIdParam));
   }

   @Operation(
      summary = "删除企业实名认证"
   )
   @CommonLog("删除企业实名认证")
   @PostMapping({"/company/delete"})
   public CommonResult<String> delete(@RequestBody @Valid @NotEmpty(message = "集合不能为空") List<CompanyRealInfoIdParam> companyRealInfoIdParamList) {
      this.memberCompanyRealInfoService.delete(companyRealInfoIdParamList);
      return CommonResult.ok();
   }
}
