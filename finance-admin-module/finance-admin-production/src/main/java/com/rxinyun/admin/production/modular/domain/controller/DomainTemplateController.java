package com.rxinyun.admin.production.modular.domain.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rxinyun.common.pojo.CommonResult;
import com.rxinyun.production.api.DomainTemplateManageApi;
import com.rxinyun.production.api.dto.DomainTemplateSubmitResultVO;
import com.rxinyun.production.api.dto.DomainTemplateVO;
import com.rxinyun.production.api.param.AdminDomainTemplatePageParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(
   name = "域名模板控制器"
)
@RestController
@RequestMapping({"/admin/domain/template"})
@Validated
public class DomainTemplateController {
   @Resource
   private DomainTemplateManageApi domainTemplateManageApi;

   @Operation(
      summary = "域名模板信息分页"
   )
   @GetMapping({"/page"})
   public CommonResult<Page<DomainTemplateVO>> page(AdminDomainTemplatePageParam pageParam) {
      return CommonResult.data(this.domainTemplateManageApi.page(pageParam));
   }

   @Operation(
      summary = "域名模板详情"
   )
   @GetMapping({"/detail"})
   public CommonResult<DomainTemplateVO> detail(@Parameter(description = "模板ID",required = true) @RequestParam String id) {
      return CommonResult.data(this.domainTemplateManageApi.getDetail(id));
   }

   @Operation(
      summary = "提交域名模板审核"
   )
   @PostMapping({"/submit"})
   public CommonResult<DomainTemplateSubmitResultVO> submit(@Parameter(description = "模板ID",required = true) @RequestParam String id) {
      return CommonResult.data(this.domainTemplateManageApi.submit(id));
   }

   @Operation(
      summary = "删除域名模板"
   )
   @PostMapping({"/delete"})
   public CommonResult<String> delete(@Parameter(description = "模板ID",required = true) @RequestParam String id) {
      this.domainTemplateManageApi.delete(id);
      return CommonResult.ok();
   }
}
