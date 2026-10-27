package com.rxinyun.member.production.modular.domain.template.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.annotation.CommonLog;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.member.production.modular.domain.template.dto.ChinaRegionsVO;
import com.rxinyun.member.production.modular.domain.template.entity.DomainTemplate;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplateAddParam;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplateEditParam;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplateIdParam;
import com.rxinyun.member.production.modular.domain.template.param.MemberDomainTemplatePageParam;
import com.rxinyun.member.production.modular.domain.template.service.MemberDomainTemplateService;
import com.rxinyun.member.production.modular.domain.template.service.XinwangSubmitResult;
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
   name = "会员端-域名模板管理"
)
@RestController
@RequestMapping({"/member/domain/template"})
@Validated
public class MemberDomainTemplateController {
   @Resource
   private MemberDomainTemplateService memberDomainTemplateService;

   @Operation(
      summary = "我的模板分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<DomainTemplate>> page(MemberDomainTemplatePageParam pageParam) {
      return CommonResult.data(this.memberDomainTemplateService.page(pageParam));
   }

   @Operation(
      summary = "创建本地模板"
   )
   @CommonLog("创建域名模板")
   @PostMapping({"/add"})
   public CommonResult<String> add(@RequestBody @Valid MemberDomainTemplateAddParam param) {
      this.memberDomainTemplateService.add(param);
      return CommonResult.ok();
   }

   @Operation(
      summary = "编辑模板"
   )
   @CommonLog("编辑域名模板")
   @PostMapping({"/edit"})
   public CommonResult<String> edit(@RequestBody @Valid MemberDomainTemplateEditParam param) {
      this.memberDomainTemplateService.edit(param);
      return CommonResult.ok();
   }

   @Operation(
      summary = "模板详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<DomainTemplate> detail(@Valid MemberDomainTemplateIdParam param) {
      return CommonResult.data(this.memberDomainTemplateService.getById(param));
   }

   @Operation(
      summary = "国内省市级联（新网 getChinaRegions，供模板表单选择与上游一致的省份、城市）"
   )
   @GetMapping({"/regions"})
   public CommonResult<ChinaRegionsVO> getChinaRegions() {
      return CommonResult.data(this.memberDomainTemplateService.getChinaRegions());
   }

   @Operation(
      summary = "提交模板到上游（新网创建模板）"
   )
   @CommonLog("提交域名模板到新网")
   @PostMapping({"/submit"})
   public CommonResult<XinwangSubmitResult> submit(@RequestBody @Valid MemberDomainTemplateIdParam param) {
      return CommonResult.data(this.memberDomainTemplateService.submitToUpstream(param));
   }

   @Operation(
      summary = "同步模板审核状态（从新网 query 拉取 verifyStatus、reason 更新本地）"
   )
   @PostMapping({"/syncStatus"})
   public CommonResult<DomainTemplate> syncStatus(@RequestBody @Valid MemberDomainTemplateIdParam param) {
      return CommonResult.data(this.memberDomainTemplateService.syncTemplateStatus(param));
   }

   @Operation(
      summary = "删除模板"
   )
   @CommonLog("删除域名模板")
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@RequestBody @Valid MemberDomainTemplateIdParam param) {
      this.memberDomainTemplateService.delete(param);
      return CommonResult.ok();
   }
}
